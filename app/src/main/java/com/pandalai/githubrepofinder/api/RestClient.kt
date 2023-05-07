package com.pandalai.githubrepofinder.api


import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


object RestClient {

    @JvmStatic lateinit var apiStreamService: ApiService
    @JvmStatic lateinit var apiService: ApiService
    @JvmStatic lateinit var apiServiceDownload: ApiService


    private fun getOkHttpClient(retryOnConnectionFailure: Boolean, timeoutSeconds: Long,
                                loggingLevel: HttpLoggingInterceptor.Level): OkHttpClient { //loggingLevel BASIC is needed for streaming api to behave like chunk data events

        val protocolList = ArrayList<Protocol>()
        protocolList.add(Protocol.HTTP_2)
        protocolList.add(Protocol.SPDY_3)
        protocolList.add(Protocol.HTTP_1_1)

        val connectionPool = ConnectionPool(3, (5 * 60 * 1000).toLong(), TimeUnit.MILLISECONDS)

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = loggingLevel


        val builder = OkHttpClient.Builder()
        builder.connectionPool(connectionPool)
        builder.readTimeout(timeoutSeconds, TimeUnit.SECONDS)
        builder.connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
        builder.writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
        builder.retryOnConnectionFailure(retryOnConnectionFailure)
        builder.protocols(protocolList)
        builder.addInterceptor(httpLoggingInterceptor)

        return builder.build()
    }

    @JvmStatic
    fun setupAllClients(){
        initStreamApiService()
        initApiService()
        initApiServiceDownload()
    }


    @JvmStatic
    fun initStreamApiService(){
        val restAdapter = Retrofit.Builder()
            .baseUrl(DataConstants.HOST_URL)
            .client(getOkHttpClient(true, 5*60, HttpLoggingInterceptor.Level.BASIC))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiStreamService = restAdapter.create(ApiService::class.java)
    }

    @JvmStatic
    fun initApiService(){
        val restAdapter = Retrofit.Builder()
            .baseUrl(DataConstants.HOST_URL)
            .client(getOkHttpClient(true, 30, HttpLoggingInterceptor.Level.BODY))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = restAdapter.create(ApiService::class.java)
    }

    @JvmStatic
    fun initApiServiceDownload(){
        val restAdapter = Retrofit.Builder()
            .baseUrl(DataConstants.HOST_URL)
            .client(getOkHttpClient(true, 10*60, HttpLoggingInterceptor.Level.BASIC))
            .build()

        apiServiceDownload = restAdapter.create(ApiService::class.java)
    }




}
