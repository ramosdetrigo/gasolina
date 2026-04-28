package com.example.combustivel.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.combustivel.data.RepositorioPostos
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.net.toUri

@SuppressLint("MutableCollectionMutableState") // zzz
@Composable
fun DetalhesPosto(navController: NavHostController, index: Int) {

    val context = LocalContext.current
    val repo = remember { RepositorioPostos(context) }

    var lista by remember { mutableStateOf(repo.carregar()) }

    if (index !in lista.indices) return

    var posto by remember { mutableStateOf(lista[index]) }

    var alcool by remember { mutableStateOf(posto.alcool.toString()) }
    var gasolina by remember { mutableStateOf(posto.gasolina.toString()) }

    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("lista")
            }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Lista de postos")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(text = posto.nome, style = MaterialTheme.typography.titleLarge)

            // converte a data de millis pra uma data legível
            Text(text = "Data: ${sdf.format(Date(posto.data))}")

            // EDITÁVEL
            OutlinedTextField(
                value = alcool,
                onValueChange = { alcool = it },
                label = { Text("Álcool") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = gasolina,
                onValueChange = { gasolina = it },
                label = { Text("Gasolina") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // BOTÕES
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Salvar modificações do posto
                Button(
                    onClick = {
                        val novoAlcool = alcool.replace(",", ".").toDoubleOrNull()
                        val novoGasolina = gasolina.replace(",", ".").toDoubleOrNull()

                        if (novoAlcool != null && novoGasolina != null) {
                            val atualizado = posto.copy(
                                alcool = novoAlcool,
                                gasolina = novoGasolina
                            )

                            lista[index] = atualizado
                            repo.salvar(lista)
                            posto = atualizado
                        }
                    }
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Salvar")
                }

                // Remover mapa da lista
                Button(
                    onClick = {
                        lista.removeAt(index)
                        repo.salvar(lista)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Excluir")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // abrir mapa usando intent
                Button(
                    onClick = {
                        val uri = "geo:${posto.latitude},${posto.longitude}".toUri()
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    }
                ) {
                    Icon(Icons.Default.Map, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Ver no mapa")
                }

                // Botão de botar as informações do posto na calculadora
                Button(
                    onClick = {
                        navController.navigate(
                            // converte o nome pra URI pq espaço pode dar pau na rota
                            "calculadora?alcool=${posto.alcool}&gasolina=${posto.gasolina}&nome=${Uri.encode(posto.nome)}"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null)
                    Text("Calculadora")
                }
            }
        }
    }
}