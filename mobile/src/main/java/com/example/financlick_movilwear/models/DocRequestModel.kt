package com.example.financlick_movilwear.models

data class DocRequestModel(
    val idDocumentoCliente: Int,
    val documentoBase64: String,
    val estatus: Int
)