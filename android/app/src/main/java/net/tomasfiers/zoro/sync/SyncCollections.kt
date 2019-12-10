package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.Key
import net.tomasfiers.zoro.data.getValue
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.remoteLibraryVersion

suspend fun DataRepo.syncCollections(): Int? {
    syncStatus.value = "Updating collections…"
    val collectionVersionsResponse = zoteroAPIClient.getCollectionVersions(
        sinceLibraryVersion = getValue(Key.LOCAL_LIBRARY_VERSION)
    )
    val remoteLibVersionAtStartSync = collectionVersionsResponse.remoteLibraryVersion
    val collectionIds = collectionVersionsResponse.body()?.keys ?: emptyList<String>()
    syncStatus.value = "Downloading ${collectionIds.size} collections…"
    downloadProgress.value = 0f
    var currentCollectionNr = 1
    collectionIds
        .chunked(MAX_ITEMS_PER_RESPONSE)
        .forEach { someCollectionIds ->
            val jsonCollectionsResponse =
                zoteroAPIClient.getCollections(someCollectionIds.joinToString(","))
            if (jsonCollectionsResponse.remoteLibraryVersion != remoteLibVersionAtStartSync) {
                throw RemoteLibraryUpdatedSignal()
            }
            jsonCollectionsResponse.body()?.forEach {
                database.collection.insert(it.asDomainModel())
                downloadProgress.value = (currentCollectionNr++).toFloat() / collectionIds.size
            }
        }
    return remoteLibVersionAtStartSync
}
