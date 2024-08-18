/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.financlick_movilwear.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.presentation.config.SessionManager
import com.example.financlick_movilwear.presentation.services.RetrofitClient
import com.example.financlick_movilwear.presentation.ui.DocumentsActivity
import com.example.financlick_movilwear.presentation.ui.RequestsActivity
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {
    lateinit var session: SessionManager
    private val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        session = SessionManager(context)

        // Registrar el listener para los cambios en los datos
        Wearable.getDataClient(this).addListener(this)

        findViewById<Button>(R.id.buttonSolicitudes).setOnClickListener {
            startActivity(Intent(this, RequestsActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.buttonNotificaciones).setOnClickListener {
            startActivity(Intent(this, DocumentsActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Eliminar el listener para evitar fugas de memoria
        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDataChanged(events: DataEventBuffer) {
        for (event in events) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/token") {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    val token = dataMap.getString("token")
                    val idCliente = dataMap.getInt("idCliente")
                    if (token != null && idCliente != null) {
                        Log.i("LISTENER", "Se recibio el token de sesion: $token")
                        Toast.makeText(context, "Se ha recibido la sesion", Toast.LENGTH_SHORT ).show()
                        session.saveToken(token, idCliente)
                        RetrofitClient.setToken(token)
                    } else {
                        Log.i("LISTENER", "El valor del token de sesion es NULL")
                    }
                }
            }
        }
    }
}
