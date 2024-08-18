package com.example.financlick_movilwear.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.models.ProductoModel
import com.example.financlick_movilwear.models.SimulacionRequestModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SimulationFormActivity : AppCompatActivity() {

    private lateinit var productRaw: String
    private lateinit var producto :TextView
    private lateinit var metodoCalculo :Spinner
    private lateinit var subMetodo :Spinner
    private lateinit var montoCredito :EditText
    private lateinit var numPagos :EditText
    private lateinit var interesMoratorio :EditText
    private lateinit var periodicidad :Spinner
    private lateinit var fechaInicio :EditText
    private lateinit var interesAnual :EditText
    private lateinit var iva :EditText
    private lateinit var btnSimular: Button
    private lateinit var btnCancelar: Button
    lateinit var request: SimulacionRequestModel
    lateinit var idProducto: String
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_simulation_form)
        productRaw = intent.getStringExtra("product").toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.your_status_bar_color)
            window.navigationBarColor = resources.getColor(R.color.your_navigation_bar_color)
        }

        val editTextFechaInicio: EditText = findViewById(R.id.editTextFechaInicio)
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

        initForm(productRaw)

        editTextFechaInicio.setOnClickListener {
            showDatePickerDialog(editTextFechaInicio)
        }

        btnSimular = findViewById(R.id.buttonSimular)
        btnCancelar = findViewById(R.id.buttonCancelar)
        btnSimular.setOnClickListener {
            if (validForm()){
                initRequest()
                Log.i("REQUESSST", request.toString())
                val intent = Intent(this, SimulationActivity::class.java)
                intent.putExtra("request", gson.fromJson(gson.toJson(request), JsonObject::class.java).toString())
                intent.putExtra("idProducto", idProducto)
                startActivity(intent)
                finish()
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val format = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(format, Locale.US)
            editText.setText(sdf.format(calendar.time))
        }

        val dialog = DatePickerDialog(this, R.style.CustomDatePicker, dateSetListener,
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorPrimary))
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.button_cancel_bg))
    }

    private fun initForm(raw: String){
        val product = gson.fromJson(raw, JsonObject::class.java)
        Log.e("PRODUCTO", product.toString())

        idProducto = product.get("idProducto").toString()
        producto = findViewById(R.id.textViewProducto)
        metodoCalculo = findViewById(R.id.spinnerMetodoCalculo)
        subMetodo = findViewById(R.id.spinnerSubMetodo)
        montoCredito = findViewById(R.id.editTextMontoCredito)
        numPagos = findViewById(R.id.editTextNumeroPagos)
        interesMoratorio = findViewById(R.id.editTextInteresMoratorio)
        periodicidad = findViewById(R.id.spinnerPeriodicidad)
        fechaInicio = findViewById(R.id.editTextFechaInicio)
        interesAnual = findViewById(R.id.editTextInteresAnual)
        iva = findViewById(R.id.editTextIVA)

        if (product.get("numPagos").asString.equals("Quincenal", true)){
            periodicidad.setSelection(0)
        } else if (product.get("numPagos").asString.equals("Mensual", true)){
            periodicidad.setSelection(1)
        } else if (product.get("numPagos").asString.equals("Mensual", true)){
            periodicidad.setSelection(2)
        }

        if (product.get("submetodo").asString.equals("Insolutos", true)){
            subMetodo.setSelection(0)
        } else if (product.get("submetodo").asString.equals("Globales", true)){
            subMetodo.setSelection(1)
        }

        producto.text = product.get("nombreProducto").asString
        numPagos?.text = Editable.Factory.getInstance().newEditable(product.get("numPagos").asInt.toString())
        interesMoratorio?.text = Editable.Factory.getInstance().newEditable(product.get("interesMoratorio").asDouble.toString())
        montoCredito?.text = Editable.Factory.getInstance().newEditable(product.get("monto").asDouble.toString())
        interesAnual?.text = Editable.Factory.getInstance().newEditable(product.get("interesAnual").asDouble.toString())
        iva?.text = Editable.Factory.getInstance().newEditable(product.get("iva").asInt.toString())

    }

    private fun initRequest() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val finalFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.isLenient = false // Asegurarse de que el análisis de la fecha sea estricto

        try {
            // Obtener los valores de los campos de entrada
            val montoCredito = montoCredito.text.toString().toDouble()
            val numPagos = numPagos.text.toString().toInt()
            val periodicidad = periodicidad.selectedItem.toString()
            val fechaInicio = fechaInicio.text.toString()
            val interesAnual = interesAnual.text.toString().toDouble()
            val interesMoratorio = interesMoratorio.text.toString().toDouble()
            val iva = iva.text.toString().toDouble()
            val metodoCalculo = metodoCalculo.selectedItem.toString()
            val subMetodo = subMetodo.selectedItem.toString().toLowerCase()
                .replace("saldos", "")
                .replace(" ", "")
                .trim()
            val monto = montoCredito

            // Analizar la fecha
            val parsedDate = dateFormat.parse(fechaInicio)
            val finalDate = finalFormat.format(parsedDate)

            // Crear el objeto request
            request = SimulacionRequestModel(
                metodoCalculo,
                subMetodo,
                periodicidad,
                numPagos,
                interesAnual,
                iva,
                false,
                finalDate,
                monto,
                interesMoratorio
            )
        } catch (e: ParseException) {
            e.printStackTrace()
            Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            Toast.makeText(this, "Por favor, ingresa números válidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validForm(): Boolean{
        var valido = true
        val product = gson.fromJson(productRaw, JsonObject::class.java)

        if (montoCredito.text.toString().isEmpty()){
            montoCredito.error = "Campo Obligatorio"
            valido = false
        }

        if (numPagos.text.toString().isEmpty()){
            numPagos.error = "Campo Obligatorio"
            valido = false
        }

        if (interesMoratorio.text.toString().isEmpty()){
            interesMoratorio.error = "Campo Obligatorio"
            valido = false
        }

        if (fechaInicio.text.toString().isEmpty()){
            fechaInicio.error = "Campo Obligatorio"
            valido = false
        }

        if (interesAnual.text.toString().isEmpty()){
            interesAnual.error = "Campo Obligatorio"
            valido = false
        }

        if (iva.text.toString().isEmpty()){
            iva.error = "Campo Obligatorio"
            valido = false
        }

        var interesMor = interesMoratorio.text.toString().toDouble()
        var interesOrd = interesAnual.text.toString().toDouble()

        if (interesMor > product.get("interesMoratorio").asDouble){
            interesMoratorio.error = "El interes no debe ser mayor al declarado % ${product.get("interesMoratorio").asDouble}"
            valido = false
        }

        if (interesOrd > product.get("interesAnual").asDouble){
            interesAnual.error = "El interes no debe ser mayor al declarado % ${product.get("interesAnual").asDouble}"
            valido = false
        }

        return valido
    }

}