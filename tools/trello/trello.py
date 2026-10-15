#!/usr/bin/env python3
"""Minimal Trello CLI for the team board. Standard library only (Python >= 3.9).

Credentials (never stored in the repo):
  - env vars TRELLO_KEY and TRELLO_TOKEN, or
  - a JSON file outside the repo: ~/.trello.json -> {"key": "...", "token": "...", "board": "..."}
Board: --board <id|shortLink|URL>, or env var TRELLO_BOARD, or "board" in ~/.trello.json.

Usage:
  python tools/trello/trello.py show PAN-4.3
  python tools/trello/trello.py move PAN-4.3 "En curso"
  python tools/trello/trello.py check PAN-4.3 "texto del item" [--checklist "Integrante 2"]
  python tools/trello/trello.py comment PAN-4.3 "texto"
  python tools/trello/trello.py assign PAN-4.3
"""
from __future__ import annotations

import argparse
import json
import os
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from pathlib import Path

API = "https://api.trello.com/1"
CRED_FILE = Path.home() / ".trello.json"
MAX_RETRIES = 3


class TrelloError(Exception):
    pass


def load_config() -> dict:
    env = {name: os.environ.get(f"TRELLO_{name.upper()}") for name in ("key", "token", "board")}
    cfg = {}
    # The file is only a fallback: a broken file must not block a complete environment.
    if not all(env.values()) and CRED_FILE.is_file():
        try:
            # utf-8-sig: Windows PowerShell 5.1 writes UTF-8 files with a BOM.
            cfg = json.loads(CRED_FILE.read_text(encoding="utf-8-sig"))
        except (OSError, ValueError) as e:
            raise TrelloError(f"No pude leer {CRED_FILE}: {e.__class__.__name__}") from None
        if not isinstance(cfg, dict):
            raise TrelloError(f"{CRED_FILE} tiene que ser un objeto JSON con key, token y board.")
    values = {name: env[name] or cfg.get(name) for name in env}
    # Values pasted from Windows files often carry a trailing CR or newline.
    return {name: str(v).strip() if v else None for name, v in values.items()}


def board_ref(value: str) -> str:
    """Accept a board id, shortLink or URL and return what the API takes.

    >>> board_ref("https://trello.com/b/AbCd1234/tp3-vora")
    'AbCd1234'
    >>> board_ref("AbCd1234")
    'AbCd1234'
    """
    m = re.search(r"trello\.com/b/([A-Za-z0-9]+)", value)
    return m.group(1) if m else value.strip()


def matches_key(card_name: str, key: str) -> bool:
    """True if the card name starts with the key (optionally in brackets).

    >>> matches_key("PAN-4.3 Detalle de entrenamiento", "pan-4.3")
    True
    >>> matches_key("[NUC-02] Room", "NUC-02")
    True
    >>> matches_key("PAN-4.30 Otra", "PAN-4.3")
    False
    >>> matches_key("PAN-4.3.1 Sub", "PAN-4.3")
    False
    """
    return re.match(rf"\s*\[?{re.escape(key)}(?![\w.-])", card_name, re.IGNORECASE) is not None


class Trello:
    def __init__(self, key: str, token: str):
        # Reject values that would break the header; never echo them.
        if any(c in key + token for c in '\r\n"'):
            raise TrelloError("TRELLO_KEY o TRELLO_TOKEN tienen caracteres inválidos (¿un salto de línea o comillas?). Volvé a cargarlos.")
        # Credentials travel in a header, never in the URL, so they can't leak into error messages.
        self._auth = f'OAuth oauth_consumer_key="{key}", oauth_token="{token}"'

    def request(self, method: str, path: str, params: dict | None = None) -> object:
        url = API + path
        data = None
        if params and method == "GET":
            url += "?" + urllib.parse.urlencode(params)
        elif params:
            data = urllib.parse.urlencode(params).encode()
        for attempt in range(MAX_RETRIES + 1):
            req = urllib.request.Request(url, data=data, method=method)
            req.add_header("Authorization", self._auth)
            req.add_header("Accept", "application/json")
            try:
                with urllib.request.urlopen(req, timeout=20) as resp:
                    raw = resp.read()
            except urllib.error.HTTPError as e:
                if e.code == 429 and attempt < MAX_RETRIES:
                    # Trello rate limit: back off and retry.
                    time.sleep(float(e.headers.get("Retry-After") or 2 ** (attempt + 1)))
                    continue
                body = e.read().decode("utf-8", "replace")[:200].strip()
                raise TrelloError(http_message(e.code, path, body)) from None
            except urllib.error.URLError as e:
                raise TrelloError(f"No pude conectar con Trello ({e.reason}). Revisá la conexión o la VPN.") from None
            except (OSError, ValueError) as e:  # timeouts, resets, invalid header values: class name only
                raise TrelloError(f"Falló el pedido a Trello ({e.__class__.__name__}). Reintentá en unos segundos.") from None
            try:
                return json.loads(raw.decode("utf-8") or "null")
            except ValueError:
                raise TrelloError("Trello devolvió una respuesta que no es JSON (¿un proxy o un portal de login de la red?).") from None
        raise TrelloError("Trello sigue devolviendo 429 (rate limit). Esperá unos segundos y reintentá.")


def http_message(code: int, path: str, body: str) -> str:
    if code == 401:
        return "Trello respondió 401: la key o el token no son válidos, o el token no tiene permiso sobre el board."
    if code == 404 or (code == 400 and "invalid id" in body.lower()):
        return f"Trello no encontró el recurso ({path}). Revisá el id o link del board (TRELLO_BOARD o --board)."
    return f"Trello respondió HTTP {code} en {path}: {body}"


def find_card(api: Trello, board: str, key: str) -> dict:
    cards = api.request("GET", f"/boards/{board}/cards", {
        "fields": "name,desc,idList,idMembers,shortUrl",
        "checklists": "all",
        "attachments": "true",
        "attachment_fields": "name,url",
    })
    found = [c for c in cards if matches_key(c["name"], key)]
    if not found:
        raise TrelloError(f"No hay ninguna tarjeta abierta cuyo nombre empiece con '{key}'.")
    if len(found) > 1:
        names = "\n  ".join(c["name"] for c in found)
        raise TrelloError(f"Hay {len(found)} tarjetas que empiezan con '{key}':\n  {names}")
    return found[0]


def lists_by_id(api: Trello, board: str) -> dict:
    return {lst["id"]: lst["name"] for lst in api.request("GET", f"/boards/{board}/lists", {"fields": "name"})}


def cmd_show(api: Trello, board: str, args) -> None:
    card = find_card(api, board, args.key)
    lists = lists_by_id(api, board)
    print(f"# {card['name']}")
    print(f"Lista: {lists.get(card['idList'], '?')}")
    print(f"URL: {card['shortUrl']}")
    print(f"Asignada: {'sí' if card['idMembers'] else 'nadie'}")
    print("\n## Descripción\n")
    print(card["desc"] or "(vacía)")
    for cl in card.get("checklists", []):
        print(f"\n## {cl['name']}\n")
        for item in sorted(cl["checkItems"], key=lambda i: i["pos"]):
            print(f"- [{'x' if item['state'] == 'complete' else ' '}] {item['name']}")
    if card.get("attachments"):
        print("\n## Links\n")
        for a in card["attachments"]:
            print(f"- {a['name']}: {a['url']}")


def cmd_move(api: Trello, board: str, args) -> None:
    card = find_card(api, board, args.key)
    target = [i for i, n in lists_by_id(api, board).items() if n.strip().lower() == args.list.strip().lower()]
    if not target:
        raise TrelloError(f"No existe la lista '{args.list}' en el board.")
    api.request("PUT", f"/cards/{card['id']}", {"idList": target[0]})
    print(f"Movida '{card['name']}' a '{args.list}'.")


def cmd_check(api: Trello, board: str, args) -> None:
    card = find_card(api, board, args.key)
    text = args.item.strip().lower()
    wanted = (args.checklist or "").strip().lower()
    checklists = [cl for cl in card.get("checklists", []) if wanted in cl["name"].lower()]
    if not checklists:
        raise TrelloError(f"La tarjeta no tiene ningún checklist que contenga '{args.checklist}'.")
    items = [(cl["name"], i) for cl in checklists for i in cl["checkItems"]]
    exact = [(c, i) for c, i in items if i["name"].strip().lower() == text]
    found = exact or [(c, i) for c, i in items if text in i["name"].lower()]
    if not found:
        raise TrelloError(f"Ningún ítem del checklist contiene '{args.item}'.")
    if len(found) > 1:
        names = "\n  ".join(f"[{c}] {i['name']}" for c, i in found)
        raise TrelloError(f"El texto coincide con {len(found)} ítems; sé más específico o usá --checklist:\n  {names}")
    checklist, item = found[0]
    api.request("PUT", f"/cards/{card['id']}/checkItem/{item['id']}", {"state": "complete"})
    print(f"Tildado en '{checklist}': {item['name']}")


def cmd_comment(api: Trello, board: str, args) -> None:
    card = find_card(api, board, args.key)
    api.request("POST", f"/cards/{card['id']}/actions/comments", {"text": args.text})
    print(f"Comentario agregado en '{card['name']}'.")


def cmd_assign(api: Trello, board: str, args) -> None:
    card = find_card(api, board, args.key)
    me = api.request("GET", "/members/me", {"fields": "id,username"})
    if me["id"] in card["idMembers"]:
        print(f"Ya estabas asignado a '{card['name']}'.")
        return
    api.request("POST", f"/cards/{card['id']}/idMembers", {"value": me["id"]})
    print(f"Asignada '{card['name']}' a @{me['username']}.")


def parse_args(argv: list[str]) -> argparse.Namespace:
    p = argparse.ArgumentParser(description="CLI mínima del board de Trello del equipo.")
    p.add_argument("--board", help="id, shortLink o URL del board (por defecto: TRELLO_BOARD)")
    sub = p.add_subparsers(dest="cmd", required=True)
    s = sub.add_parser("show", help="muestra descripción, checklist y links de una tarjeta")
    s.add_argument("key")
    s.set_defaults(func=cmd_show)
    s = sub.add_parser("move", help="mueve la tarjeta a otra lista")
    s.add_argument("key")
    s.add_argument("list")
    s.set_defaults(func=cmd_move)
    s = sub.add_parser("check", help="tilda un ítem del checklist")
    s.add_argument("key")
    s.add_argument("item")
    s.add_argument("--checklist", help="nombre (o parte) del checklist, si el ítem se repite")
    s.set_defaults(func=cmd_check)
    s = sub.add_parser("comment", help="agrega un comentario")
    s.add_argument("key")
    s.add_argument("text")
    s.set_defaults(func=cmd_comment)
    s = sub.add_parser("assign", help="te asigna la tarjeta")
    s.add_argument("key")
    s.set_defaults(func=cmd_assign)
    return p.parse_args(argv)


def main(argv: list[str]) -> int:
    for stream in (sys.stdout, sys.stderr):
        stream.reconfigure(encoding="utf-8", errors="replace")  # Windows pipes default to cp1252
    args = parse_args(argv)
    try:
        cfg = load_config()
        if not cfg["key"] or not cfg["token"]:
            raise TrelloError("Faltan credenciales: definí TRELLO_KEY y TRELLO_TOKEN (ver docs/onboarding/TRELLO.md).")
        board = args.board or cfg["board"]
        if not board:
            raise TrelloError("Falta el board: definí TRELLO_BOARD o pasá --board <link del board>.")
        args.func(Trello(cfg["key"], cfg["token"]), board_ref(board), args)
    except TrelloError as e:
        print(f"Error: {e}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
