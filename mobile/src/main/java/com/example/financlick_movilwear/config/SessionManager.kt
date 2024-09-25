package com.example.financlick_movilwear.config

import android.content.Context
import android.util.Log
import com.example.financlick_movilwear.models.DatosClienteFisica
import com.example.financlick_movilwear.models.DatosClienteMoral
import com.example.financlick_movilwear.models.PersonaFisica
import com.example.financlick_movilwear.models.PersonaMoral
import com.example.financlick_movilwear.models.UsuarioModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

class SessionManager (context: Context){
    private val sharedPreferences = context.getSharedPreferences("financlick", Context.MODE_PRIVATE)

    // Guardar Datos del Usuario
    fun saveUser(userRaw: JsonObject, token: String) {

        // Eliminar la sesión anterior si el usuario ya estaba logueado
        if (isLoggedIn()) {
            clearSession()
        }

        val editor = sharedPreferences.edit()

        val usuarioCliente = userRaw.get("usuarioCliente")?.asJsonObject
        editor.putString("user", usuarioCliente?.toString() ?: "{}")
        Log.d("saveUser", "User: ${usuarioCliente?.toString()}")

        val clienteObj = userRaw.get("cliente")?.asJsonObject
        editor.putString("cliente", clienteObj?.toString() ?: "{}")
        Log.d("saveUser", "Cliente: ${clienteObj?.toString()}")
        if (clienteObj != null) {
            val regimenFiscalStr = clienteObj.get("regimenFiscal")?.asString
            if (regimenFiscalStr != null) {
                if (regimenFiscalStr == "FISICA") {
                    editor.putString("user_type", "FISICA")
                    val personaObj = userRaw.get("persona")?.asJsonObject
                    editor.putString("person", personaObj?.toString() ?: "{}")
                    Log.d("saveUser", "Person (FISICA): ${personaObj?.toString()}")

                    val dataFisica = userRaw.get("datosClienteFisica")?.asJsonObject
                    editor.putString("data", dataFisica?.toString() ?: "{}")
                    Log.d("saveUser", "Data (FISICA): ${dataFisica?.toString()}")
                } else {
                    editor.putString("user_type", "MORAL")
                    val personaMoralObj = userRaw.get("personaMoral")?.asJsonObject
                    editor.putString("person", personaMoralObj?.toString() ?: "{}")
                    Log.d("saveUser", "Person (MORAL): ${personaMoralObj?.toString()}")

                    val dataMoral = userRaw.get("datosClienteMoral")?.asJsonObject
                    editor.putString("data", dataMoral?.toString() ?: "{}")
                    Log.d("saveUser", "Data (MORAL): ${dataMoral?.toString()}")
                }
            }
        }
        editor.putString("token", token)
        Log.d("saveUser", "Token: $token")

        editor.apply() // Usar apply() para guardar los cambios de forma asíncrona
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
            ?: return null

        val gson = Gson() // Create a single Gson instance

        return try {
            if (userType == "FISICA") {
                val personaObj = gson.fromJson(json, PersonaFisica::class.java)
                gson.toJsonTree(personaObj).asJsonObject
            } else {
                val personaObj = gson.fromJson(json, PersonaMoral::class.java)
                gson.toJsonTree(personaObj).asJsonObject
            }
        } catch (e: JsonSyntaxException) {
            Log.e("SessionManager", "Error parsing JSON", e)
            null // Or handle the error appropriately
        }
    }

    fun getClient(): JsonObject? {
        val clientStr = sharedPreferences.getString("cliente", null)
        val gson = Gson()
        return try {
            gson.fromJson(clientStr, JsonObject::class.java)
        } catch (e: JsonSyntaxException) {
            Log.e("SessionManager", "Error parsing JSON", e)
            null // Or handle the error appropriately
        }
    }

    fun getData(): JsonObject?{
        val userType = sharedPreferences.getString("user_type", null)
        if (userType.equals("FISICA")){
            val json = sharedPreferences.getString("data", null)
            val datosObj = Gson().fromJson(json, DatosClienteFisica::class.java)
            return Gson().toJsonTree(datosObj).asJsonObject
        }else{
            val json = sharedPreferences.getString("data", null)
            val datosObj = Gson().fromJson(json, DatosClienteMoral::class.java)
            return Gson().toJsonTree(datosObj).asJsonObject
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.contains("user") && sharedPreferences.contains("token")
    }

    // Eliminar Datos del Usuario (Cerrar Sesion)
    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}