package shts.jp.android.nogifeed.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_news_feed_list.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.activities.NewsBrowseActivity
import shts.jp.android.nogifeed.adapters.NewsAdapter
import shts.jp.android.nogifeed.adapters.OnNewsClickListener
import shts.jp.android.nogifeed.api.AsyncNewsClient
import shts.jp.android.nogifeed.entities.News

class NewsListFragment : Fragment() {

    companion object {
        val newInstance: NewsListFragment =
                NewsListFragment()
    }

    private val subscriptions: CompositeSubscription = CompositeSubscription()

    val adapter: NewsAdapter = NewsAdapter().apply {
        clickListener = object : OnNewsClickListener {
            override fun onClickNews(news: News) {
                startActivity(NewsBrowseActivity.getStartIntent(activity, news))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_feed_list, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsFeedList.adapter = adapter
        newsFeedList.addItemDecoration(DividerItemDecoration(context, VERTICAL))
        fab.setOnClickListener { showFilterDialog() }
        refreshLayout.setOnRefreshListener { getAllNews() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getAllNews()
    }

    override fun onDestroyView() {
        subscriptions.unsubscribe()
        super.onDestroyView()
    }

    private fun getAllNews() {
        subscriptions.add(AsyncNewsClient.get(activity)
                .doOnSubscribe { refreshLayout.isRefreshing = true }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ newsList ->
                    refreshLayout.isRefreshing = false
                    newsList?.let {
                        adapter.add(it)
                    } ?: run {
                        Snackbar.make(coordinatorLayout, R.string.failed_to_get_news, Snackbar.LENGTH_LONG).show()
                    }
                }, { e ->
                    refreshLayout.isRefreshing = false
                    e.printStackTrace()
                }))
    }

    private fun showFilterDialog() {
        val typeList = News.Type.getTypeList(activity)
        val filter = News.Type.getFilter(activity)

        AlertDialog.Builder(activity)
                .setTitle(R.string.news_filter_dialog_title)
                .setMultiChoiceItems(typeList, filter, { _, which, isChecked -> filter[which] = isChecked })
                .setPositiveButton(R.string.news_filter_dialog_ok, { _, _ ->
                    News.Type.setFilter(activity, filter)
                    getAllNews()
                })
                .setNegativeButton(R.string.news_filter_dialog_cancel, null).show()
    }
}
