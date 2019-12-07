package net.tomasfiers.zoro.zotero_api

import net.tomasfiers.zoro.util.createJSONAPIClient
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// "lazy" computes value only on first access (client creation is expensive).
val zoteroAPIClient by lazy {
    createJSONAPIClient(
        baseUrl = "https://api.zotero.org/users/4670453/",
        requestHeaders = mapOf(
            "Zotero-API-Version" to "3",
            "Zotero-API-Key" to ZOTERO_API_KEY
        ),
        APIInterface = ZoteroAPIClient::class.java
    )
}

interface ZoteroAPIClient {

    @GET("collections?format=versions")
    suspend fun getCollectionVersions(
        @Query("since") sinceLibraryVersion: Int = 0
    ): Response<Map<String, Int>>

    // `collectionIds` should be a comma separated list.
    @GET("collections")
    suspend fun getCollections(
        @Query("collectionKey") collectionIds: String,
        @Query("limit") amount: Int = MAX_ITEMS_PER_RESPONSE
    ): Response<List<CollectionJSON>>
}

val <T> Response<T>.remoteLibraryVersion: Int?
    get() = this.headers()["Last-Modified-Version"]?.toInt()

// This limit does not apply when "?format=versions".
const val MAX_ITEMS_PER_RESPONSE = 100
