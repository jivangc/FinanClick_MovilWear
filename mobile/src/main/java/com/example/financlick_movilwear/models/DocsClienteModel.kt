package com.example.financlick_movilwear.models

import com.google.gson.annotations.SerializedName

data class DocsClienteModel (
    @SerializedName("idDocumentoCliente") val idDocumentoCliente: Int,
    @SerializedName("documentoBase64") val documentoBase64: String,
    @SerializedName("estatus") val estatus: Int,
    @SerializedName("idDocumento") val idDocumento: Int,
    @SerializedName("idCliente") val idCliente: Int,
    ){
}