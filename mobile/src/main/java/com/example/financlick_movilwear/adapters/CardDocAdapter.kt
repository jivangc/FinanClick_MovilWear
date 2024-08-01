package com.example.financlick_movilwear.adapters

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.financlick_movilwear.R
import com.example.financlick_movilwear.items.CardDocItem
import com.example.financlick_movilwear.ui.EditDocumentActivity
import java.io.File

class CardDocAdapter(private var items: List<CardDocItem>) : RecyclerView.Adapter<CardDocAdapter.CardDocViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDocViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)
        return CardDocViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardDocViewHolder, position: Int) {
        val cardData = items[position]

        try {
            // Decodifica la imagen base64
            val decodedString = Base64.decode(cardData.imageBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            if (cardData.isPdf) {
                val tempFile = File.createTempFile("tempPdf", ".pdf")
                tempFile.writeBytes(decodedString)

                val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY))
                val page = pdfRenderer.openPage(0)
                val width = page.width
                val height = page.height
                val pdfBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                page.render(pdfBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                pdfRenderer.close()

                holder.imageView.setImageBitmap(pdfBitmap)
                tempFile.delete()
            } else {
                holder.imageView.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            holder.imageView.setImageResource(R.drawable.imgplaceholder)
        }

        holder.titleView.text = cardData.title
        holder.descriptionView.text = cardData.description
        holder.statusView.text = cardData.status
        holder.cardButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditDocumentActivity::class.java)
            intent.putExtra("docs", cardData.doc.idDocumentoCliente.toString())
            // Usa el contexto de la vista para iniciar la actividad
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: List<CardDocItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class CardDocViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.cardImage)
        val titleView: TextView = view.findViewById(R.id.cardTitle)
        val descriptionView: TextView = view.findViewById(R.id.cardDescription)
        val statusView: TextView = view.findViewById(R.id.status)
        val cardButton: Button = view.findViewById(R.id.cardButton)
    }
}
