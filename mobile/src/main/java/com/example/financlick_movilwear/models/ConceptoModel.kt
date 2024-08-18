package com.example.financlick_movilwear.models

import com.google.gson.annotations.SerializedName

class ConceptoModel (
    @SerializedName("idConcepto") val idConcepto: Int,
    @SerializedName("nombreConcepto") val nombreConcepto: String,
    @SerializedName("valor") val valor: Double,
    @SerializedName("tipoValor") val tipoValor: String,
    @SerializedName("iva") val iva: Double,
    @SerializedName("estatus") val estatus: Int,
    @SerializedName("idEmpresa") val idEmpresa: Int
){
}