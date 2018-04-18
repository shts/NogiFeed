package shts.jp.android.nogifeed.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.async
import shts.jp.android.nogifeed.api.NogiFeedApiClient
import shts.jp.android.nogifeed.db.Favorite2
import shts.jp.android.nogifeed.db.NogiFeedDatabase
import shts.jp.android.nogifeed.models.Member
import shts.jp.android.nogifeed.models.Members

class AllMemberViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteDao = NogiFeedDatabase.getInstance(application).favoriteDao()

    val favorites = favoriteDao.favorites()

    var membersData = MutableLiveData<Members>()
    var processing = MutableLiveData<Boolean>()
    var result = MutableLiveData<Boolean>()

    fun getAllMembers() {
        async {
            processing.postValue(true)
            val members = NogiFeedApiClient.getAllMembers().toBlocking().single()
            if (members != null && !members.isEmpty()) {
                result.postValue(true)
                membersData.postValue(members)
            } else {
                result.postValue(false)
            }
            processing.postValue(false)
        }
    }

    fun insert(member: Member) {
        async {
            favoriteDao.insert(Favorite2(0, member.id))
        }
    }

    fun delete(member: Member) {
        async {
            favoriteDao.delete(member.id)
        }
    }

}
