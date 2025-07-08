package com.cocido.invernadero.network

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import com.cocido.invernadero.models.InvernaderoData

class SensorWebSocketListener(
    private val onDataReceived: (InvernaderoData) -> Unit
) : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocket", "Conectado al backend")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val json = JSONObject(text)
            val data = InvernaderoData(
                temperature = json.getDouble("temperature"),
                humidity = json.getDouble("humidity"),
                soilMoisture = json.getDouble("soilMoisture"),
                co2 = json.getInt("co2"),
                timestamp = json.getString("timestamp")
            )
            onDataReceived(data)
        } catch (e: Exception) {
            Log.e("WebSocket", "Error parseando JSON", e)
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocket", "Fallo de conexión", t)
    }
}
