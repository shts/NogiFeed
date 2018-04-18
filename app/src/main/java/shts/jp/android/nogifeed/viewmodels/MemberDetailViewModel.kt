package shts.jp.android.nogifeed.viewmodels

import android.app.Application
import android.arch.core.util.Function
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import kotlinx.coroutines.experimental.async
import shts.jp.android.nogifeed.api.NogiFeedApiClient
import shts.jp.android.nogifeed.db.Favorite2
import shts.jp.android.nogifeed.db.NogiFeedDatabase
import shts.jp.android.nogifeed.models.Entries
import shts.jp.android.nogifeed.models.Member

class MemberDetailViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private val LIMIT = 30
    }

    private val favoriteDao = NogiFeedDatabase
            .getInstance(application)
            .favoriteDao()

    enum class Result {
        InProgress, Success, Failure
    }

    var entriesLiveData = MutableLiveData<Entries>()
    var memberLiveData = MutableLiveData<Member>()
    var resultLiveData = MutableLiveData<Result>()
    val favorites = favoriteDao.favorites()
    var favoritesQueryResult = MutableLiveData<List<Favorite2>>()
//            favoriteDao.getFavorites()

//    fun getLivaData(): LiveData<Int> {
//        Transformations.map(favorites) {  }
//    }

    fun getFavorites() {
        async {
            favoritesQueryResult.postValue(
                    favoriteDao.getFavorites()
            )
        }
    }

    private var counter = 0

    private fun skip(): Int = counter * LIMIT

    fun getMemberFeed(memberId: Int) {
        counter = 0
        getEntries(memberId)
    }

    fun getNextMemberFeed(memberId: Int) {
        counter++
        getEntries(memberId)
    }

    private fun getEntries(memberId: Int) {
        async {
            resultLiveData.postValue(Result.InProgress)
            try {
                val entries = NogiFeedApiClient
                        .getMemberEntries(memberId, skip(), LIMIT)
                        .toBlocking()
                        .single()

                entriesLiveData.postValue(entries)
                resultLiveData.postValue(Result.Success)
            } catch (e: Throwable) {
                resultLiveData.postValue(Result.Failure)
            }
        }
    }

    fun getMember(memberId: Int) {
        async {
            val member = NogiFeedApiClient
                    .getMember(memberId)
                    .toBlocking()
                    .single()
            memberLiveData.postValue(member)
        }
    }

    fun insert(memberId: Int) {
        async {
            favoriteDao.insert(Favorite2(0, memberId))
        }
    }

    fun delete(memberId: Int) {
        async {
            favoriteDao.delete(memberId)
        }
    }
}
