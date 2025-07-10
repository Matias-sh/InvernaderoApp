package com.cocido.invernadero.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.cocido.invernadero.R
import com.cocido.invernadero.network.SensorWebSocketListener
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.android.material.snackbar.Snackbar
import com.cocido.invernadero.models.InvernaderoData
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var temperatureText: TextView
    private lateinit var humidityText: TextView
    private lateinit var soilText: TextView
    private lateinit var co2Text: TextView
    private lateinit var lastUpdate: TextView
    private lateinit var modoButton: Button
    private lateinit var iluminacionButton: Button

    private var isAutoMode = true
    private var isLightOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sensores
        temperatureText = findViewById(R.id.valor_temperatura)
        humidityText = findViewById(R.id.valor_humedad_relativa)
        soilText = findViewById(R.id.valor_humedad_suelo)
        co2Text = findViewById(R.id.valor_co2)
        lastUpdate = findViewById(R.id.last_update)

        // Botones generales
        modoButton = findViewById(R.id.btn_modo)
        iluminacionButton = findViewById(R.id.btn_iluminacion)

        // Acciones generales
        modoButton.setOnClickListener { toggleModo() }
        iluminacionButton.setOnClickListener { toggleIluminacion() }

        // Acciones por malla
        listOf(
            R.id.btn_malla1_subir to "Malla 1 subida",
            R.id.btn_malla1_bajar to "Malla 1 bajada",
            R.id.btn_malla2_subir to "Malla 2 subida",
            R.id.btn_malla2_bajar to "Malla 2 bajada",
            R.id.btn_malla3_subir to "Malla 3 subida",
            R.id.btn_malla3_bajar to "Malla 3 bajada"
        ).forEach { (id, mensaje) ->
            findViewById<Button>(id).setOnClickListener {
                showFeedback(mensaje)
            }
        }

        connectToWebSocket()
    }

    private fun connectToWebSocket() {
        val request = Request.Builder()
            .url("wss://invernadero-backend.fly.dev/ws")
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

    private fun toggleModo() {
        isAutoMode = !isAutoMode
        val text = if (isAutoMode) "MODO\nAUTO" else "MODO\nMANUAL"
        val icon = if (isAutoMode) R.drawable.ic_auto_mode else R.drawable.ic_manual_mode
        modoButton.text = text
        modoButton.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
        showFeedback("Modo cambiado a ${if (isAutoMode) "Automático" else "Manual"}")
    }

    private fun toggleIluminacion() {
        isLightOn = !isLightOn

        val text = if (isLightOn) "ILUMINACIÓN\nON" else "ILUMINACIÓN\nOFF"
        val icon = if (isAutoMode) R.drawable.ic_light_on else R.drawable.ic_light_off

        iluminacionButton.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                iluminacionButton.text = text
                iluminacionButton.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
                iluminacionButton.animate().alpha(1f).setDuration(150).start()
            }.start()

        showFeedback("Iluminación ${if (isLightOn) "encendida" else "apagada"}")
    }



    private fun showFeedback(message: String) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_SHORT).show()
    }
}
