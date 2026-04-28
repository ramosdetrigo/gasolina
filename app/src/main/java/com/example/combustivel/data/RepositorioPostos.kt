package com.example.combustivel.data

import android.content.Context
import com.example.combustivel.model.Posto
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit

class RepositorioPostos(context: Context) {

    private val prefs = context.getSharedPreferences("postos_prefs", Context.MODE_PRIVATE)
    private val key = "lista_postos"

    // Salvar como json
    fun salvar(lista: List<Posto>) {
        val jsonArray = lista.map { posto -> posto.toJson() }
        prefs.edit { putString(key, jsonArray.toString()) }
    }

    // Carregar json dos prefs
    fun carregar(): MutableList<Posto> {
        val json = prefs.getString(key, null) ?: return mutableListOf()

        val lista = mutableListOf<Posto>()
        val jsonArray = JSONArray(json)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val posto = Posto.fromJson(obj)

            lista.add(posto)
        }

        return lista
    }
}