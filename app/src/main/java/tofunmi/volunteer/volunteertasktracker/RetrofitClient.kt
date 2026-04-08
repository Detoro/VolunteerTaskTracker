package tofunmi.volunteer.volunteertasktracker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//const val api: String = "http://192.168.1.141:8000/"
const val api = "http://localhost:8000/"

object RetrofitClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(api)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    val apiInterface: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}