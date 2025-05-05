package com.carlyu.pmxv.models

import com.google.gson.annotations.SerializedName

data class ExampleDataModel(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)
