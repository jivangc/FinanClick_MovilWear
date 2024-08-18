package com.example.financlick_movilwear.presentation.adapters

import com.example.financlick_movilwear.presentation.models.RequestModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.financlick_movilwear.R


class RequestItemAdapter(private var products: MutableList<RequestModel>) :
    RecyclerView.Adapter<RequestItemAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.request_item, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.tvProductName.text = product.name
        holder.tvProductPrice.text = product.price
        holder.tvProductCode.text = product.code
        holder.tvProductStatus.text = product.status
        holder.tvProductStatus.setTextColor(product.statusColor)
    }

    override fun getItemCount(): Int = products.size

    fun updateItems(newItems: MutableList<RequestModel>) {
        products.clear()
        products.addAll(newItems)
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvProductCode: TextView = itemView.findViewById(R.id.tvProductCode)
        val tvProductStatus: TextView = itemView.findViewById(R.id.tvProductStatus)
    }
}