package com.example.financlick_movilwear.items

data class CardCreditItem (
    val producto: String,
    val monto: Double,
    val estatus: String,
    val idCredito: Int,
    val pagos: Int,
    val periodicidad: String,
    val interes: Double,
    val fechaActivacion: String? = null
) {

}