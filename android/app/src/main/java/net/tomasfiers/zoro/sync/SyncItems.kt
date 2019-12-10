package net.tomasfiers.zoro.sync

import net.tomasfiers.zoro.data.DataRepo
import net.tomasfiers.zoro.data.Key
import net.tomasfiers.zoro.data.getValue
import net.tomasfiers.zoro.zotero_api.MAX_ITEMS_PER_RESPONSE
import net.tomasfiers.zoro.zotero_api.remoteLibraryVersion

suspend fun DataRepo.syncItems(remoteLibVersionAtStartSync: Int?) {
    val itemVersions = zoteroAPIClient.getItemVersions(
        sinceLibraryVersion = getValue(Key.LOCAL_LIBRARY_VERSION)
    )
    val itemIds = itemVersions.keys
    itemIds
        .chunked(MAX_ITEMS_PER_RESPONSE)
        .forEach { someItemIds ->
            val response = zoteroAPIClient.getItems(someItemIds.joinToString(","))
            if (response.remoteLibraryVersion != remoteLibVersionAtStartSync) {
                throw RemoteLibraryUpdatedSignal()
            }
            response.body()?.forEach { itemJson ->
                database.item.insert(itemJson.asDomainModel())
            }
        }
}