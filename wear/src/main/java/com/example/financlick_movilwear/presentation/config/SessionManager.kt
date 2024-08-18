package com.example.financlick_movilwear.presentation.config

import android.content.Context

class SessionManager (context: Context){
    private val sharedPreferences = context.getSharedPreferences("financlickwear", Context.MODE_PRIVATE)

    fun saveToken(token: String, idCliente: Int){
        val editor = sharedPreferences.edit()

        if(isLoggedIn()){
            clearSession()
        }

        editor.putInt("idCliente", idCliente)
        editor.putString("userToken", token)
        editor.apply()
    }


    fun clearSession(){
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("userToken", "")
    }

    fun getCliente(): Int? {
        return sharedPreferences.getInt("idCliente", 0)
    }

    fun isLoggedIn(): Boolean {
        val token = sharedPreferences.getString("userToken", null)
        return token != null && token.isNotBlank()
    }
}