package com.example.financlick_movilwear.items

class CardProductItem(
    val idProducto: Int,
    val nombreProducto: String,
    val metodoCalculo: String,
    val submetodo: String,
    val periodicidad: String,
    val interesMoratorio: Double,
    val monto: Double,
    val numPagos: Int,
    val interesAnual: Double,
    val iva: Double,
    val estatus: Int
) {
}