package com.example.financlick_movilwear.models
import com.google.gson.annotations.SerializedName

data class ObligadoModel(
    @SerializedName("idObligado") val idObligado: Int? = null,
    @SerializedName("idCredito") val idCredito: Int? = null,
    @SerializedName("idPersona") val idPersona: Int? = null,
    @SerializedName("idPersonaMoral") val idPersonaMoral: Int? = null,
    @SerializedName("idPersonaNavigation") val idPersonaNavigation: PersonaFisica? = null,
    @SerializedName("idPersonaMoralNavigation") val idPersonaMoralNavigation: PersonaMoral? = null
){

}
