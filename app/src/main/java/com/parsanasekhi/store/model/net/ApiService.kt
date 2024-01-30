package com.parsanasekhi.store.model.net

import com.google.gson.JsonObject
import com.parsanasekhi.store.model.data.AdsResponse
import com.parsanasekhi.store.model.data.CartResponse
import com.parsanasekhi.store.model.data.CommentResponse
import com.parsanasekhi.store.model.data.NewCommentResponse
import com.parsanasekhi.store.model.data.ProductResponse
import com.parsanasekhi.store.model.data.SignInResponse
import com.parsanasekhi.store.model.data.UserCartInfo
import com.parsanasekhi.store.model.repository.TokenInMemory
import com.parsanasekhi.store.util.BASE_URL
import ir.dunijet.dunibazaar.model.data.CheckOut
import ir.dunijet.dunibazaar.model.data.SubmitOrder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("signUp")
    suspend fun signUp(@Body jsonObject: JsonObject): SignInResponse

    @POST("signIn")
    suspend fun signIn(@Body jsonObject: JsonObject): SignInResponse

    @GET("refreshToken")
    fun refreshToken(): Call<SignInResponse>

    @GET("getProducts")
    suspend fun getAllProducts(): ProductResponse

    @GET("getSliderPics")
    suspend fun getAllAds(): AdsResponse

    @POST("getComments")
    suspend fun getAllComments(@Body jsonObject: JsonObject): CommentResponse

    @POST("addNewComment")
    suspend fun addNewComment(@Body jsonObject: JsonObject): NewCommentResponse

    @POST("addToCart")
    suspend fun addToCart(@Body jsonObject: JsonObject): CartResponse

    @GET("getUserCart")
    suspend fun getUserCart(): UserCartInfo

    @POST("removeFromCart")
    suspend fun removeFromCart(@Body jsonObject: JsonObject): CartResponse

    @POST("submitOrder")
    suspend fun submitOrder(@Body jsonObject: JsonObject): SubmitOrder

    @POST("checkout")
    suspend fun checkOut(@Body jsonObject: JsonObject): CheckOut

}

fun createApiService(): ApiService {

    val client = OkHttpClient.Builder()
        .addInterceptor {
            val oldRequest = it.request()
            val newRequest = oldRequest.newBuilder()
            if (TokenInMemory.token != null)
                newRequest.addHeader("authorization", TokenInMemory.token!!)
            newRequest.addHeader("accept", "application/json")
            it.proceed(newRequest.build())
        }

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client.build())
        .build()

    return retrofit.create(ApiService::class.java)
}