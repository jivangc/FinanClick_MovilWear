package com.example.financlick_movilwear.presentation.models

interface DocDetailCallback {
    fun onSuccess(docDetail: DocDetailModel)
    fun onError(errorMessage: String)
}