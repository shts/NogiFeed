package shts.jp.android.nogifeed.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_all_member.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.adapters.MemberGridAdapter
import shts.jp.android.nogifeed.adapters.OnMemberClickListener
import shts.jp.android.nogifeed.api.NogiFeedApiClient
import shts.jp.android.nogifeed.models.Member
import shts.jp.android.nogifeed.providers.FavoriteContentObserver
import shts.jp.android.nogifeed.providers.dao.Favorites

class AllMemberActivity : AppCompatActivity() {

    companion object {
        fun getStartIntent(context: Context): Intent =
                Intent(context, AllMemberActivity::class.java)
    }

    private val subscriptions = CompositeSubscription()
    private var adapter: MemberGridAdapter = MemberGridAdapter().apply {
        clickListener = object : OnMemberClickListener {
            override fun onClickMember(member: Member) {
                this@AllMemberActivity.setResult(Activity.RESULT_OK)
                Favorites.toggle(this@AllMemberActivity, member)
            }
        }
    }

    private val favoriteContentObserver = object : FavoriteContentObserver() {
        override fun onChangeState(@State state: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_member)

        toolbar.visibility = View.VISIBLE
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.primary))
        toolbar.setTitle(R.string.choose_member)
        toolbar.setNavigationIcon(R.drawable.ic_clear_purple_700_18dp)
        toolbar.setNavigationOnClickListener { finish() }

        memberGridView.adapter = adapter
        memberGridView.layoutManager = GridLayoutManager(this, 3)

        refreshLayout.setOnRefreshListener { getAllMembers() }

        getAllMembers()
    }

    override fun onResume() {
        favoriteContentObserver.register(this)
        super.onResume()
    }

    override fun onPause() {
        favoriteContentObserver.unregister(this)
        super.onPause()
    }

    override fun onDestroy() {
        subscriptions.unsubscribe()
        super.onDestroy()
    }

    private fun getAllMembers() {
        subscriptions.add(NogiFeedApiClient.getAllMembers()
                .doOnSubscribe { refreshLayout.isRefreshing = true }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ members ->
                    refreshLayout.isRefreshing = false
                    members?.let {
                        adapter.add(it)
                    }
                }, { refreshLayout.isRefreshing = false }))
    }
}
