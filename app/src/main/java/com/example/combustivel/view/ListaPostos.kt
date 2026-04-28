package com.example.combustivel.view

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.combustivel.data.RepositorioPostos

@SuppressLint("MutableCollectionMutableState") // se aquieta aí mah
@Composable
fun ListaPostos(navController: NavHostController) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val repo = remember { RepositorioPostos(context) }

    var lista by remember { mutableStateOf(repo.carregar()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("calculadora")
            }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Calculadora")
            }
        }
    ) { padding ->

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(lista) { index, posto ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable {
                            navController.navigate("detalhe/$index")
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = posto.nome, style = MaterialTheme.typography.titleMedium)

                        Text(
                            text = "A: %.2f | G: %.2f".format(posto.alcool, posto.gasolina)
                        )
                    }
                }
            }
        }
    }
}