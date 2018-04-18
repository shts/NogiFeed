package shts.jp.android.nogifeed.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.async
import shts.jp.android.nogifeed.api.NogiFeedApiClient
import shts.jp.android.nogifeed.models.Entries
import shts.jp.android.nogifeed.providers.FavoriteContentObserver

class AllFeedListViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val LIMIT = 30
    }

    // get all feed
    var allEntriesLiveData = MutableLiveData<Entries>()
    var allResult = MutableLiveData<Boolean>()
    var allProcessing = MutableLiveData<Boolean>()

    // favorite observer
    var stateData = MutableLiveData<Int>()

    private var counter: Int = 0
    private fun skip() = counter * LIMIT

    private val favoriteContentObserver = object : FavoriteContentObserver() {
        override fun onChangeState(@State state: Int) {
            stateData.value = state
        }
    }.also {
        it.register(application)
    }

    override fun onCleared() {
        favoriteContentObserver.unregister(getApplication())
        super.onCleared()
    }

    fun getAllEntries() {
        counter = 0

        async {
            allProcessing.postValue(true)
            val entries = getEntries()
            if (entries == null || entries.isEmpty()) {
                allResult.postValue(false)
            } else {
                allEntriesLiveData.postValue(entries)
                allResult.postValue(true)
            }
            allProcessing.postValue(false)
        }
    }

    fun getNextEntries() {
        counter++
        allProcessing.value = false

        async {
            val entries = getEntries()
            if (entries != null && !entries.isEmpty()) {
                allEntriesLiveData.postValue(entries)
            }
        }
    }

    private fun getEntries() = NogiFeedApiClient
            .getAllEntries(skip(), LIMIT)
            .toBlocking()
            .single()
}
