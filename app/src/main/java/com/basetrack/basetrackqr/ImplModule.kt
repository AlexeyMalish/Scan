package com.basetrack.basetrackqr

import android.content.Context
import android.content.SharedPreferences
import com.basetrack.basetrackqr.login.network.api.ApiHelper
import com.basetrack.basetrackqr.login.network.api.ApiHelperImpl
import com.basetrack.basetrackqr.login.network.api.InterfaceApi
import com.basetrack.basetrackqr.login.utils.Constants.Companion.BASE_URL
import com.basetrack.basetrackqr.login.utils.Constants.Companion.SAVE_TAG
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ImplModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }


    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(okHttpClient)
            .build()


    @Provides
    @Singleton
    fun providesUserAPI(retrofitBuilder: Retrofit): InterfaceApi{
        return retrofitBuilder.create(InterfaceApi::class.java)
    }


    @Provides
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper


    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SAVE_TAG, Context.MODE_PRIVATE)
    }
}
