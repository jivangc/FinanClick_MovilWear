package com.example.financlick_movilwear.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.adapters.CardDocAdapter
import com.example.financlick_movilwear.config.SessionManager
import com.example.financlick_movilwear.items.CardDocItem
import com.example.financlick_movilwear.models.DocDetailCallback
import com.example.financlick_movilwear.models.DocDetailModel
import com.example.financlick_movilwear.models.DocsClienteModel
import com.example.financlick_movilwear.services.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DocumentsActivity : AppCompatActivity() {
    lateinit var contexto: Context
    lateinit var backButton: ImageButton
    lateinit var session: SessionManager
    private var docDetailList: MutableList<Pair<DocsClienteModel, DocDetailModel?>> = mutableListOf()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_documents)
        contexto = this
        session = SessionManager(contexto)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.your_status_bar_color)
            window.navigationBarColor = resources.getColor(R.color.your_navigation_bar_color)
        }

        progressBar = findViewById(R.id.progressBar)
        backButton = findViewById(R.id.backIcon)
        backButton.setOnClickListener {
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicialmente, configura un adaptador vacío
        val adapter = CardDocAdapter(emptyList())
        recyclerView.adapter = adapter

        // Obtener documentos y detalles
        getDocuments { items ->
            // Actualizar la lista de datos y el adaptador
            adapter.updateItems(items)
        }
    }

    private fun getDocuments(onComplete: (List<CardDocItem>) -> Unit) {
        val cliente = session.getClient()
        val idCliente = cliente?.get("idCliente")?.asInt

        progressBar.visibility = View.VISIBLE

        if (idCliente != null) {
            RetrofitClient.instance.getDocs(idCliente).enqueue(object : Callback<List<DocsClienteModel>> {
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
                                            progressBar.visibility = View.GONE
                                            onComplete(items)
                                        }
                                    }

                                    override fun onError(errorMessage: String) {
                                        Toast.makeText(contexto, errorMessage, Toast.LENGTH_SHORT).show()
                                        remainingRequests--
                                        progressBar.visibility = View.GONE

                                        if (remainingRequests == 0) {
                                            Log.i("DocDetail", "Detalles de todos los documentos con algunos errores")
                                            val items = getCardDocItems()
                                            onComplete(items)
                                        }
                                    }
                                })
                            }
                        } else {
                            progressBar.visibility = View.GONE
                            Toast.makeText(contexto, "No se recibieron documentos", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(contexto, "Error al obtener documentos: ${response.toString()}", Toast.LENGTH_SHORT).show()
                        Log.e("Documents", "Error: ${response.toString()}")
                        finish()
                    }
                }

                override fun onFailure(call: Call<List<DocsClienteModel>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e("Documents", "Error: ${t.message}")
                    Toast.makeText(contexto, "Error al realizar la solicitud: ${t.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(contexto, "Error al obtener el id del cliente", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getDocDetail(idDoc: Int, callback: DocDetailCallback) {
        RetrofitClient.instance.getDocDetail(idDoc).enqueue(object : Callback<DocDetailModel> {
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

    private fun getCardDocItems(): List<CardDocItem> {
        val cardDocItems = mutableListOf<CardDocItem>()

        for ((docsCliente, docDetail) in docDetailList) {
            val title = docDetail?.nombre ?: "Título no disponible"
            val description = docDetail?.tipo ?: "Descripción no disponible"
            var status = ""
            val base64Image = docsCliente.documentoBase64.replace(" ", "")
            var isPdf = false

            if (docsCliente.estatus == 1) {
                status = "Aprobado"
            }else if (docsCliente.estatus == 0) {
                status = "No Aprobado"
            }

            if (base64Image == null || base64Image.isEmpty()) {
                isPdf = false
            }else{
                isPdf = true
            }

            val cardDocItem = CardDocItem(base64Image, title, description, status, "Action", isPdf, docsCliente)
            cardDocItems.add(cardDocItem)
        }

        return cardDocItems
    }

    override fun onResume() {
        super.onResume()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicialmente, configura un adaptador vacío
        val adapter = CardDocAdapter(emptyList())
        recyclerView.adapter = adapter

        // Obtener documentos y detalles
        getDocuments { items ->
            // Actualizar la lista de datos y el adaptador
            adapter.updateItems(items)
        }
    }
}