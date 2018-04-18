package shts.jp.android.nogifeed.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_all_feed_list.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.activities.BlogActivity
import shts.jp.android.nogifeed.activities.MemberDetailActivity
import shts.jp.android.nogifeed.adapters.AllFeedAdapter
import shts.jp.android.nogifeed.adapters.OnEntryClickListener
import shts.jp.android.nogifeed.adapters.OnPageMaxScrolledListener
import shts.jp.android.nogifeed.models.Entry
import shts.jp.android.nogifeed.viewmodels.AllFeedListViewModel

class AllFeedListFragment : Fragment() {

    companion object {
        val newInstance: AllFeedListFragment = AllFeedListFragment()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AllFeedListViewModel::class.java)
    }

    private val adapter: AllFeedAdapter = AllFeedAdapter().apply {
        scrollListener = object : OnPageMaxScrolledListener {
            override fun onScrolledMaxPage() {
                viewModel.getNextEntries()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.allEntriesLiveData.observe(this, Observer {
            if (it != null) adapter.add(it)
        })

        viewModel.allProcessing.observe(this, Observer {
            if (it != null) refreshLayout.isRefreshing = it
        })

        viewModel.allResult.observe(this, Observer {
            if (it != null && !it) {
                Toast.makeText(activity, R.string.feed_failure, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.stateData.observe(this, Observer {
            if (it != null) adapter.notifyDataSetChanged()
        })
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
        refreshLayout.setOnRefreshListener { viewModel.getAllEntries() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getAllEntries()
    }
}
