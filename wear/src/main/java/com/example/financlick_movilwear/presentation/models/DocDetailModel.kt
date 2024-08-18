package com.example.financlick_movilwear.presentation.models

import com.google.gson.annotations.SerializedName

data class DocDetailModel (
    @SerializedName("idCatalogoDocumento") val idCatalogoDocumento: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String
){
}