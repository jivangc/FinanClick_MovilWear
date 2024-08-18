package com.example.financlick_movilwear.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.presentation.models.DocItemModel

class DocumentItemAdapter (private var documents: MutableList<DocItemModel>) : RecyclerView.Adapter<DocumentItemAdapter.DocumentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.document_item, parent, false)
        return DocumentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val product = documents[position]
        holder.tvDocumentName.text = product.name
        holder.tvTipoPersona.text = product.type
        holder.tvDocumentStatus.text = product.status
        holder.tvDocumentStatus.setTextColor(product.statusColor)
    }

    override fun getItemCount(): Int = documents.size

    fun updateItems(newItems: MutableList<DocItemModel>) {
        documents.clear()
        documents.addAll(newItems)
        notifyDataSetChanged()
    }

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDocumentName: TextView = itemView.findViewById(R.id.tvDocumentName)
        val tvTipoPersona: TextView = itemView.findViewById(R.id.tvTipoPersona)
        val tvDocumentStatus: TextView = itemView.findViewById(R.id.tvDocumentStatus)
    }
}