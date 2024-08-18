package com.example.financlick_movilwear.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class SimulacionRequestModel (
    @SerializedName("metodoCalculo") val metodoCalculo: String,
    @SerializedName("subMetodoCalculo") val subMetodoCalculo: String,
    @SerializedName("periodicidad") val periodicidad: String,
    @SerializedName("numPagos") val numPagos: Int,
    @SerializedName("interesAnual") val interesAnual: Double,
    @SerializedName("iva") val iva: Double,
    @SerializedName("ivaExento") val ivaExento: Boolean,
    @SerializedName("fechaInicio") val fechaInicio: String,
    @SerializedName("monto") val monto: Double,
    @SerializedName("interesMoratorio") val interesMoratorio: Double
){
}