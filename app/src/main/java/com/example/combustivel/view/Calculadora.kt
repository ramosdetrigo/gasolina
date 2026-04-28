package com.example.combustivel.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.navigation.NavHostController
import com.example.combustivel.R
import com.example.combustivel.data.RepositorioPostos
import com.example.combustivel.model.Posto
import com.google.android.gms.location.LocationServices
import kotlin.math.round

@Composable
fun Calculadora(
    navController: NavHostController,
    alcoolInicial: String,
    gasolinaInicial: String,
    nomeInicial: String
) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var alcool by remember { mutableStateOf(alcoolInicial) }
    var gasolina by remember { mutableStateOf(gasolinaInicial) }
    var nomeDoPosto by remember { mutableStateOf(nomeInicial) }

    // eficiência persistente
    var eficiencia by remember {
        mutableFloatStateOf(prefs.getFloat("eficiencia", 70f))
    }

    LaunchedEffect(eficiencia) {
        prefs.edit {
            putFloat("eficiencia", eficiencia)
        }
    }

    // resultado da calculadora -> alcool é melhor, gasolina é melhor, erro, ou vazio
    var tipoResultado by remember { mutableStateOf<String?>(null) }
    var valorCalculado by remember { mutableFloatStateOf(0f) }

    val resultadoTexto = when (tipoResultado) {
        "erro" -> stringResource(R.string.resultado_invalido)
        "alcool" -> stringResource(R.string.melhor_alcool, valorCalculado)
        "gasolina" -> stringResource(R.string.melhor_gasolina, valorCalculado)
        else -> "---"
    }

    // permissão de localização
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    // callback pra obter localização via GPS via dados async
    fun getLocation(onResult: (Double, Double) -> Unit) {
        val client = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onResult(0.0, 0.0)
            return
        }

        client.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                onResult(loc.latitude, loc.longitude)
            } else {
                onResult(0.0, 0.0)
            }
        }
    }

    Scaffold(
        // botão flutuante pra ver a lista de postos
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("lista") }
            ) {
                Icon(Icons.Filled.List, contentDescription = "Lista")
            }
        }
    ) { padding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {

            Column(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // input: álcool
                OutlinedTextField(
                    value = alcool,
                    onValueChange = { alcool = it },
                    label = { Text(stringResource(R.string.alcool_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // input: gasolina
                OutlinedTextField(
                    value = gasolina,
                    onValueChange = { gasolina = it },
                    label = { Text(stringResource(R.string.gasolina_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // label do slider de eficiencia
                Text(
                    text = stringResource(R.string.eficiencia, eficiencia.toInt()),
                    style = MaterialTheme.typography.bodyMedium
                )

                Slider(
                    value = eficiencia,
                    onValueChange = { eficiencia = round(it) },
                    valueRange = 50f..100f,
                    steps = 49,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )

                // botão de calcular qual é mais eficiente
                Button(
                    onClick = {
                        val alcoolValor = alcool.replace(",", ".").toFloatOrNull()
                        val gasolinaValor = gasolina.replace(",", ".").toFloatOrNull()

                        if (alcoolValor == null || gasolinaValor == null || gasolinaValor == 0f) {
                            tipoResultado = "erro"
                        } else {
                            val alcoolAjustado = alcoolValor / (eficiencia / 100f)
                            valorCalculado = alcoolAjustado

                            // guarda qual é mais eficiente e só seta as coisas do lado de fora (resultadoTexto when ...)
                            // pq não dá pra usar context muito facilmente pra isso aqui
                            tipoResultado =
                                if (alcoolAjustado <= gasolinaValor) "alcool" else "gasolina"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.calcular))
                }

                // Resultado: álcool, gasolina, erro, ...
                Text(
                    text = resultadoTexto,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // linha separadora
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                // input do nome do posto pra salvar na lista de postos
                OutlinedTextField(
                    value = nomeDoPosto,
                    onValueChange = { nomeDoPosto = it },
                    label = { Text(stringResource(R.string.nome_posto)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Botão de salvar posto
                Button(
                    onClick = {

                        val alcoolValor = alcool.replace(",", ".").toDoubleOrNull()
                        val gasolinaValor = gasolina.replace(",", ".").toDoubleOrNull()

                        if (alcoolValor == null || gasolinaValor == null) return@Button

                        if (!hasLocationPermission) {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            return@Button
                        }

                        val repositorio = RepositorioPostos(context)

                        getLocation { lat, lon ->

                            val lista = repositorio.carregar()

                            val posto = Posto(
                                // seta o nome do posto como um genérico caso o input esteja vazio
                                nome = nomeDoPosto.ifBlank { "Posto ${lista.size + 1}" },
                                alcool = alcoolValor,
                                gasolina = gasolinaValor,
                                data = System.currentTimeMillis(),
                                latitude = lat,
                                longitude = lon
                            )

                            lista.add(posto)

                            repositorio.salvar(lista)

                            navController.navigate("lista")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.salvar_posto))
                }
            }
        }
    }
}