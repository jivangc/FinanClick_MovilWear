package com.example.financlick_movilwear.config

import android.content.Context
import com.example.financlick_movilwear.models.DatosClienteFisica
import com.example.financlick_movilwear.models.DatosClienteMoral
import com.example.financlick_movilwear.models.PersonaFisica
import com.example.financlick_movilwear.models.PersonaMoral
import com.example.financlick_movilwear.models.UsuarioModel
import com.google.gson.Gson
import com.google.gson.JsonObject

class SessionManager (context: Context){
    private val sharedPreferences = context.getSharedPreferences("financlick", Context.MODE_PRIVATE)

    // Guardar Datos del Usuario
    fun saveUser(userRaw: JsonObject, token: String) {
        val editor = sharedPreferences.edit()

        editor.putString("user", userRaw.get("usuarioCliente")?.asJsonObject?.toString())

        val cliente = userRaw.get("cliente")?.asJsonObject
        if (cliente != null) {
            val regimenFiscal = cliente.get("regimenFiscal")?.asString
            if (regimenFiscal != null) {
                if (regimenFiscal == "FISICA") {
                    editor.putString("user_type", "FISICA")
                    editor.putString("person", userRaw.get("persona")?.asJsonObject?.toString())
                    editor.putString("data", userRaw.get("datosClienteFisica")?.asJsonObject?.toString())
                } else {
                    editor.putString("user_type", "MORAL")
                    editor.putString("person", userRaw.get("personaMoral")?.asJsonObject?.toString())
                    editor.putString("data", userRaw.get("datosClienteMoral")?.asJsonObject?.toString())
                }
            }
        }

        editor.putString("token", token)
        editor.apply()
    }

    // Obtener datos del usuario
    fun getUser(): UsuarioModel?{
        val json = sharedPreferences.getString("user", null)
        return Gson().fromJson(json, UsuarioModel::class.java)
    }
    fun getToken(): String?{
        return sharedPreferences.getString("token", null)
    }

    fun getUserType(): String?{
        return sharedPreferences.getString("user_type", null)
    }

    fun getPerson(): JsonObject? {
        val userType = sharedPreferences.getString("user_type", null)
        val json = sharedPreferences.getString("person", null)
            ?: return null // Handle the case where no person data is stored

        return if (userType == "FISICA") {
            val persona = Gson().fromJson(json, PersonaFisica::class.java)
            Gson().toJsonTree(persona).asJsonObject
        } else {
            val persona = Gson().fromJson(json, PersonaMoral::class.java)
            Gson().toJsonTree(persona).asJsonObject
        }
    }

    fun getData(): JsonObject?{
        val userType = sharedPreferences.getString("user_type", null)
        if (userType.equals("FISICA")){
            val json = sharedPreferences.getString("data", null)
            val datos = Gson().fromJson(json, DatosClienteFisica::class.java)
            return Gson().toJsonTree(datos).asJsonObject
        }else{
            val json = sharedPreferences.getString("data", null)
            val datos = Gson().fromJson(json, DatosClienteMoral::class.java)
            return Gson().toJsonTree(datos).asJsonObject
        }
    }

    // Eliminar Datos del Usuario (Cerrar Sesion)
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}