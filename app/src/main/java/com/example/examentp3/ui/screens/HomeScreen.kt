package com.example.examentp3.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.examentp3.viewmodel.ExampleViewModel

@Composable
fun HomeScreen(
    onNavigate: () -> Unit,
    viewModel: ExampleViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(state.message)
        Text("Contador: ${state.counter}")

        Button(onClick = viewModel::incrementCounter) {
            Text("Incrementar")
        }

        Button(onClick = onNavigate) {
            Text("Ir a otra pantalla")
        }
    }
}