package com.example.financlick_movilwear.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.financlick_movilwear.MainActivity
import com.example.financlick_movilwear.items.CardCreditItem
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.items.CardProductItem
import com.example.financlick_movilwear.models.CreditoModel
import com.example.financlick_movilwear.ui.RequestsActivty
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

class CardCreditAdapter (private var items: List<CardCreditItem>, val context:Context): RecyclerView.Adapter<CardCreditAdapter.CardCreditViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewTye: Int): CardCreditViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_cred_item, parent, false)
        return CardCreditViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardCreditViewHolder, position: Int) {
        val credito = items[position]

        holder.titleView.text = credito.producto
        holder.montoView.text = "$ " + credito.monto.toString()
        holder.estatusView.text = credito.estatus

        when (credito.estatus.toLowerCase()) {
            "pendiente" -> holder.estatusView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.pendiente))
            "firmado" -> holder.estatusView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.aceptado))
            "en curso" -> holder.estatusView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.aceptado))
            "finalizado" -> holder.estatusView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.rechazado))
            else -> holder.estatusView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.revision)) // Color por defecto en caso de que no coincida con ningún estatus
        }

        holder.creditoView.text = credito.idCredito.toString()
        holder.buttonView.setOnClickListener{
            showDetailDialog(context, credito)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newProducts: List<CardCreditItem>) {
        items = newProducts
        notifyDataSetChanged()
    }

    inner class CardCreditViewHolder(view: View): RecyclerView.ViewHolder(view){
        val titleView: TextView = view.findViewById(R.id.producto)
        val montoView: TextView = view.findViewById(R.id.monto)
        val estatusView: TextView = view.findViewById(R.id.estatus)
        val creditoView: TextView = view.findViewById(R.id.idCredito)
        val buttonView: Button = view.findViewById(R.id.cardButtonCred)
    }

    private fun showDetailDialog(context: Context, credito: CardCreditItem) {
        // Suponemos que fechaActivacion tiene el formato "yyyy-MM-dd"
        // Ajusta este formato según tu caso
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("dd/MM/yyyy")

        val fechaActivacionString = credito.fechaActivacion ?: ""
        val fechaActivacionDate = try {
            inputFormat.parse(fechaActivacionString)
        } catch (e: Exception) {
            Date() // Fecha por defecto si la conversión falla
        }

        val message = """
            Producto: ${credito.producto ?: "N/A"}
            Monto: $ ${credito.monto ?: 0.0}
            Número de Pagos: ${credito.pagos ?: 0}
            Periodicidad: ${credito.periodicidad ?: "N/A"}
            Interés: % ${credito.interes ?: 0.0}
            Fecha de Activación: ${outputFormat.format(fechaActivacionDate)}
        """.trimIndent()

        AlertDialog.Builder(context)
            .setTitle("Detalle del Crédito")
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}