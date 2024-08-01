package com.example.financlick_movilwear.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.financlick_movilwear.MainActivity
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.config.SessionManager
import com.example.financlick_movilwear.models.PersonaFisica
import com.example.financlick_movilwear.models.UsuarioModel
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject

class HomeActivity : AppCompatActivity() {
    lateinit var session: SessionManager
    var data: JsonObject? = null
    var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.your_status_bar_color)
            window.navigationBarColor = resources.getColor(R.color.your_navigation_bar_color)
        }

        session = SessionManager(this)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val menuButton: ImageButton = findViewById(R.id.menuIcon)
        val datosButton: ImageButton = findViewById(R.id.datosButton)
        val perfilButton: ImageButton = findViewById(R.id.perfilButton)
        val solicitarButton: ImageButton = findViewById(R.id.solicitarButton)
        val btnMenu: ImageButton = findViewById(R.id.btnMenu)
        val btnRequests: ImageButton = findViewById(R.id.btnRequests)
        val btnNotifications: ImageButton = findViewById(R.id.btnNotifications)

        setSupportActionBar(toolbar)

        menuButton.setOnClickListener {  // Set click listener on ImageButton
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // TOP BUTTONS
        datosButton.setOnClickListener {
            showDocuments()
        }

        perfilButton.setOnClickListener {
            showProfile()
        }

        // NAV MENU
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (this !is HomeActivity) {
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                }
                R.id.nav_profile -> {
                    showProfile()
                }
                R.id.nav_settings -> {
                    showDocuments()
                }
                R.id.logout -> {
                    showLogoutDialog()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // MENU BUTTONS
        btnMenu.setOnClickListener {
            if (this !is HomeActivity) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }


        if (session.getToken() !== null) {
            userType = session.getUserType()
            val txtWelcome = findViewById<TextView>(R.id.welcomeText)
            data = session.getPerson()
            if (userType.equals("FISICA")) {
                val nombre = data?.get("nombre")?.asString ?: "Invitado"
                txtWelcome.text = "Hola, $nombre"
            } else if (userType.equals("MORAL")) {
                val razonSocial = data?.get("razonSocial")?.asString ?: "Empresa no identificada"
                txtWelcome.text = razonSocial
            } else {
                txtWelcome.text = "No Encontrado"
            }
            Toast.makeText(this, "Bienvenido de Nuevo", Toast.LENGTH_SHORT).show()
        } else {
            val intent = android.content.Intent(this, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Ocurrio un error al iniciar sesion, vuelve a intentarlo mas tarde", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cerrar Sesión")
        builder.setMessage("Está seguro que desea cerrar sesión?")

        builder.setPositiveButton("Aceptar") { dialog, which ->
            Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
            session.clearSession()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    fun showProfile(){
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun showDocuments(){
        val intent = Intent(this, DocumentsActivity::class.java)
        startActivity(intent)
    }
}