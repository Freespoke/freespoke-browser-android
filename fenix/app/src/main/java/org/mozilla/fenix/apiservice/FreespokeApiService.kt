package org.mozilla.fenix.apiservice

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.mozilla.fenix.BuildConfig
import org.mozilla.fenix.apiservice.model.DateJsonAdapter
import org.mozilla.fenix.apiservice.model.QuickLinkObject
import org.mozilla.fenix.apiservice.model.ShopCollection
import org.mozilla.fenix.apiservice.model.TrendingNews
import org.mozilla.fenix.apiservice.model.UserData
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(DateJsonAdapter())
    .addLast(KotlinJsonAdapterFactory())
    .build()

val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(
    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(client)
    .baseUrl(BuildConfig.BASE_URL)
    .build()
interface FreespokeApiService {

    @GET("v2/stories/top/overview")
    suspend fun getTrendingNews(@Query("page") pageNum: Int, @Query("per_page") size: Int): List<TrendingNews>

    @GET("v2/shop/collections")
    suspend fun getShops(@Query("page") pageNum: Int, @Query("per_page") size: Int): ShopCollection

    @GET("app/freespoke/widgets/quick-links")
    suspend fun getFreespokeQuickLinks(@Query("limit") limit: Int, @Query("client") client: String): QuickLinkObject

    @POST("/accounts/register/android")
    suspend fun signUpUser(@Field("firstName") firstName: String,
                           @Field("lastName") lastName: String,
                           @Field("email") email: String,
                           @Field("password") password: String): UserData
}

object FreespokeApi {
    val service : FreespokeApiService by lazy {
        retrofit.create(FreespokeApiService::class.java)
    }
}
