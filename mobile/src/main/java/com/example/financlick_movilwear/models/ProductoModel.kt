package com.example.financlick_movilwear.models

import com.google.gson.annotations.SerializedName

data class ProductoModel (
    @SerializedName("idProducto") val idProducto: Int,
    @SerializedName("nombreProducto") val nombreProducto: String,
    @SerializedName("reca") val reca: Int,
    @SerializedName("metodoCalculo") val metodoCalculo: String,
    @SerializedName("subMetodo") val subMetodo: String,
    @SerializedName("monto") val monto: Double,
    @SerializedName("periodicidad") val periodicidad: String,
    @SerializedName("numPagos") val numPagos: Int,
    @SerializedName("interesAnual") val interesAnual: Double,
    @SerializedName("iva") val iva: Double,
    @SerializedName("interesMoratorio") val interesMoratorio: Double,
    @SerializedName("pagoAnticipado") val pagoAnticipado: String?, // Puede ser null
    @SerializedName("aplicacionDePagos") val aplicacionDePagos: String,
    @SerializedName("idEmpresa") val idEmpresa: Int,
    @SerializedName("idDetalleProductos") val idDetalleProductos: Int?, // Puede ser null
    @SerializedName("estatus") val estatus: Int,
    @SerializedName("detalleProductos") val detalleProductos: List<DetalleProductoModel>
){
}