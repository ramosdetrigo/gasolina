package com.example.combustivel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.combustivel.ui.theme.CombustivelTheme
import com.example.combustivel.view.Calculadora
import com.example.combustivel.view.DetalhesPosto
import com.example.combustivel.view.ListaPostos

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CombustivelTheme {
                val navController: NavHostController = rememberNavController()
                NavHost(navController = navController, startDestination = "calculadora") {
                    // navArguments pra poder colocar dados dos postos da tela de detalhes
                    // na tela da calculadora
                    composable(
                        route = "calculadora?alcool={alcool}&gasolina={gasolina}&nome={nome}",
                        arguments = listOf(
                            navArgument("alcool") { defaultValue = "" },
                            navArgument("gasolina") { defaultValue = "" },
                            navArgument("nome") { defaultValue = "" }
                        )
                    ) { backStackEntry ->
                        Calculadora(
                            navController = navController,
                            alcoolInicial = backStackEntry.arguments?.getString("alcool") ?: "",
                            gasolinaInicial = backStackEntry.arguments?.getString("gasolina") ?: "",
                            nomeInicial = backStackEntry.arguments?.getString("nome") ?: ""
                        )
                    }
                    composable("lista") { ListaPostos(navController) }
                    composable("detalhe/{index}") { backStack ->
                        val index = backStack.arguments?.getString("index")?.toInt() ?: 0
                        DetalhesPosto(navController, index)
                    }
                }
            }
        }
    }
}

