package com.example.financlick_movilwear.models

import com.google.gson.annotations.SerializedName

data class DatosClienteFisica(
    @SerializedName("idClienteFisica") val idClienteFisica: Int,
    @SerializedName("idPersona") val idPersona: Int?,
    @SerializedName("idCliente") val idCliente: Int?,
    @SerializedName("idClienteNavigation") val idClienteNavigation: ClienteModel? = null,
    @SerializedName("idPersonaNavigation") val idPersonaNavigation: PersonaFisica? = null
)