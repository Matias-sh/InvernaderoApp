package com.cocido.invernadero.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cocido.invernadero.R
import com.cocido.invernadero.network.SensorWebSocketListener
import okhttp3.OkHttpClient
import okhttp3.Request
import com.cocido.invernadero.models.InvernaderoData

class MainActivity : AppCompatActivity() {

    private lateinit var temperatureText: TextView
    private lateinit var humidityText: TextView
    private lateinit var soilText: TextView
    private lateinit var co2Text: TextView
    private lateinit var lastUpdate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        temperatureText = findViewById(R.id.valor_temperatura)
        humidityText = findViewById(R.id.valor_humedad_relativa)
        soilText = findViewById(R.id.valor_humedad_suelo)
        co2Text = findViewById(R.id.valor_co2)
        lastUpdate = findViewById(R.id.last_update)

        connectToWebSocket()
    }

    private fun connectToWebSocket() {
        val request = Request.Builder()
            .url("wss://invernadero-backend-fsa.onrender.com/ws")
            .build()


        val listener = SensorWebSocketListener { data ->
            runOnUiThread {
                temperatureText.text = "${data.temperature}°C"
                humidityText.text = "${data.humidity}%"
                soilText.text = "${data.soilMoisture}%"
                co2Text.text = "${data.co2} ppm"
                lastUpdate.text = "Última actualización: ${data.timestamp.substring(11, 19)}"
            }
        }

        OkHttpClient().newWebSocket(request, listener)
    }
}