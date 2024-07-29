package com.example.financlick_movilwear.models

import com.google.gson.annotations.SerializedName

data class PersonaFisica(
    @SerializedName("idPersona") val idPersona: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("apellidoPaterno") val apellidoPaterno: String,
    @SerializedName("apellidoMaterno") val apellidoMaterno: String,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String, // Usar formato adecuado para Date
    @SerializedName("paisNacimiento") val paisNacimiento: String,
    @SerializedName("estadoNacimiento") val estadoNacimiento: String,
    @SerializedName("genero") val genero: String,
    @SerializedName("rfc") val rfc: String,
    @SerializedName("curp") val curp: String,
    @SerializedName("claveElector") val claveElector: String?,
    @SerializedName("nacionalidad") val nacionalidad: String,
    @SerializedName("estadoCivil") val estadoCivil: String?,
    @SerializedName("regimenMatrimonial") val regimenMatrimonial: String?,
    @SerializedName("nombreConyuge") val nombreConyuge: String?,
    @SerializedName("calle") val calle: String,
    @SerializedName("numExterior") val numExterior: String,
    @SerializedName("numInterior") val numInterior: String?,
    @SerializedName("colonia") val colonia: String,
    @SerializedName("codigoPostal") val codigoPostal: String,
    @SerializedName("paisResidencia") val paisResidencia: String,
    @SerializedName("estadoResidencia") val estadoResidencia: String,
    @SerializedName("ciudadResidencia") val ciudadResidencia: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefono") val telefono: String?
)