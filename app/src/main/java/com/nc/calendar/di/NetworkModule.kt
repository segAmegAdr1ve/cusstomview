package com.nc.calendar.di

import android.content.Context
import android.net.ConnectivityManager
import com.nc.calendar.BuildConfig
import com.nc.calendar.Constants.API_KEY_PARAM
import com.nc.calendar.Constants.CITY_PARAM
import com.nc.calendar.data.network.NoInternetException
import com.nc.calendar.data.network.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideClient(
        @LoggingInterceptor loggingInterceptor: Interceptor,
        @NetworkStateInterceptor networkStateInterceptor: Interceptor,
        @QueryInterceptor queryInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(networkStateInterceptor)
        .addInterceptor(queryInterceptor)
        .build()

    @Provides
    @QueryInterceptor
    @Singleton
    fun provideQueryInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val newUrl = request.url.newBuilder()
                .addQueryParameter(CITY_PARAM, CITY)
                .addQueryParameter(API_KEY_PARAM, BuildConfig.API_KEY)
                .build()
            val newRequest = request.newBuilder()
                .url(newUrl)
                .build()
            chain.proceed(newRequest)
        }
    }

    @LoggingInterceptor
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @NetworkStateInterceptor
    @Provides
    @Singleton
    fun provideNetworkStateInterceptor(connectivityManager: ConnectivityManager): Interceptor {
        return Interceptor { chain ->
            if (connectivityManager.activeNetwork == null) {
                throw NoInternetException()
            }
            chain.proceed(chain.request())
        }
    }

    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val CITY = "Ulyanovsk"
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class QueryInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoggingInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkStateInterceptor