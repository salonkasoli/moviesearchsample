package com.github.salonkasoli.moviesearchsample.configuration

import com.google.gson.annotations.SerializedName

data class Config(
    @SerializedName("images")
    val images: Images,
    @SerializedName("change_keys")
    val changeKeys: List<String>
)

data class Images(
    @SerializedName("base_url")
    val baseUrl: String,
    @SerializedName("secure_base_url")
    val secureBaseUrl: String,
    @SerializedName("backdrop_sizes")
    val backdropSizes: List<String>,
    @SerializedName("poster_sizes")
    val posterSizes: List<String>,
    @SerializedName("profile_sizes")
    val profileSizes: List<String>,
    @SerializedName("still_sizes")
    val stillSizes: List<String>
)