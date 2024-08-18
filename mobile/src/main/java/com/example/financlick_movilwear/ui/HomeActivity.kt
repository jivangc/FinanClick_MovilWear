package com.example.financlick_movilwear.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
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
import com.example.financlick_movilwear.items.CardCreditItem
import com.example.financlick_movilwear.models.ClienteModel
import com.example.financlick_movilwear.models.CreditoModel
import com.example.financlick_movilwear.models.PersonaFisica
import com.example.financlick_movilwear.models.UsuarioModel
import com.example.financlick_movilwear.services.RetrofitClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity(), DataClient.OnDataChangedListener {
    lateinit var session: SessionManager
    var data: JsonObject? = null
    var userType: String? = null
    var clienteActual: Int = 0
    lateinit var clienteRaw: ClienteModel
    val gson = Gson()
    lateinit var creditosCliente: List<CreditoModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        Wearable.getDataClient(this).addListener(this)

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
        val btnSolicitudes: Button = findViewById(R.id.btnVerSolicitudes)
        val txtMontoAprobado: TextView = findViewById(R.id.txtAprobado)
        val tablaSolicitudes: TableLayout = findViewById(R.id.tablaSolicitudes)

        Log.i("SESION", session.getClient().toString())
        try{
            clienteRaw = gson.fromJson(session.getClient(), ClienteModel::class.java)
            clienteActual = clienteRaw.idCliente
        }catch (e: Exception){
            Log.e("SESION", "session getClient failed or is null")
            finish()
        }

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

        solicitarButton.setOnClickListener {
            showProducts()
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
        val btnMenu: ImageButton = findViewById(R.id.btnMenu)
        val btnRequests: ImageButton = findViewById(R.id.btnRequests)
        val btnNotifications: ImageButton = findViewById(R.id.btnNotifications)
        btnMenu.setOnClickListener {
            if (this !is HomeActivity) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }

        btnRequests.setOnClickListener{
            val intent = Intent(this, RequestsActivty::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btnNotifications.setOnClickListener{
            val intent = Intent(this, NotificationsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }


        // Boton de la tablas de solicitudes
        btnSolicitudes.setOnClickListener{
            val intent = Intent(this, RequestsActivty::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
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

        enviarDatos()

        getCreditos { result ->
            creditosCliente = emptyList()
            val listaMutable = creditosCliente.toMutableList()

            for (cred in result) {
                if (cred.idCliente == clienteActual) {
                    listaMutable.add(cred)
                }
            }
            creditosCliente = listaMutable

            setActiveCredit(txtMontoAprobado)
            llenarTablaConDatos(this, tablaSolicitudes, creditosCliente.sortedByDescending { cred -> cred.idCredito }.take(3))
            //enviarDatosWearable(creditosCliente)
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
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    // Mostrar Pantallas
    fun showProfile(){
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun showDocuments(){
        val intent = Intent(this, DocumentsActivity::class.java)
        startActivity(intent)
    }

    fun showProducts(){
        val intent = Intent(this, ProductsActivity::class.java)
        startActivity(intent)
    }

    fun showNotificatios(){
        val intent = Intent(this, NotificationsActivity::class.java)
        startActivity(intent)
    }

    fun showRequest(){
        val intent = Intent(this, RequestsActivty::class.java)
        startActivity(intent)
    }

    // Datos de Pantalla
    private fun setActiveCredit(txtValue: TextView){
        var creditoAprobado: Double = 0.0

        for (cred in creditosCliente){
            if (cred.estatus == 3){
                creditoAprobado += cred.monto
            }
        }

        txtValue.text = "$ " + creditoAprobado.toString()
    }

    private fun llenarTablaConDatos(context: Context, tableLayout: TableLayout, requestItems: List<CreditoModel>) {
        for (item in requestItems) {
            val row = TableRow(context)

            // ID
            val idTextView = TextView(context).apply {
                text = item.idCredito.toString()
                setPadding(8, 8, 8, 8)
                setTextColor(Color.BLACK)
                textSize = 14f
            }

            // Monto
            val montoTextView = TextView(context).apply {
                text = "$ " + item.monto.toString()
                setPadding(8, 8, 8, 8)
                setTextColor(Color.BLACK)
                textSize = 14f
            }

            // Documento (ImageView)
            val imageView = ImageView(context).apply {
                setImageResource(R.drawable.informacion_solicitud)
                setPadding(8, 8, 8, 8)
                setColorFilter(Color.BLACK)
                layoutParams = ViewGroup.LayoutParams(40, 30)
            }

            // Estado
            val estadoTextView = TextView(context).apply {
                var statusString = ""
                var statusColor = ""
                when (item.estatus){
                    1 -> {
                        statusString = "Pendiente"
                        statusColor = "#FFC107"
                    }
                    2 -> {
                        statusString = "Firmado"
                        statusColor = "#FFC107"
                    }
                    3 -> {
                        statusString = "En Curso"
                        statusColor = "#4CAF50"
                    }
                    4 -> {
                        statusString = "Finalizado"
                        statusColor = "#F44336"
                    }

                }

                text = statusString
                setPadding(8, 8, 8, 8)
                textSize = 14f
                setTextColor(Color.parseColor(statusColor))
            }

            // Agregar los elementos al TableRow
            row.addView(idTextView)
            row.addView(montoTextView)
            row.addView(imageView)
            row.addView(estadoTextView)

            // Añadir el TableRow al TableLayout
            tableLayout.addView(row)
        }
    }

    // Peticiones a Api
    private fun getCreditos(onComplete: (List<CreditoModel>) -> Unit){
        RetrofitClient.instance.getCreditos().enqueue(object : Callback<List<CreditoModel>> {
            override fun onResponse(call: Call<List<CreditoModel>>, response: Response<List<CreditoModel>>) {
                Log.i("CREDITOS", response.body().toString())
                if (response.isSuccessful && response.body() != null) {
                    try {
                        val creditosRaw = response.body()!!
                        onComplete(creditosRaw)
                    } catch (e: Exception) {
                        Log.e("CREDITOS", "Error processing credits", e)
                        onComplete(emptyList())  // En caso de error, devolvemos una lista vacía
                    }
                } else {
                    onComplete(emptyList())  // Si la respuesta no es exitosa, devolvemos una lista vacía
                }
            }

            override fun onFailure(call: Call<List<CreditoModel>>, t: Throwable) {
                Log.e("CREDITOS", "Request failed", t)
                onComplete(emptyList())  // En caso de fallo en la llamada, devolvemos una lista vacía
            }
        })
    }

    // Listener Wearable
    override fun onDataChanged(event: DataEventBuffer) {
        Log.i("LISTENER", "Listener Activo en Movil")
    }

    private fun enviarDatos() {
        val tokenSession = session.getToken()
        val clienteId = session.getClient()?.get("idCliente")?.asInt
        val dataMapRequest = PutDataMapRequest.create("/token")
        if (tokenSession != null) {
            dataMapRequest.dataMap.putString("token", tokenSession)
        }
        if (clienteId != null) {
            dataMapRequest.dataMap.putInt("idCliente", clienteId)
        }
        val putDataRequest = dataMapRequest.asPutDataRequest()
        val putDataTask: Task<DataItem> = Wearable.getDataClient(this).putDataItem(putDataRequest)

        putDataTask.addOnSuccessListener {
            Log.i("LISTENER", "Se ha enviado la información del token: $tokenSession")
        }.addOnFailureListener { e ->
            Log.e("LISTENER", "Error al enviar la información del token", e)
        }
    }

}