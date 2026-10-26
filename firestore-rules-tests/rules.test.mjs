// EXT-03: firestore.rules isolates the profile (users/{uid}) of two accounts.
// Runs on the Firestore emulator with a demo- project, so it never touches the real project.
// Run from the repo root: see README, "Reglas de Firestore".
import { after, before, beforeEach, describe, test } from 'node:test';
import { readFileSync } from 'node:fs';
import {
  assertFails,
  assertSucceeds,
  initializeTestEnvironment,
} from '@firebase/rules-unit-testing';
import { deleteDoc, doc, getDoc, setDoc, updateDoc } from 'firebase/firestore';

const PROJECT_ID = 'demo-vora-coach';
const ALICE = 'alice-uid';
const BOB = 'bob-uid';

const profile = (firstName) => ({ firstName, lastName: 'Morgan', heightCm: 178, weightKg: 76 });

let env;

before(async () => {
  env = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules: readFileSync(new URL('../firestore.rules', import.meta.url), 'utf8'),
      host: '127.0.0.1',
      port: 8080,
    },
  });
});

beforeEach(async () => {
  await env.clearFirestore();
  // Seeds both profiles bypassing the rules, as if each owner had saved it before.
  await env.withSecurityRulesDisabled(async (context) => {
    const db = context.firestore();
    await setDoc(doc(db, 'users', ALICE), profile('Alice'));
    await setDoc(doc(db, 'users', BOB), profile('Bob'));
  });
});

after(async () => {
  await env?.cleanup();
});

const aliceDb = () => env.authenticatedContext(ALICE).firestore();
const anonymousDb = () => env.unauthenticatedContext().firestore();

describe('account A on its own profile', () => {
  test('reads it', async () => {
    await assertSucceeds(getDoc(doc(aliceDb(), 'users', ALICE)));
  });

  test('creates it', async () => {
    await env.withSecurityRulesDisabled((context) => deleteDoc(doc(context.firestore(), 'users', ALICE)));
    await assertSucceeds(setDoc(doc(aliceDb(), 'users', ALICE), profile('Alice')));
  });

  test('updates it', async () => {
    await assertSucceeds(updateDoc(doc(aliceDb(), 'users', ALICE), { weightKg: 75 }));
  });
});

describe('account A on the profile of account B', () => {
  test('cannot read it', async () => {
    await assertFails(getDoc(doc(aliceDb(), 'users', BOB)));
  });

  test('cannot overwrite it', async () => {
    await assertFails(setDoc(doc(aliceDb(), 'users', BOB), profile('Hacked')));
  });

  test('cannot update it', async () => {
    await assertFails(updateDoc(doc(aliceDb(), 'users', BOB), { weightKg: 1 }));
  });

  test('cannot delete it', async () => {
    await assertFails(deleteDoc(doc(aliceDb(), 'users', BOB)));
  });

  test('cannot create a profile with the id of another account', async () => {
    await assertFails(setDoc(doc(aliceDb(), 'users', 'carol-uid'), profile('Carol')));
  });
});

describe('without a session', () => {
  test('cannot read any profile', async () => {
    await assertFails(getDoc(doc(anonymousDb(), 'users', ALICE)));
  });

  test('cannot write any profile', async () => {
    await assertFails(setDoc(doc(anonymousDb(), 'users', ALICE), profile('Anonymous')));
  });
});

describe('outside users/{uid}', () => {
  test('a signed-in account cannot use other collections', async () => {
    await assertFails(setDoc(doc(aliceDb(), 'other', 'doc'), { any: 'value' }));
  });
});
