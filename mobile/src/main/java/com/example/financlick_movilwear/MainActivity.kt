package com.example.financlick_movilwear

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
    private lateinit var progressBar: ProgressBar

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
        progressBar = findViewById(R.id.progressBar)

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

    fun login(param: LoginModel) {
        // Mostrar el ProgressBar
        progressBar.visibility = View.VISIBLE
        Toast.makeText(this, "Iniciando Sesion", Toast.LENGTH_SHORT).show()

        RetrofitClient.instance.postLogin(param).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // Ocultar el ProgressBar
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val stringJson = response.body()?.string()
                    val jsonObject = JsonParser.parseString(stringJson).asJsonObject
                    val token = jsonObject.get("token")?.asString
                    Log.i("MainActivity", "Token: $token")
                    if (token != null) {
                        getCurrentUser(token)
                        val intent = Intent(contexto, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(contexto, "Ha ocurrido un error al iniciar sesion, intente nuevamente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(contexto, response.errorBody()?.string(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Ocultar el ProgressBar
                progressBar.visibility = View.GONE

                // Manejar la falla
                Log.e("MainActivity", "Failure: ${t.message}")
                Toast.makeText(contexto, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
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
                    val stringJson = response.body()?.string()
                    val gson = Gson()
                    val jsonObject = JsonParser.parseString(stringJson).asJsonObject
                    Log.i("MainActivityRaw", "Response: $stringJson")

                    val usuario = jsonObject.get("usuarioCliente")?.asJsonObject
                    if (usuario != null) {
                        Log.i("MainActivity", "Usuario: ${usuario.get("usuario")?.asString}")

                        // Verifica los valores antes de guardarlos
                        Log.i("MainActivity", "Saving user with token: $token")
                        Log.i("MainActivity", "UsuarioCliente JSON: ${usuario.toString()}")

                        sessionManager.saveUser(jsonObject, token)

                        // Verifica si los valores se guardaron correctamente
                        val savedUser = sessionManager.getUser()
                        val savedToken = sessionManager.getToken()
                        Log.i("MainActivity", "Saved User: ${savedUser?.usuario}")
                        Log.i("MainActivity", "Saved Token: $savedToken")
                    } else {
                        Log.e("MainActivity", "UsuarioCliente is null")
                    }
                } else {
                    Toast.makeText(
                        contexto,
                        "${response.errorBody()?.string()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })
    }
}