package com.example.financlick_movilwear.models

import com.google.gson.annotations.SerializedName

data class DocDetailModel (
    @SerializedName("idCatalogoDocumento") val idCatalogoDocumento: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("estatus") val estatus: Int,
    @SerializedName("idEmpresa") val idEmpresa: Int
    ){
}