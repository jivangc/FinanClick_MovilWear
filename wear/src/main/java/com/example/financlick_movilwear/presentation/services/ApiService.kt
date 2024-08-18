package com.example.financlick_movilwear.presentation.services

import com.example.financlick_movilwear.presentation.models.CreditoModel
import com.example.financlick_movilwear.presentation.models.DocDetailModel
import com.example.financlick_movilwear.presentation.models.DocsClienteModel
import com.example.financlick_movilwear.presentation.models.ProductoModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("Credito")
    fun getCreditos(): Call<List<CreditoModel>>

    @GET("Producto")
    fun getProducts(): Call<List<ProductoModel>>

    @GET("Documento/{id}")
    fun getDocDetail(@Path("id") id: Int): Call<DocDetailModel>

    @GET("DocumentoCliente/cliente/{id}")
    fun getDocs(@Path("id") id: Int): Call<List<DocsClienteModel>>
}