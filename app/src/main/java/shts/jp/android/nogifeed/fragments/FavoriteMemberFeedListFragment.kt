package shts.jp.android.nogifeed.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_favorite_feed_list.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.activities.AllMemberActivity
import shts.jp.android.nogifeed.activities.BlogActivity
import shts.jp.android.nogifeed.adapters.FavoriteAdapter
import shts.jp.android.nogifeed.adapters.OnEntryClickListener
import shts.jp.android.nogifeed.adapters.OnPageMaxScrolledListener
import shts.jp.android.nogifeed.extensions.gone
import shts.jp.android.nogifeed.extensions.visible
import shts.jp.android.nogifeed.models.Entry
import shts.jp.android.nogifeed.viewmodels.FavoriteMemberFeedViewModel

class FavoriteMemberFeedListFragment : Fragment() {

    companion object {
        private val REQUEST_CODE = 0

        val newInstance: FavoriteMemberFeedListFragment =
                FavoriteMemberFeedListFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(FavoriteMemberFeedViewModel::class.java)
    }

    private var ids: List<Int>? = null

    private val adapter: FavoriteAdapter = FavoriteAdapter().apply {
        scrollListener = object : OnPageMaxScrolledListener {
            override fun onScrolledMaxPage() {
                //viewModel.getNextEntries()
            }
        }
        clickListener = object : OnEntryClickListener {
            override fun onClickEntry(entry: Entry) {
                startActivity(BlogActivity.getStartIntent(context, entry))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.favoriteData.observe(this, Observer {
            if (it != null && !it.isEmpty()) {
                ids = it.map { it.memberId }
                ids?.let { viewModel.getEntries(it) }
                setVisibilityEmptyView(false)
            } else {
                setVisibilityEmptyView(true)
                Toast.makeText(activity, R.string.empty_favorite, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.processing.observe(this, Observer {
            if (it != null) {
                refreshLayout.isRefreshing = it
            }
        })
        viewModel.result.observe(this, Observer {
            if (it != null && !it) {
                Toast.makeText(activity, R.string.feed_failure, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.entriesData.observe(this, Observer {
            if (it != null) {
                adapter.clear()
                adapter.add(it)
            }
        })
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

        refreshLayout.setOnRefreshListener {
            ids?.let { viewModel.getEntries(it) }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ids?.let { viewModel.getEntries(it) }
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
