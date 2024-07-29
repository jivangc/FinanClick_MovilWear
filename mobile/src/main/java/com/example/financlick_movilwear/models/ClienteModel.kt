package com.example.financlick_movilwear.models
import com.google.gson.annotations.SerializedName

data class ClienteModel(
    @SerializedName("idCliente") val idCliente: Int,
    @SerializedName("regimenFiscal") val regimenFiscal: String? = null,
    @SerializedName("idEmpresa") val idEmpresa: Int? = null,
    @SerializedName("estatus") val estatus: Int
)