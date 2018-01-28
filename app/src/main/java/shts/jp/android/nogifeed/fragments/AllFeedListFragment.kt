package shts.jp.android.nogifeed.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_all_feed_list.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.activities.BlogActivity
import shts.jp.android.nogifeed.activities.MemberDetailActivity
import shts.jp.android.nogifeed.adapters.AllFeedAdapter
import shts.jp.android.nogifeed.adapters.OnEntryClickListener
import shts.jp.android.nogifeed.adapters.OnPageMaxScrolledListener
import shts.jp.android.nogifeed.api.NogiFeedApiClient
import shts.jp.android.nogifeed.models.Entry
import shts.jp.android.nogifeed.providers.FavoriteContentObserver

class AllFeedListFragment : Fragment() {

    companion object {
        private val PAGE_LIMIT = 30

        val newInstance: AllFeedListFragment = AllFeedListFragment()
    }

    private var counter = 0

    private val adapter: AllFeedAdapter = AllFeedAdapter().apply {
        scrollListener = object : OnPageMaxScrolledListener {
            override fun onScrolledMaxPage() {
                getNextFeed()
            }
        }
        clickListener = object : OnEntryClickListener {
            override fun onClickEntry(entry: Entry) {
                activity.startActivity(BlogActivity.getStartIntent(activity, entry))
            }

            override fun onClickProfileImage(entry: Entry) {
                activity.startActivity(MemberDetailActivity.getStartIntent(activity, entry.memberId))
            }
        }
    }

    private val subscriptions: CompositeSubscription = CompositeSubscription()

    private val favoriteContentObserver = object : FavoriteContentObserver() {
        override fun onChangeState(@State state: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    // 推しメン登録は個人ページより行われるので resume <-> pause だと拾えない
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoriteContentObserver.register(context)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_feed_list, null, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allFeedList.adapter = adapter
        allFeedList.addItemDecoration(DividerItemDecoration(context, VERTICAL))

        refreshLayout.setOnRefreshListener { getAllFeeds() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getAllFeeds()
    }

    private fun getAllFeeds() {
        counter = 0

        subscriptions.add(NogiFeedApiClient.getAllEntries(counter * PAGE_LIMIT, PAGE_LIMIT)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { refreshLayout.isRefreshing = true }
                .subscribe({ entries ->
                    refreshLayout.isRefreshing = false
                    adapter.add(entries)
                }, {
                    refreshLayout.isRefreshing = false
                    Toast.makeText(activity, R.string.feed_failure, Toast.LENGTH_SHORT).show()
                }))
    }

    private fun getNextFeed() {
        refreshLayout.isRefreshing = false

        counter++
        subscriptions.add(NogiFeedApiClient.getAllEntries(counter * PAGE_LIMIT, PAGE_LIMIT)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ entries ->
                    if (entries != null) {
                        adapter.add(entries)
                    } else {
                        Toast.makeText(activity, R.string.feed_failure, Toast.LENGTH_SHORT).show()
                    }
                }, { }))
    }

    override fun onDestroyView() {
        favoriteContentObserver.unregister(context)
        subscriptions.unsubscribe()
        super.onDestroyView()
    }
}
