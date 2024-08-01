package com.example.financlick_movilwear.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.financlick_movilwear.MainActivity
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.config.SessionManager
import com.example.financlick_movilwear.items.CardDocItem
import com.example.financlick_movilwear.models.DocDetailCallback
import com.example.financlick_movilwear.models.DocDetailModel
import com.example.financlick_movilwear.models.DocRequestModel
import com.example.financlick_movilwear.models.DocsClienteModel
import com.example.financlick_movilwear.services.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream

class EditDocumentActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1001
    private val PICK_PDF_REQUEST_CODE = 1002
    private var pdfBase64: String? = null
    private lateinit var webView: WebView
    private var contexto = this
    lateinit var session: SessionManager
    private lateinit var idDoc: String
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_document)
        idDoc = intent.getStringExtra("docs").toString()
        session = SessionManager(contexto)
        progressBar = findViewById(R.id.progressBar)

        // Initialize WebView
        webView = findViewById(R.id.webview)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // Check and request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }

        val replaceButton: Button = findViewById(R.id.button_replace)
        val saveButton: Button = findViewById(R.id.button_save)
        val cancelButton: Button = findViewById(R.id.button_cancel)
        val backIcon: ImageButton = findViewById(R.id.backIcon)


        replaceButton.setOnClickListener {
            openFilePicker()
        }

        saveButton.setOnClickListener {
            showConfirmSave()
        }

        cancelButton.setOnClickListener {
            finish()
        }

        backIcon.setOnClickListener {
            finish()
        }

        // Load PDF if already selected
        //pdfBase64?.let { loadPdfDocument(it) }
        getDocument { docCliente ->
            if (docCliente != null) {
                // Maneja el documento recibido
                // Por ejemplo, puedes mostrarlo en la interfaz de usuario o procesarlo de alguna manera
                loadPdfDocument(docCliente.documentoBase64)
            } else {
                // Maneja el caso en que el documento no se encontró o hubo un error
                Toast.makeText(this, "No se pudo obtener el documento", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, PICK_PDF_REQUEST_CODE)
    }

    private fun saveDocument() {
        val base64Pdf = pdfBase64

        if (base64Pdf.isNullOrBlank()) {
            Toast.makeText(this, "No hay un documento cargado", Toast.LENGTH_SHORT).show()
        } else {
            // Crear el objeto DocumentRequestModel
            val documentRequest = DocRequestModel(
                idDocumentoCliente = idDoc.toInt(), // Ajusta según sea necesario
                documentoBase64 = base64Pdf,
                estatus = 0 // Ajusta según sea necesario
            )

            // Enviar el objeto a la API
            sendPdfToApi(documentRequest)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                pdfBase64 = getBase64FromUri(uri)
                pdfBase64?.let { loadPdfDocument(it) }
                // Optionally update your UI to indicate a PDF has been selected
            }
        }
    }

    private fun getBase64FromUri(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream?.read(buffer).also { bytesRead = it ?: -1 } != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead)
            }
            Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun loadPdfDocument(docBase64Raw: String) {
        webView.loadUrl("file:///android_asset/viewer.html")
        var docBase64 = docBase64Raw.replace(" ", "")

        if (docBase64.equals("") || docBase64.isEmpty()) {
            // colocar un documento por defecto
            docBase64 = "data:application/pdf;base64,JVBERi0xL"
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    val encodedBase64 = Uri.encode(docBase64)
                    webView.evaluateJavascript("renderPDF('$encodedBase64')", null)
                }
            }
            Toast.makeText(contexto, "No se encontraron documentos, sube uno", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(contexto, "Cargando...", Toast.LENGTH_SHORT).show()
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    val encodedBase64 = Uri.encode(docBase64)
                    webView.evaluateJavascript("renderPDF('$encodedBase64')", null)
                }
            }
        }

    }

    // API
    private fun getDocument(callback: (DocsClienteModel?) -> Unit) {
        val cliente = session.getClient()
        val idCliente = cliente?.get("idCliente")?.asInt
        val idDocStr = idDoc // Asegúrate de obtener idDocumento de manera correcta
        progressBar.visibility = View.VISIBLE

        if (idCliente != null && idDocStr != null) {
            val idDoc: Int? = try {
                idDocStr.toInt()
            } catch (e: NumberFormatException) {
                Log.e("Documents", "Error al convertir idDoc a entero: ${e.message}")
                progressBar.visibility = View.GONE
                null
            }

            RetrofitClient.instance.getDocs(idCliente).enqueue(object : Callback<List<DocsClienteModel>> {
                override fun onResponse(call: Call<List<DocsClienteModel>>, response: Response<List<DocsClienteModel>>) {
                    if (response.isSuccessful) {
                        val documentos = response.body()
                        var docCliente: DocsClienteModel? = null
                        if (documentos != null) {
                            for (documento in documentos) {
                                if (documento.idDocumentoCliente == idDoc) {
                                    docCliente = documento
                                }
                            }
                        } else {
                            Toast.makeText(contexto, "No se recibieron documentos", Toast.LENGTH_SHORT).show()
                        }
                        progressBar.visibility = View.GONE
                        callback(docCliente)

                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(contexto, "Error al obtener documentos: ${response.message()}", Toast.LENGTH_SHORT).show()
                        Log.e("Documents", "Error: ${response.message()}")
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<List<DocsClienteModel>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e("Documents", "Error: ${t.message}")
                    Toast.makeText(contexto, "Error al realizar la solicitud: ${t.message}", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            })
        } else {
            progressBar.visibility = View.GONE
            Toast.makeText(contexto, "Error al obtener el id del cliente o el idDocumento", Toast.LENGTH_SHORT).show()
            finish()
            callback(null)
        }
    }

    private fun sendPdfToApi(param: DocRequestModel) {
        progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.postDocs(param).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(contexto, "Documento subido exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(contexto, "Error en la respuesta: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(contexto, "Error en la solicitud: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showConfirmSave() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Guardar Documento")
        builder.setMessage("Está seguro que desea guardar este documento?")

        builder.setPositiveButton("Aceptar") { dialog, which ->
            saveDocument()
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }


}
