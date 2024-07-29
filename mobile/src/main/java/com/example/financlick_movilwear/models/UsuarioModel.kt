package com.example.financlick_movilwear.models
import com.google.gson.annotations.SerializedName

open class UsuarioModel(
    @SerializedName("idUsuarioCliente") val idUsuarioCliente: Int,
    @SerializedName("idCliente") val idCliente: Int?,
    @SerializedName("usuario") val usuario: String?,
    @SerializedName("contrasenia") val contrasenia: String?,
    @SerializedName("estatus") val estatus: Int?
){}