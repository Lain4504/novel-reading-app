package com.miraimagiclab.novelreadingapp.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.miraimagiclab.novelreadingapp.data.remote.api.NovelApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.AuthApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.ChapterApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.CommentApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.ReviewApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.UserNovelInteractionApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.UserApiService
import com.miraimagiclab.novelreadingapp.data.remote.api.ImageApiService
import com.miraimagiclab.novelreadingapp.data.auth.SessionManager
import com.miraimagiclab.novelreadingapp.data.local.prefs.AuthDataStore
import com.miraimagiclab.novelreadingapp.data.remote.interceptor.AuthInterceptor
import com.miraimagiclab.novelreadingapp.data.remote.interceptor.TokenAuthenticator
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    @Named("baseOkHttp")
    fun provideBaseOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("authOkHttp")
    fun provideAuthOkHttpClient(
        @Named("baseOkHttp") baseOkHttp: OkHttpClient,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return baseOkHttp.newBuilder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideSessionManager(authDataStore: AuthDataStore): SessionManager = SessionManager(authDataStore)

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionManager: SessionManager): AuthInterceptor = AuthInterceptor(sessionManager)

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        @Named("refreshAuthApi") authApiService: AuthApiService,
        sessionManager: SessionManager
    ): TokenAuthenticator = TokenAuthenticator(authApiService, sessionManager)

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    @Named("authlessRetrofit")
    fun provideAuthlessRetrofit(
        @Named("baseOkHttp") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("authedRetrofit")
    fun provideAuthedRetrofit(
        @Named("authOkHttp") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideNovelApiService(@Named("authedRetrofit") retrofit: Retrofit): NovelApiService {
        return retrofit.create(NovelApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@Named("authedRetrofit") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("refreshAuthApi")
    fun provideRefreshAuthApi(@Named("authlessRetrofit") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChapterApiService(@Named("authedRetrofit") retrofit: Retrofit): ChapterApiService {
        return retrofit.create(ChapterApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCommentApiService(@Named("authedRetrofit") retrofit: Retrofit): CommentApiService {
        return retrofit.create(CommentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewApiService(@Named("authedRetrofit") retrofit: Retrofit): ReviewApiService {
        return retrofit.create(ReviewApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserNovelInteractionApiService(@Named("authedRetrofit") retrofit: Retrofit): UserNovelInteractionApiService {
        return retrofit.create(UserNovelInteractionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(@Named("authedRetrofit") retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideImageApiService(@Named("authedRetrofit") retrofit: Retrofit): ImageApiService {
        return retrofit.create(ImageApiService::class.java)
    }
}
