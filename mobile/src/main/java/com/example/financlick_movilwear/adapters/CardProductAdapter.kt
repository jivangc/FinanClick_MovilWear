package com.example.financlick_movilwear.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.items.CardProductItem
import com.example.financlick_movilwear.ui.SimulationFormActivity
import com.google.gson.Gson

class CardProductAdapter(private var items: List<CardProductItem>): RecyclerView.Adapter<CardProductAdapter.CardProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_prod_item, parent, false)
        return CardProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardProductViewHolder, position: Int) {
        val productItem = items[position]
        val gson = Gson()

        // Configura los datos de la tarjeta
        holder.titleView.text = productItem.nombreProducto
        holder.descriptionView.text = productItem.metodoCalculo
        holder.periodicidadView.text = "Periodicidad: ${productItem.periodicidad}"
        holder.numPagosView.text = "Número de pagos: ${productItem.numPagos}"
        holder.interesAnualView.text = "Interés Anual: ${productItem.interesAnual}%"
        holder.ivaView.text = "IVA: ${productItem.iva}%"
        holder.statusView.text = "Estatus: ${if (productItem.estatus == 1) "Activo" else "Inactivo"}"

        // Configura el botón
        holder.buttonView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SimulationFormActivity::class.java)
            Log.i("CardProductAdapter", "Product clicked: ${gson.toJson(productItem)}")
            intent.putExtra("product", gson.toJson(productItem))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newProducts: List<CardProductItem>) {
        items = newProducts
        notifyDataSetChanged()
    }

    inner class CardProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.cardTitle)
        val descriptionView: TextView = view.findViewById(R.id.cardDescription)
        val periodicidadView: TextView = view.findViewById(R.id.cardPeriodicidad)
        val numPagosView: TextView = view.findViewById(R.id.cardNumPagos)
        val interesAnualView: TextView = view.findViewById(R.id.cardInteresAnual)
        val ivaView: TextView = view.findViewById(R.id.cardIVA)
        val statusView: TextView = view.findViewById(R.id.cardStatus)
        val buttonView: Button = view.findViewById(R.id.cardButton)
    }
}