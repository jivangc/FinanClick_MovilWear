package com.example.financlick_movilwear.services

import com.example.financlick_movilwear.models.LoginModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
interface AuthApiService {
    // Endpoint para iniciar sesion
    @POST("UsuarioCliente/login")
    fun postLogin(@Body loginModel: LoginModel): Call<ResponseBody>

    @GET("UsuarioCliente/detail")
    fun getDetail(): Call<ResponseBody>

}