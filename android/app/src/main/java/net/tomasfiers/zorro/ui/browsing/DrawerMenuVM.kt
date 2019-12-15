package net.tomasfiers.zorro.ui.browsing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.tomasfiers.zorro.data.DataRepo
import net.tomasfiers.zorro.data.clearLocalData

class DrawerMenuViewModel(private val dataRepo: DataRepo) : ViewModel() {

    val lastSyncText = dataRepo.lastSyncText

    fun clearLocalData() = viewModelScope.launch {
        dataRepo.clearLocalData()
    }
}