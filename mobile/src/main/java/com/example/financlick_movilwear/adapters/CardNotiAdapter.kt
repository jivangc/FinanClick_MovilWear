package com.example.financlick_movilwear.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.items.CardNotiItem

class CardNotiAdapter(private var items: List<CardNotiItem>): RecyclerView.Adapter<CardNotiAdapter.CardNotiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): CardNotiAdapter.CardNotiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_noti_item, parent, false)
        return CardNotiViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardNotiAdapter.CardNotiViewHolder, position: Int) {
        // aqui se cargan a los datos de la notificacion
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CardNotiItem>){
        items = newItems
        notifyDataSetChanged()
    }

    inner class CardNotiViewHolder(view: View): RecyclerView.ViewHolder(view){
        // clase para mapear los datos del item de notificacion
    }

}