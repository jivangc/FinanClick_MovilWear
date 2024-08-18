package com.example.financlick_movilwear.presentation.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.SwipeDismissFrameLayout
import androidx.wear.widget.SwipeDismissFrameLayout.Callback
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.presentation.MainActivity
import com.example.financlick_movilwear.presentation.adapters.DocumentItemAdapter
import com.example.financlick_movilwear.presentation.adapters.RequestItemAdapter
import com.example.financlick_movilwear.presentation.config.SessionManager
import com.example.financlick_movilwear.presentation.models.DocDetailCallback
import com.example.financlick_movilwear.presentation.models.DocDetailModel
import com.example.financlick_movilwear.presentation.models.DocItemModel
import com.example.financlick_movilwear.presentation.models.DocsClienteModel
import com.example.financlick_movilwear.presentation.services.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class DocumentsActivity : AppCompatActivity() {
    val context: Context = this
    private lateinit var adapter: DocumentItemAdapter
    private lateinit var session: SessionManager
    private var idCliente: Int = 0
    private var docDetailList: MutableList<Pair<DocsClienteModel, DocDetailModel?>> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_documents)
        supportActionBar?.hide()
        adapter = DocumentItemAdapter(mutableListOf())

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
        this.getDocs { docs ->
            // Actualizar el adaptador con los nuevos datos
            Log.i("DOCUMENTOS", docs.toString())
            adapter.updateItems(docs)
        }
    }

    private fun getDocs(onComplete: (MutableList<DocItemModel>) -> Unit) {

        if (idCliente != null) {
            RetrofitClient.instance.getDocs(idCliente).enqueue(object :
                retrofit2.Callback<List<DocsClienteModel>> {
                override fun onResponse(call: Call<List<DocsClienteModel>>, response: Response<List<DocsClienteModel>>) {
                    if (response.isSuccessful) {
                        val documentos = response.body()
                        if (documentos != null) {
                            val detallesDocs = mutableListOf<DocDetailModel?>()
                            var remainingRequests = documentos.size

                            for (documento in documentos) {
                                getDocDetail(documento.idDocumento, object : DocDetailCallback {
                                    override fun onSuccess(docDetail: DocDetailModel) {
                                        detallesDocs.add(docDetail)
                                        remainingRequests--

                                        if (remainingRequests == 0) {
                                            docDetailList.clear()
                                            for (i in documentos.indices) {
                                                docDetailList.add(Pair(documentos[i], detallesDocs.getOrNull(i)))
                                            }

                                            // Convertir a CardDocItem y llamar al callback
                                            val items = getCardDocItems()
                                            onComplete(items)
                                        }
                                    }

                                    override fun onError(errorMessage: String) {
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                        remainingRequests--

                                        if (remainingRequests == 0) {
                                            Log.i("DocDetail", "Detalles de todos los documentos con algunos errores")
                                            val items = getCardDocItems()
                                            onComplete(items)
                                        }
                                    }
                                })
                            }
                        } else {
                            Toast.makeText(context, "No se recibieron documentos", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Error al obtener documentos: ${response.toString()}", Toast.LENGTH_SHORT).show()
                        Log.e("Documents", "Error: ${response.toString()}")
                        finish()
                    }
                }

                override fun onFailure(call: Call<List<DocsClienteModel>>, t: Throwable) {

                    Log.e("Documents", "Error: ${t.message}")
                    Toast.makeText(context, "Error al realizar la solicitud: ${t.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
        } else {
            Toast.makeText(context, "Error al obtener el id del cliente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getDocDetail(idDoc: Int, callback: DocDetailCallback) {
        RetrofitClient.instance.getDocDetail(idDoc).enqueue(object :
            retrofit2.Callback<DocDetailModel> {
            override fun onResponse(call: Call<DocDetailModel>, response: Response<DocDetailModel>) {
                if (response.isSuccessful) {
                    val docDetail = response.body()
                    if (docDetail != null) {
                        callback.onSuccess(docDetail)
                    } else {
                        callback.onError("No se recibieron detalles del documento")
                    }
                } else {
                    callback.onError("Error al obtener detalles del documento: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<DocDetailModel>, t: Throwable) {
                callback.onError("Error al realizar la solicitud: ${t.message}")
            }
        })
    }

    private fun getCardDocItems(): MutableList<DocItemModel> {
        val cardDocItems = mutableListOf<DocItemModel>()

        for ((docsCliente, docDetail) in docDetailList) {
            val title = docDetail?.nombre ?: "Título no disponible"
            val description = docDetail?.tipo ?: "Descripción no disponible"
            var status = "No Reconocido"
            var statusColor = Color.BLACK

            when(docsCliente.estatus){
                4 -> {
                    status = "Pendiente"
                    statusColor = Color.YELLOW
                }
                3 -> {
                    status = "Por revisar"
                    statusColor = Color.YELLOW
                }
                2 -> {
                    status = "Rechazado"
                    statusColor = Color.RED
                }
                1 -> {
                    status = "Aprobado"
                    statusColor = Color.GREEN
                }
            }

            val cardDocItem = DocItemModel(title, description, status, statusColor)
            cardDocItems.add(cardDocItem)
        }

        return cardDocItems
    }
}