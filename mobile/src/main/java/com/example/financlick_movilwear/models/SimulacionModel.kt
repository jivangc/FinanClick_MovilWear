package com.example.financlick_movilwear.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class SimulacionModel (
    @SerializedName("numPago") val numPago: Int,
    @SerializedName("saldoInsoluto") val saldoInsoluto: Double,
    @SerializedName("capital") val capital: Double,
    @SerializedName("interes") val interes: Double,
    @SerializedName("ivaSobreInteres") val ivaSobreInteres: Double,
    @SerializedName("interesMasIva") val interesMasIva: Double,
    @SerializedName("pagoFijo") val pagoFijo: Double,
    @SerializedName("fechaInicio") val fechaInicio: Date,
    @SerializedName("fechaFin") val fechaFin: Date
){
}