package com.cocido.invernadero.models

data class InvernaderoData(
    val temperature: Double,
    val humidity: Double,
    val soilMoisture: Double,
    val co2: Int,
    val timestamp: String
)
