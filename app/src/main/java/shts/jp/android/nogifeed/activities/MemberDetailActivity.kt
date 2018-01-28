package shts.jp.android.nogifeed.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import kotlinx.android.synthetic.main.fragment_member_detail.*
import kotlinx.android.synthetic.main.view_member_detail_header.view.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.adapters.MemberFeedAdapter
import shts.jp.android.nogifeed.adapters.OnMemberEntryClickListener
import shts.jp.android.nogifeed.adapters.OnPageMaxScrolledListener
import shts.jp.android.nogifeed.api.NogiFeedApiClient
import shts.jp.android.nogifeed.models.Entry
import shts.jp.android.nogifeed.models.Member
import shts.jp.android.nogifeed.providers.FavoriteContentObserver
import shts.jp.android.nogifeed.providers.UnreadArticlesContentObserver
import shts.jp.android.nogifeed.providers.dao.Favorites

class MemberDetailActivity : AppCompatActivity() {

    companion object {
        private val EXTRA_MEMBER_ID = "memberId"
        private val LIMIT = 30

        fun getStartIntent(context: Context, member: Member): Intent {
            return getStartIntent(context, member.id!!)
        }

        fun getStartIntent(context: Context, memberId: Int): Intent {
            return Intent(context, MemberDetailActivity::class.java)
                    .putExtra(EXTRA_MEMBER_ID, memberId)
        }
    }

    private val subscriptions: CompositeSubscription = CompositeSubscription()

    private val adapter: MemberFeedAdapter = MemberFeedAdapter().apply {
        scrollListener = object : OnPageMaxScrolledListener {
            override fun onScrolledMaxPage() {
                getNextFeed()
            }
        }
        clickListener = object : OnMemberEntryClickListener {
            override fun onClickMemberEntry(entry: Entry) {
                startActivity(BlogActivity.getStartIntent(this@MemberDetailActivity, entry))
            }
        }
    }

    private val observer: UnreadArticlesContentObserver = object : UnreadArticlesContentObserver() {
        override fun onChangeState(state: Int) {
            if (state == FavoriteContentObserver.State.ADD) {
                Snackbar.make(coordinatorLayout, R.string.registered_favorite_member, Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(coordinatorLayout, R.string.unregistered_favorite_member, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private var memberId: Int = -1
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_member_detail)
        observer.register(this)

        memberId = intent?.getIntExtra(EXTRA_MEMBER_ID, -1) ?: return
        memberDetailView.setup(memberId)

        fab.setOnClickListener { Favorites.toggle(this, memberId) }

        memberEntryList.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        memberEntryList.adapter = adapter

        collapsingToolbar.setCollapsedTitleTextColor(
                ContextCompat.getColor(this, android.R.color.white))
        collapsingToolbar.setExpandedTitleColor(
                ContextCompat.getColor(this, android.R.color.transparent))

        val sharedElementName = "share"
        memberDetailView.profile_image.transitionName = sharedElementName

        getEntries()
    }

    private fun getEntries() {
        counter = 0
        subscriptions.add(NogiFeedApiClient
                .getMemberEntries(memberId, (counter * LIMIT), LIMIT)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ entries ->
                    if (entries == null) {
                        Snackbar.make(coordinatorLayout, R.string.feed_failure, Snackbar.LENGTH_SHORT).show()
                        return@subscribe
                    }
                    adapter.add(entries)
                    collapsingToolbar.title = entries[0].memberName
                }, {
                    Snackbar.make(coordinatorLayout, R.string.feed_failure, Snackbar.LENGTH_SHORT).show()
                }))
    }

    fun getNextFeed() {
        counter++

        subscriptions.add(NogiFeedApiClient
                .getMemberEntries(memberId, (counter * LIMIT), LIMIT)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ entries ->
                    if (entries == null) {
                        Snackbar.make(coordinatorLayout, R.string.feed_failure, Snackbar.LENGTH_SHORT).show()
                        return@subscribe
                    }
                    if (entries.isEmpty()) {
                        // 空の場合はもう Footer を表示しない
                        adapter.showFooter = false
                        adapter.scrollListener = null
                    } else {
                        adapter.add(entries)
                    }
                }, {
                    Snackbar.make(coordinatorLayout, R.string.feed_failure, Snackbar.LENGTH_SHORT).show()
                }))
    }

    override fun onDestroy() {
        subscriptions.unsubscribe()
        observer.unregister(this)
        super.onDestroy()
    }
}
