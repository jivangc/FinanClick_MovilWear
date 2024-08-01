package com.example.financlick_movilwear.services

import com.example.financlick_movilwear.models.DocDetailModel
import com.example.financlick_movilwear.models.DocRequestModel
import com.example.financlick_movilwear.models.DocsClienteModel
import com.example.financlick_movilwear.models.LoginModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {

    // ---- LOGIN ----
    // Endpoint para iniciar sesion
    @POST("UsuarioCliente/login")
    fun postLogin(@Body loginModel: LoginModel): Call<ResponseBody>

    // Endpoint para obtener los detalles del usuario
    @GET("UsuarioCliente/detail")
    fun getDetail(): Call<ResponseBody>

    // ---- DOCUMENTOS ----
    // Endpoint para obtener los documentos del cliente
    @GET("DocumentoCliente/cliente/{id}")
    fun getDocs(@Path("id") id: Int): Call<List<DocsClienteModel>>

    // Endpoint para subir los documentos
    @POST("DocumentoCliente/subir")
    fun postDocs(@Body document: DocRequestModel): Call<ResponseBody>

    // Endpoint para obtener los detalles de los documentos
    @GET("Documento/{id}")
    fun getDocDetail(@Path("id") id: Int): Call<DocDetailModel>

}