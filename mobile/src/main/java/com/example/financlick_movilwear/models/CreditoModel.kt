package com.example.financlick_movilwear.models
import com.google.gson.annotations.SerializedName

data class CreditoModel(
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
    @SerializedName("interesMoratorio") val interesMoratorio: Double,
    @SerializedName("avals") val avals: List<Any>? = null,
    @SerializedName("obligados") val obligados: List<ObligadoModel>? = null,
    @SerializedName("nombreCliente") val nombreCliente: String? = null,
    @SerializedName("regimenFiscal") val regimenFiscal: String? = null
){

}
