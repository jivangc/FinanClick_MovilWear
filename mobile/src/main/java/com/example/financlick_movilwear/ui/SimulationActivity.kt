package com.example.financlick_movilwear.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.config.SessionManager
import com.example.financlick_movilwear.models.ClienteModel
import com.example.financlick_movilwear.models.ConceptoModel
import com.example.financlick_movilwear.models.CreditoModel
import com.example.financlick_movilwear.models.DetalleProductoModel
import com.example.financlick_movilwear.models.ProductoModel
import com.example.financlick_movilwear.models.SimulacionModel
import com.example.financlick_movilwear.models.SimulacionRequestModel
import com.example.financlick_movilwear.services.RetrofitClient
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.properties.Delegates


class SimulationActivity : AppCompatActivity() {

    lateinit var request: SimulacionRequestModel
    lateinit var requestCredito: CreditoModel
    lateinit var requestRaw: String
    lateinit var producto: String
    lateinit var backButton: ImageButton
    lateinit var btnSolicitar: Button
    lateinit var session: SessionManager
    lateinit var clienteRaw: ClienteModel
    var cliente: Int = 0
    val context = this
    private lateinit var progressBar: ProgressBar
    val gson = Gson()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_simulation)
        requestRaw = intent.getStringExtra("request").orEmpty()
        Log.i("REQUEST RAW", requestRaw)
        request = gson.fromJson(requestRaw, SimulacionRequestModel::class.java)
        producto = intent.getStringExtra("idProducto").orEmpty()
        progressBar = findViewById(R.id.progressBar)
        backButton = findViewById(R.id.backIcon)
        btnSolicitar = findViewById(R.id.button_request)
        session = SessionManager(this)
        clienteRaw = gson.fromJson(session.getClient(), ClienteModel::class.java)
        cliente = clienteRaw.idCliente

        backButton.setOnClickListener {
            finish()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.your_status_bar_color)
            window.navigationBarColor = resources.getColor(R.color.your_navigation_bar_color)
        }

        val tableConcepto = findViewById<TableLayout>(R.id.tableConcepto)
        val tableAmortizacion = findViewById<TableLayout>(R.id.tableAmortizacion)

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

        getProductConcept(producto){ result ->
            progressBar.visibility = View.VISIBLE
            if (result != null){
                addConceptoDataToTable(tableConcepto, result.detalleProductos)
            }else{
                progressBar.visibility = View.GONE
                finish()
            }
        }

        getSimulacion(request){ results ->
            addAmortizacionDataToTable(tableAmortizacion, results)
            progressBar.visibility = View.GONE
        }

        btnSolicitar.setOnClickListener{
            initCreditRequest()
            showConfirmSave()
        }
    }

    // CARGAR DATA A LAS TABLAS
    private fun addConceptoDataToTable(tableLayout: TableLayout, data: List<DetalleProductoModel>) {
        for (item in data) {
            var concepto = item.idConceptoNavigation
            val tableRow = TableRow(this)

            val textView1 = TextView(this)
            textView1.text = concepto.nombreConcepto
            textView1.setPadding(8, 8, 8, 8)
            tableRow.addView(textView1)

            val textView2 = TextView(this)
            textView2.text = concepto.tipoValor
            textView2.setPadding(8, 8, 8, 8)
            tableRow.addView(textView2)

            val textView3 = TextView(this)
            textView3.text = concepto.valor.toString()
            textView3.setPadding(8, 8, 8, 8)
            tableRow.addView(textView3)

            val textView4 = TextView(this)
            textView4.text = concepto.valor.toString()
            textView4.setPadding(8, 8, 8, 8)
            tableRow.addView(textView4)

            tableLayout.addView(tableRow)
        }
    }

    private fun addAmortizacionDataToTable(tableLayout: TableLayout, data: List<SimulacionModel>) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")

        // Configuración del estilo de las celdas
        val cellPadding = 16
        val cellBackgroundColor = Color.parseColor("#FFFFFF")
        val cellTextColor = Color.parseColor("#000000")
        val cellTextSize = 14f

        for (item in data) {
            val tableRow = TableRow(this)

            // Función para configurar un TextView
            fun createStyledTextView(text: String): TextView {
                return TextView(this).apply {
                    this.text = text
                    setPadding(cellPadding, cellPadding, cellPadding, cellPadding)
                    setBackgroundColor(cellBackgroundColor)
                    setTextColor(cellTextColor)
                    textSize = cellTextSize
                    gravity = Gravity.CENTER
                    setBackgroundResource(R.drawable.cell_border) // Fondo con borde
                }
            }

            tableRow.addView(createStyledTextView(item.numPago.toString()))
            tableRow.addView(createStyledTextView(dateFormat.format(item.fechaInicio)))
            tableRow.addView(createStyledTextView(dateFormat.format(item.fechaFin)))
            tableRow.addView(createStyledTextView(daysBetween(item.fechaInicio, item.fechaFin).toString()))
            tableRow.addView(createStyledTextView(item.saldoInsoluto.toString()))
            tableRow.addView(createStyledTextView(item.capital.toString()))
            tableRow.addView(createStyledTextView(item.interes.toString()))
            tableRow.addView(createStyledTextView(item.ivaSobreInteres.toString()))
            tableRow.addView(createStyledTextView(item.interesMasIva.toString()))
            tableRow.addView(createStyledTextView(item.pagoFijo.toString()))

            tableLayout.addView(tableRow)
        }
    }

    fun dateToLocalDate(date: Date): LocalDate {
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    fun daysBetween(startDate: Date, endDate: Date): Long {
        val startLocalDate = dateToLocalDate(startDate)
        val endLocalDate = dateToLocalDate(endDate)
        return ChronoUnit.DAYS.between(startLocalDate, endLocalDate)
    }

    // OBTENER CONCEPTOS SI APLICA
    private fun getSimulacion(request: SimulacionRequestModel, onComplete: (List<SimulacionModel>) -> Unit) {
        //progressBar.visibility = View.VISIBLE
        RetrofitClient.instance.postSimulacion(request).enqueue(object : Callback<List<SimulacionModel>> {
            override fun onResponse(call: Call<List<SimulacionModel>>, response: Response<List<SimulacionModel>>) {
                Log.i("Simulacion", response.toString())
                if (response.isSuccessful) {
                    val simulacionList = response.body()
                    if (simulacionList != null) {
                        // Maneja la respuesta exitosa aquí
                        onComplete(simulacionList)
                    } else {
                        // Manejar el caso en que la respuesta sea nula
                        onComplete(emptyList())
                    }
                } else {
                    Log.e("Simulacion", "Error en la respuesta: ${response.code()}")
                    // Manejar el error, por ejemplo, mostrar un mensaje de error
                    onComplete(emptyList())
                }
                //progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<SimulacionModel>>, t: Throwable) {
                Log.e("Simulacion", "Error en la solicitud: ${t.message}")
                // Manejar la falla de la llamada, por ejemplo, mostrar un mensaje de error
                onComplete(emptyList())
                //progressBar.visibility = View.GONE
            }
        })
    }

    private fun getProductConcept(request: String, onComplete: (ProductoModel?) -> Unit) {
        var idProducto: Int = request.toInt()
        RetrofitClient.instance.getProduct(idProducto).enqueue(object : Callback<ProductoModel> {
            override fun onResponse(call: Call<ProductoModel>, response: Response<ProductoModel>) {
                Log.i("Producto", response.toString())
                if (response.isSuccessful) {
                    val producto = response.body()
                    if (producto != null) {
                        onComplete(producto)
                    } else {
                        Log.i("Producto", "No se encontró ningún producto en la respuesta")
                        onComplete(null)
                    }
                } else {
                    Log.i("Producto", "La respuesta no fue exitosa: ${response.code()}")
                    onComplete(null)
                }
            }

            override fun onFailure(call: Call<ProductoModel>, t: Throwable) {
                Log.e("Producto", "Error en la llamada a la API: ${t.message}")
                onComplete(null)
            }
        })
    }

    private fun postRequest(request: CreditoModel, onComplete: (Boolean?) -> Unit){
        RetrofitClient.instance.postCredito(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("SOLICITUD", response.body().toString())
                if (response.isSuccessful){
                    onComplete(true)
                }else{
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("SOLICITUD", "Error en la solicitud: ${t.message}")
                onComplete(false)
            }
        })
    }

    private fun initCreditRequest(){
        requestCredito = CreditoModel(
            idCredito = 0,
            idProducto = producto.toInt(),
            monto = request.monto,
            estatus = 1,
            iva = request.iva,
            periodicidad = request.periodicidad,
            fechaFirma = null,
            fechaActivacion = null,
            numPagos = request.numPagos,
            interesOrdinario = request.interesAnual,
            idPromotor = 0,
            idCliente = cliente,
            interesMoratorio = request.interesMoratorio,
            avals = emptyList(),
            obligados = emptyList(),
            nombreCliente = null,
            regimenFiscal = null
        )
    }

    private fun showConfirmSave() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Realizar Solicitud")
        builder.setMessage("Estas seguro de realizar esta solicitud de credito?, una vez realizado no podras modificar los datos ingresados")

        builder.setPositiveButton("Aceptar") { dialog, which ->
            postRequest(requestCredito){ result ->
                if(result == true){
                    Toast.makeText(this, "El credito ha sido solicitado, espere el contacto de su asesor para continuar con el proceso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, RequestsActivty::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this, "La solicitud no se pudo realizar, intentelo nuevamente mas tarde.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

}