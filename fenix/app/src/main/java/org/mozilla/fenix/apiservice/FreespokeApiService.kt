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
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(DateJsonAdapter())
    .addLast(KotlinJsonAdapterFactory())
    .build()

val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(
    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

val retrofit = Retrofit.Builder()
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
    @Headers("x-client-secret: cce90e80e99383e2fe5c39b42f73b5c3")
    @FormUrlEncoded
    suspend fun signUpUser(@Field("firstName") firstName: String,
                           @Field("lastName") lastName: String,
                           @Field("email") email: String,
                           @Field("password") password: String): Response<UserData>

    @GET("/accounts/profile")
    suspend fun getProfile(@Header("Authorization") bearerToken: String): Response<UserProfileData>
}

object FreespokeApi {
    val service : FreespokeApiService by lazy {
        retrofit.create(FreespokeApiService::class.java)
    }

    public suspend fun getUserProfileData(): UserProfileData {
        return UserProfileData(
            attributes = ProfileAttributes(
                registrationPlatform = "android",
                subscription = Subscription(
                    subscriptionName = "free trial",
                    subscriptionPaymentSource = "android",
                    subscriptionExpiry = 1711866551L
                )
            ),
            firstName = "Nic",
            lastName = "Test",
            manageSubscriptionLink = "https://freespoke.recurly.com/account/65TZ59ZEFRacyVASnFoKSSYfoWTiL338"
        )
    }
}

data class UserProfileData(
    val attributes: ProfileAttributes,
    val firstName: String,
    val lastName: String,
    val manageSubscriptionLink: String
)

data class ProfileAttributes(
    val registrationPlatform: String,
    val subscription: Subscription?
)

data class Subscription(
    val subscriptionName: String,
    val subscriptionPaymentSource: String,
    val subscriptionExpiry: Long
)
