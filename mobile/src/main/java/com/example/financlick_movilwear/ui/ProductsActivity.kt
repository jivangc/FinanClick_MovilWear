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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.adapters.CardDocAdapter
import com.example.financlick_movilwear.adapters.CardProductAdapter
import com.example.financlick_movilwear.config.SessionManager
import com.example.financlick_movilwear.items.CardProductItem
import com.example.financlick_movilwear.models.ProductoModel
import com.example.financlick_movilwear.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsActivity : AppCompatActivity() {
    lateinit var contexto: Context
    lateinit var session: SessionManager
    lateinit var backButton: ImageButton
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_products)
        contexto = this
        session = SessionManager(contexto)

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

        progressBar = findViewById(R.id.progressBar)
        backButton = findViewById(R.id.backIcon)
        backButton.setOnClickListener {
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicialmente, configura un adaptador vacío
        val adapter = CardProductAdapter(emptyList())
        recyclerView.adapter = adapter

        // Cargar datos desde el servidor
        this.getProducts { products ->
            // Actualizar el adaptador con los nuevos datos
            adapter.updateItems(products)
        }
    }

    private fun getProducts(onComplete: (List<CardProductItem>) -> Unit) {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.getProducts().enqueue(object : Callback<List<ProductoModel>> {
            override fun onResponse(call: Call<List<ProductoModel>>, response: Response<List<ProductoModel>>) {

                if (response.isSuccessful) {
                    val products = response.body()
                    if (products != null) {
                        // Convertir ProductoModel a CardProductItem
                        val cardProducts = products.map { product ->
                            CardProductItem(
                                idProducto = product.idProducto,
                                nombreProducto = product.nombreProducto,
                                metodoCalculo = product.metodoCalculo,
                                periodicidad = product.periodicidad,
                                numPagos = product.numPagos,
                                interesAnual = product.interesAnual,
                                iva = product.iva,
                                estatus = product.estatus,
                                monto = product.monto,
                                submetodo = product.subMetodo,
                                interesMoratorio = product.interesMoratorio
                            )
                        }
                        onComplete(cardProducts)
                    } else {
                        // Manejar el caso en que la respuesta sea nula
                        onComplete(emptyList())
                    }
                } else {
                    // Manejar el error, por ejemplo, mostrar un mensaje de error
                    onComplete(emptyList())
                }
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<ProductoModel>>, t: Throwable) {
                // Manejar la falla de la llamada, por ejemplo, mostrar un mensaje de error
                Log.e("PAULOOOOOO", t.toString())
                onComplete(emptyList())
                progressBar.visibility = View.GONE
            }
        })
    }

}