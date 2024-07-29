package com.example.financlick_movilwear

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financlick_movilwear.config.SessionManager
import com.example.financlick_movilwear.models.LoginModel
import com.example.financlick_movilwear.models.UsuarioModel
import com.example.financlick_movilwear.services.RetrofitClient
import com.example.financlick_movilwear.ui.HomeActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var contexto: Context
    lateinit var emailInputLayout: TextInputLayout
    lateinit var emailInput: TextInputEditText
    lateinit var contrasenaInputLayout: TextInputLayout
    lateinit var contrasenaInput: TextInputEditText
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        contexto = this
        sessionManager = SessionManager(this)
        contrasenaInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)
        contrasenaInput = findViewById<TextInputEditText>(R.id.passwordEditText)
        emailInputLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
        emailInput = findViewById<TextInputEditText>(R.id.usernameEditText)

        val emailParam = intent.getStringExtra("email")
        if(emailParam != null){
            emailInput.setText(emailParam.toString())
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            if(formValido()){
                val params = LoginModel(emailInput.text.toString(), contrasenaInput.text.toString())
                login(params)
            } else {
                Toast.makeText(this, "Existe información incorrecta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login (param: LoginModel){
        Toast.makeText(this, "Iniciando Sesion", Toast.LENGTH_SHORT).show()
        RetrofitClient.instance.postLogin(param).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    var stringJson = response.body()?.string();
                    val gson = Gson()
                    val jsonObject = JsonParser.parseString(stringJson).asJsonObject
                    val token = jsonObject.get("token")?.asString
                    Log.i("MainActivity", "Token: $token")
                    if (token != null) {
                        getCurrentUser(token)
                        Toast.makeText(contexto, "Bienvenido ${sessionManager.getUser()?.usuario}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(contexto, HomeActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(contexto, "Ha ocurrido un error al iniciar sesion, intente nuevamente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(contexto, "${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show();
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })
    }

    fun formValido() : Boolean {
        var valido = true
        if(contrasenaInput.text.toString().isEmpty()){
            contrasenaInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            contrasenaInputLayout.error = ""
        }
        if(emailInput.text.toString().isEmpty()){
            emailInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            emailInputLayout.error = ""
        }
        return valido
    }

    fun getCurrentUser(token: String) {
        RetrofitClient.setToken(token)
        RetrofitClient.instance.getDetail().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val stringJson = response.body()?.string();
                    val gson = Gson()
                    val jsonObject = JsonParser.parseString(stringJson).asJsonObject
                    Log.i("MainActivityRaw", "Response: $stringJson")
                    val usuario = gson.fromJson(jsonObject.get("usuarioCliente").asJsonObject, UsuarioModel::class.java)
                    Log.i("MainActivity", "Usuario: ${usuario.usuario}")
                    sessionManager.saveUser(jsonObject, token)
                } else {
                    Toast.makeText(
                        contexto,
                        "${response.errorBody()?.string()}",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })
    }
}