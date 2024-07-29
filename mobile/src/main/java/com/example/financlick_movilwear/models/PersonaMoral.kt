package com.example.financlick_movilwear.models
import com.google.gson.annotations.SerializedName

data class PersonaMoral(
    @SerializedName("idPersonaMoral") val idPersonaMoral: Int,
    @SerializedName("razonSocial") val razonSocial: String,
    @SerializedName("razonComercial") val razonComercial: String?,
    @SerializedName("fechaConstitucion") val fechaConstitucion: String, // Usar formato adecuado para Date
    @SerializedName("rfc") val rfc: String,
    @SerializedName("nacionalidad") val nacionalidad: String,
    @SerializedName("paisRegistro") val paisRegistro: String,
    @SerializedName("estadoRegistro") val estadoRegistro: String,
    @SerializedName("ciudadRegistro") val ciudadRegistro: String,
    @SerializedName("numEscritura") val numEscritura: String,
    @SerializedName("fechaRppc") val fechaRppc: String?, // Usar formato adecuado para Date
    @SerializedName("nombreNotario") val nombreNotario: String?,
    @SerializedName("numNotario") val numNotario: String?,
    @SerializedName("folioMercantil") val folioMercantil: String?,
    @SerializedName("calle") val calle: String,
    @SerializedName("numExterior") val numExterior: String,
    @SerializedName("numInterior") val numInterior: String?,
    @SerializedName("colonia") val colonia: String,
    @SerializedName("codigoPostal") val codigoPostal: String,
    @SerializedName("paisResidencia") val paisResidencia: String,
    @SerializedName("estadoResidencia") val estadoResidencia: String,
    @SerializedName("ciudadResidencia") val ciudadResidencia: String
)