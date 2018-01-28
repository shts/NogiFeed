package shts.jp.android.nogifeed.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_favorite_feed_list.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.activities.AllMemberActivity
import shts.jp.android.nogifeed.activities.BlogActivity
import shts.jp.android.nogifeed.adapters.FavoriteAdapter
import shts.jp.android.nogifeed.adapters.OnEntryClickListener
import shts.jp.android.nogifeed.adapters.OnPageMaxScrolledListener
import shts.jp.android.nogifeed.api.NogiFeedApiClient
import shts.jp.android.nogifeed.extensions.gone
import shts.jp.android.nogifeed.extensions.visible
import shts.jp.android.nogifeed.models.Entries
import shts.jp.android.nogifeed.models.Entry
import shts.jp.android.nogifeed.providers.dao.Favorites

class FavoriteMemberFeedListFragment : Fragment() {

    companion object {
        private val REQUEST_CODE = 0
        private val LIMIT = 30

        val newInstance: FavoriteMemberFeedListFragment =
                FavoriteMemberFeedListFragment()
    }

    private var counter = 0
    private val subscriptions: CompositeSubscription = CompositeSubscription()
    private val adapter: FavoriteAdapter = FavoriteAdapter().apply {
        scrollListener = object : OnPageMaxScrolledListener {
            override fun onScrolledMaxPage() {
                getNextFeed()
            }
        }
        clickListener = object : OnEntryClickListener {
            override fun onClickEntry(entry: Entry) {
                startActivity(BlogActivity.getStartIntent(context, entry))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite_feed_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener {
            startActivityForResult(AllMemberActivity.getStartIntent(activity), REQUEST_CODE)
        }

        favoriteList.adapter = adapter
        favoriteList.setHasFixedSize(true)

        refreshLayout.setOnRefreshListener { getEntries() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getEntries()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            getEntries()
        }
    }

    override fun onDestroyView() {
        subscriptions.unsubscribe()
        super.onDestroyView()
    }

    @SuppressLint("CheckResult")
    private fun getEntries() {
        setVisibilityEmptyView(false)
        counter = 0

        subscriptions.add(favoriteEntries(counter * LIMIT)
                .doOnSubscribe { refreshLayout.isRefreshing = true }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ entries ->
                    refreshLayout.isRefreshing = false
                    if (entries.isEmpty()) {
                        setVisibilityEmptyView(true)
                    } else {
                        adapter.add(entries)
                    }
                }, {
                    setVisibilityEmptyView(false)
                    refreshLayout.isRefreshing = false
                    Toast.makeText(activity, R.string.feed_failure, Toast.LENGTH_SHORT).show()
                }))
    }

    private fun getNextFeed() {
        refreshLayout.isRefreshing = false

        counter++
        subscriptions.add(favoriteEntries(counter * LIMIT)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ entries ->
                    if (entries != null) {
                        adapter.add(entries)
                    } else {
                        Toast.makeText(activity, R.string.feed_failure, Toast.LENGTH_SHORT).show()
                    }
                }, {
                    Toast.makeText(activity, R.string.feed_failure, Toast.LENGTH_SHORT).show()
                }))
    }

    @SuppressLint("CheckResult")
    private fun favoriteEntries(skip: Int): Observable<Entries> {
        return favorites()
                .map { favorites -> favorites.map { it.memberId } }
                .flatMap { integers ->
                    NogiFeedApiClient.getMemberEntries(integers, skip, LIMIT)
                }
    }

    private fun favorites(): Observable<Favorites> {
        return Observable.create { subscriber ->
            try {
                subscriber.onNext(Favorites.all(context))
                subscriber.onCompleted()
            } catch (e: Throwable) {
                subscriber.onError(e)
            }
        }
    }

    private fun setVisibilityEmptyView(isVisible: Boolean) {
        if (isVisible) {
            adapter.clear()
            emptyView.visible()
        } else {
            emptyView.gone()
        }
    }
}
