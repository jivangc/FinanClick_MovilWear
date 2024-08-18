package com.example.financlick_movilwear.presentation.models

import com.google.gson.annotations.SerializedName

data class DocsClienteModel (
    @SerializedName("idDocumentoCliente") val idDocumentoCliente: Int,
    @SerializedName("estatus") val estatus: Int,
    @SerializedName("idDocumento") val idDocumento: Int,
    @SerializedName("idCliente") val idCliente: Int,
){
}