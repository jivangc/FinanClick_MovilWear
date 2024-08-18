package com.example.financlick_movilwear.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.adapters.CardCreditAdapter
import com.example.financlick_movilwear.adapters.CardProductAdapter
import com.example.financlick_movilwear.config.SessionManager
import com.example.financlick_movilwear.items.CardCreditItem
import com.example.financlick_movilwear.items.CardProductItem
import com.example.financlick_movilwear.models.ClienteModel
import com.example.financlick_movilwear.models.CreditoModel
import com.example.financlick_movilwear.models.ProductoModel
import com.example.financlick_movilwear.services.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class RequestsActivty : AppCompatActivity() {

    lateinit var context: Context
    lateinit var session: SessionManager
    lateinit var backButton: ImageButton
    var idClienteAct: Int = 0
    lateinit var cliente: ClienteModel
    //private lateinit var progressBar: ProgressBar
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_requests_activty)
        session = SessionManager(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.your_status_bar_color)
            window.navigationBarColor = resources.getColor(R.color.your_navigation_bar_color)
        }

        // MENU BUTTONS
        val btnMenu: ImageButton = findViewById(R.id.btnMenu)
        val btnRequests: ImageButton = findViewById(R.id.btnRequests)
        val btnNotifications: ImageButton = findViewById(R.id.btnNotifications)
        btnMenu.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
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

        cliente = gson.fromJson(session.getClient(), ClienteModel::class.java)
        idClienteAct = cliente.idCliente

        backButton = findViewById(R.id.backIcon)
        backButton.setOnClickListener {
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicialmente, configura un adaptador vacío
        val adapter = CardCreditAdapter(emptyList(), this)
        recyclerView.adapter = adapter

        // Cargar datos desde el servidor
        this.getCreditos { credit ->
            // Actualizar el adaptador con los nuevos datos
            adapter.updateItems(credit.sortedByDescending { cred -> cred.idCredito })
        }
    }

    private fun getCreditos(onComplete: (List<CardCreditItem>) -> Unit) {
        // Obtener los productos primero
        getProducts { products ->
            // Después de obtener los productos, hacer la llamada para obtener los créditos
            RetrofitClient.instance.getCreditos().enqueue(object : Callback<List<CreditoModel>> {
                override fun onResponse(call: Call<List<CreditoModel>>, response: Response<List<CreditoModel>>) {
                    Log.i("CREDITOS", response.body().toString())

                    if (response.isSuccessful && response.body() != null) {
                        try {
                            val creditosRaw = response.body()!!
                            val items = creditosRaw
                                .filter { credito -> credito.idCliente == idClienteAct }
                                .map { credito ->
                                    // Buscar el nombre del producto según el idProducto
                                    val productoNombre = products.find { it.idProducto == credito.idProducto }?.nombreProducto ?: "Desconocido"
                                    var estatus = ""

                                    when(credito.estatus){
                                        1 -> estatus = "Pendiente"
                                        2 -> estatus = "Firmado"
                                        3 -> estatus = "En Curso"
                                        4 -> estatus = "Finalizado"
                                    }

                                    CardCreditItem(
                                        producto = productoNombre,
                                        monto = credito.monto,
                                        estatus = estatus,
                                        idCredito = credito.idCredito,
                                        pagos = credito.numPagos,
                                        periodicidad = credito.periodicidad,
                                        interes = credito.interesOrdinario,
                                        fechaActivacion = credito.fechaActivacion
                                    )
                                }
                            onComplete(items)
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
    }


    private fun getProducts(onComplete: (List<ProductoModel>) -> Unit) {
        //progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getProducts().enqueue(object : Callback<List<ProductoModel>> {
            override fun onResponse(call: Call<List<ProductoModel>>, response: Response<List<ProductoModel>>) {

                if (response.isSuccessful) {
                    val products = response.body()
                    if (products != null) {
                        onComplete(products)
                    } else {
                        // Manejar el caso en que la respuesta sea nula
                        onComplete(emptyList())
                    }
                } else {
                    // Manejar el error, por ejemplo, mostrar un mensaje de error
                    onComplete(emptyList())
                }
                //progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<ProductoModel>>, t: Throwable) {
                // Manejar la falla de la llamada, por ejemplo, mostrar un mensaje de error
                Log.e("PRODUCTOS", t.toString())
                onComplete(emptyList())
                //progressBar.visibility = View.GONE
            }
        })
    }
}