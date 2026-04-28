// model/Posto.kt
package com.example.combustivel.model

import org.json.JSONObject

data class Posto(
    val nome: String,
    val alcool: Double,
    val gasolina: Double,
    val data: Long,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    fun toJson() = JSONObject().apply {
        put("nome", nome)
        put("alcool", alcool)
        put("gasolina", gasolina)
        put("data", data)
        put("latitude", latitude)
        put("longitude", longitude)
    }

    companion object {
        fun fromJson(obj: JSONObject?) = Posto(
            nome = obj?.getString("nome") ?: "",
            alcool = obj?.getDouble("alcool") ?: 0.0,
            gasolina = obj?.getDouble("gasolina") ?: 0.0,
            data = obj?.getLong("data") ?: 0,
            latitude = obj?.optDouble("latitude") ?: 0.0,
            longitude = obj?.optDouble("longitude") ?: 0.0
        )
    }
}