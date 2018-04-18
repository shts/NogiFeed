package shts.jp.android.nogifeed.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.async
import shts.jp.android.nogifeed.api.NogiFeedApiClient
import shts.jp.android.nogifeed.db.NogiFeedDatabase
import shts.jp.android.nogifeed.models.Entries

class FavoriteMemberFeedViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val LIMIT = 30
    }

    private val favoriteDao = NogiFeedDatabase.getInstance(application).favoriteDao()

    var favoriteData = favoriteDao.favorites()
    var entriesData = MutableLiveData<Entries>()
    var processing = MutableLiveData<Boolean>()
    var result =  MutableLiveData<Boolean>()

    private var counter: Int = 0
    private fun skip() = counter * LIMIT

    fun getEntries(ids: List<Int>) {
        counter = 0

        async {
            processing.postValue(true)

            val entries = NogiFeedApiClient
                    .getMemberEntries(ids, skip(), LIMIT)
                    .toBlocking()
                    .single()

            if (entries != null && !entries.isEmpty()) {
                entriesData.postValue(entries)
                result.postValue(true)
            } else {
                result.postValue(false)
            }
            processing.postValue(false)
        }
    }

    fun getNextEntries(ids: List<Int>) {
        counter++

        async {
            processing.postValue(true)

            val entries = NogiFeedApiClient
                    .getMemberEntries(ids, skip(), LIMIT)
                    .toBlocking()
                    .single()

            if (entries != null && !entries.isEmpty()) {
                entriesData.postValue(entries)
                result.postValue(true)
            } else {
                result.postValue(false)
            }
            processing.postValue(false)
        }
    }
}
