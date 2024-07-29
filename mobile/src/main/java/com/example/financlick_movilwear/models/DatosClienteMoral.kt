package com.example.financlick_movilwear.models

import com.google.gson.annotations.SerializedName

data class DatosClienteMoral(
    @SerializedName("idClienteMoral") val idClienteMoral: Int,
    @SerializedName("idPersonaMoral") val idPersonaMoral: Int?,
    @SerializedName("nombreRepLegal") val nombreRepLegal: String,
    @SerializedName("rfcrepLegal") val rfcrepLegal: String,
    @SerializedName("idCliente") val idCliente: Int?,
    @SerializedName("idClienteNavigation") val idClienteNavigation: ClienteModel? = null,
    @SerializedName("idPersonaMoralNavigation") val idPersonaMoralNavigation: PersonaMoral? = null
)