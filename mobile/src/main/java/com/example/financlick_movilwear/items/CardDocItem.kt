package com.example.financlick_movilwear.items

import com.example.financlick_movilwear.models.DocDetailModel
import com.example.financlick_movilwear.models.DocsClienteModel

data class CardDocItem (
    val imageBase64: String,
    val title: String,
    val description: String,
    val status: String,
    val buttonText: String,
    val isPdf: Boolean,
    val doc: DocsClienteModel
){

}