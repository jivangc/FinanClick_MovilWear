package com.example.financlick_movilwear.models

import com.google.gson.annotations.SerializedName

class DetalleProductoModel (
    @SerializedName("idDetalleProductos") val idDetalleProductos: Int,
    @SerializedName("idProducto") val idProducto: Int,
    @SerializedName("valor") val valor: Double,
    @SerializedName("tipoValor") val tipoValor: String,
    @SerializedName("iva") val iva: Double,
    @SerializedName("idConcepto") val idConcepto: Int,
    @SerializedName("estatus") val estatus: Int,
    @SerializedName("idConceptoNavigation") val idConceptoNavigation: ConceptoModel
){
}