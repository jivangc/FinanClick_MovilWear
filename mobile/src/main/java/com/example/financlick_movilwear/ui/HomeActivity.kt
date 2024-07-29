package com.example.financlick_movilwear.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financlick_movilwear.MainActivity
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.config.SessionManager
import com.example.financlick_movilwear.models.PersonaFisica
import com.example.financlick_movilwear.models.UsuarioModel
import com.google.gson.Gson
import com.google.gson.JsonObject

class HomeActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    var data: JsonObject? = null
    var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        session = SessionManager(this)
        if (session.getToken() !== null) {
            userType = session.getUserType()
            val txtWelcome = findViewById<TextView>(R.id.welcomeText)
            data = session.getPerson()
            Log.i("dataSession", data.toString())
            Log.i("dataSession", userType.toString())
            if (userType.equals("FISICA")) {
                val nombre = data?.get("nombre")?.asString ?: "Invitado"
                txtWelcome.text = "Hola, $nombre"
            } else {
                val razonSocial = data?.get("razonSocial")?.asString ?: "Empresa no identificada"
                txtWelcome.text = razonSocial
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val sessionManager = SessionManager(this)
        Log.i("SessionManager", "Session Cleaned")
        sessionManager.clearSession()
    }
}