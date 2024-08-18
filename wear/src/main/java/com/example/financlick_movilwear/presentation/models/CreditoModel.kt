package com.example.financlick_movilwear.presentation.models

import com.google.gson.annotations.SerializedName

data class CreditoModel (
    @SerializedName("idCredito") val idCredito: Int,
    @SerializedName("idProducto") val idProducto: Int,
    @SerializedName("monto") val monto: Double,
    @SerializedName("estatus") val estatus: Int,
    @SerializedName("iva") val iva: Double,
    @SerializedName("periodicidad") val periodicidad: String,
    @SerializedName("fechaFirma") val fechaFirma: String? = null,
    @SerializedName("fechaActivacion") val fechaActivacion: String? = null,
    @SerializedName("numPagos") val numPagos: Int,
    @SerializedName("interesOrdinario") val interesOrdinario: Double,
    @SerializedName("idPromotor") val idPromotor: Int,
    @SerializedName("idCliente") var idCliente: Int,
    @SerializedName("interesMoratorio") val interesMoratorio: Double
){
}