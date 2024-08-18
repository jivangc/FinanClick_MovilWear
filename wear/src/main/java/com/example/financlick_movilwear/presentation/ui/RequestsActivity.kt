package com.example.financlick_movilwear.presentation.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.SwipeDismissFrameLayout
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.presentation.adapters.RequestItemAdapter
import com.example.financlick_movilwear.presentation.models.RequestModel
import androidx.wear.widget.SwipeDismissFrameLayout.Callback
import com.example.financlick_movilwear.presentation.MainActivity
import com.example.financlick_movilwear.presentation.config.SessionManager
import com.example.financlick_movilwear.presentation.models.CreditoModel
import com.example.financlick_movilwear.presentation.models.ProductoModel
import com.example.financlick_movilwear.presentation.services.RetrofitClient
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Response

class RequestsActivity : AppCompatActivity(){
    val context: Context = this
    private lateinit var adapter: RequestItemAdapter
    private lateinit var session: SessionManager
    private var idCliente: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_requests)
        supportActionBar?.hide()
        adapter = RequestItemAdapter(mutableListOf())
        val swipeDismissRoot = findViewById<SwipeDismissFrameLayout>(R.id.swipe_dismiss_root)

        swipeDismissRoot.addCallback(object : Callback() {
            override fun onDismissed(layout: SwipeDismissFrameLayout) {
                super.onDismissed(layout)
                startActivity(Intent(context, MainActivity::class.java))
                finish()
            }
        })
        session = SessionManager(context)

        if(!session.isLoggedIn()){
            startActivity(Intent(context, MainActivity::class.java))
            finish()
        }

        idCliente = session.getCliente()!!

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicialmente, configura un adaptador vacío
        recyclerView.adapter = adapter

        // Cargar datos desde el servidor
        this.getCreditos { credit ->
            // Actualizar el adaptador con los nuevos datos
            adapter.updateItems(credit)
        }
    }

    private fun getProducts(onComplete: (List<ProductoModel>) -> Unit) {
        //progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getProducts().enqueue(object :
            retrofit2.Callback<List<ProductoModel>> {
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

    private fun getCreditos(onComplete: (MutableList<RequestModel>) -> Unit) {
        // Obtener los productos primero
        getProducts { products ->
            // Después de obtener los productos, hacer la llamada para obtener los créditos
            RetrofitClient.instance.getCreditos().enqueue(object :
                retrofit2.Callback<List<CreditoModel>> {
                override fun onResponse(call: Call<List<CreditoModel>>, response: Response<List<CreditoModel>>) {
                    Log.i("CREDITOS", response.body().toString())

                    if (response.isSuccessful && response.body() != null) {
                        try {
                            val creditosRaw = response.body()!!
                            val items = creditosRaw
                                .filter { credito -> credito.idCliente == idCliente }
                                .map { credito ->
                                    // Buscar el nombre del producto según el idProducto
                                    val productoNombre = products.find { it.idProducto == credito.idProducto }?.nombreProducto ?: "Desconocido"
                                    var estatus = ""
                                    var color = Color.BLACK

                                    // Usamos un único caso para cada valor en `when`
                                    when (credito.estatus) {
                                        1 -> {
                                            estatus = "Pendiente"
                                            color = Color.YELLOW
                                        }
                                        2 -> {
                                            estatus = "Firmado"
                                            color = Color.GREEN
                                        }
                                        3 -> {
                                            estatus = "En Curso"
                                            color = Color.GREEN
                                        }
                                        4 -> {
                                            estatus = "Finalizado"
                                            color = Color.RED
                                        }
                                    }

                                    RequestModel(
                                        name = productoNombre,
                                        price = "$ " + credito.monto.toString(),
                                        code = credito.idCredito.toString(),
                                        status = estatus,
                                        statusColor = color
                                    )
                                }
                            onComplete(items.toMutableList())
                        } catch (e: Exception) {
                            Log.e("CREDITOS", "Error processing credits", e)
                            onComplete(mutableListOf())  // En caso de error, devolvemos una lista vacía
                        }
                    } else {
                        onComplete(mutableListOf())  // Si la respuesta no es exitosa, devolvemos una lista vacía
                    }
                }

                override fun onFailure(call: Call<List<CreditoModel>>, t: Throwable) {
                    Log.e("CREDITOS", "Request failed", t)
                    onComplete(mutableListOf())  // En caso de fallo en la llamada, devolvemos una lista vacía
                }
            })
        }
    }

}