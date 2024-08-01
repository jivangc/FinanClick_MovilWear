package com.example.financlick_movilwear.models

interface DocDetailCallback {
    fun onSuccess(docDetail: DocDetailModel)
    fun onError(errorMessage: String)
}