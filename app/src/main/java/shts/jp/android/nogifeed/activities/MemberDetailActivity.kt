package shts.jp.android.nogifeed.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.util.Log
import kotlinx.android.synthetic.main.fragment_member_detail.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.adapters.MemberFeedAdapter
import shts.jp.android.nogifeed.adapters.OnMemberEntryClickListener
import shts.jp.android.nogifeed.adapters.OnPageMaxScrolledListener
import shts.jp.android.nogifeed.db.Favorite2
import shts.jp.android.nogifeed.models.Entry
import shts.jp.android.nogifeed.models.Member
import shts.jp.android.nogifeed.viewmodels.MemberDetailViewModel

class MemberDetailActivity : AppCompatActivity() {

    companion object {
        private val EXTRA_MEMBER_ID = "memberId"

        fun getStartIntent(context: Context, member: Member): Intent {
            return getStartIntent(context, member.id!!)
        }

        fun getStartIntent(context: Context, memberId: Int): Intent {
            return Intent(context, MemberDetailActivity::class.java)
                    .putExtra(EXTRA_MEMBER_ID, memberId)
        }
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MemberDetailViewModel::class.java)
    }

    private val adapter: MemberFeedAdapter = MemberFeedAdapter().apply {
        scrollListener = object : OnPageMaxScrolledListener {
            override fun onScrolledMaxPage() {

                viewModel.getNextMemberFeed(memberId)
            }
        }
        clickListener = object : OnMemberEntryClickListener {
            override fun onClickMemberEntry(entry: Entry) {
                startActivity(BlogActivity.getStartIntent(this@MemberDetailActivity, entry))
            }
        }
    }

    // Room と LiveData の Observe の関係が不明
    // データ取得と監視を切り離したい
    private var isFirst: Boolean = true
    private var memberId: Int = -1
    private var favorites: List<Favorite2>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_member_detail)

        memberId = intent?.getIntExtra(EXTRA_MEMBER_ID, -1) ?: return

        fab.setOnClickListener {
            val exits = favorites?.any { memberId == it.memberId } ?: return@setOnClickListener
            if (exits) {
                viewModel.delete(memberId)
            } else {
                viewModel.insert(memberId)
            }
        }

        memberEntryList.addItemDecoration(DividerItemDecoration(this, VERTICAL))
        memberEntryList.adapter = adapter

        collapsingToolbar.setCollapsedTitleTextColor(
                ContextCompat.getColor(this, android.R.color.white))
        collapsingToolbar.setExpandedTitleColor(
                ContextCompat.getColor(this, android.R.color.transparent))

        viewModel.favoritesQueryResult.observe(this, Observer {
            if (it != null) {
                val f = it.any { it.memberId == memberId }
                if (f) {

                } else {

                }
            }
        })

        viewModel.memberLiveData.observe(this, Observer {
            if (it != null) {
                memberDetailView.setup(it)
                viewModel.getMemberFeed(memberId)
            }
        })

        viewModel.favorites.observe(this, Observer {
            Log.d(MemberDetailActivity::class.java.simpleName,
                    "favorites")
            if (it != null) {
                favorites = it

                // 画面起動時に呼ばれてしまうので初回はスキップする
                if (isFirst) {
                    isFirst = false
                    return@Observer
                }

                val f = favorites?.any { it.memberId == memberId }
                if (f != null) {
                    val message = if (f) {
                        R.string.registered_favorite_member
                    } else {
                        R.string.unregistered_favorite_member
                    }
                    Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
                } else {
//                    Snackbar.make(coordinatorLayout, R.string.unregistered_favorite_member, Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.entriesLiveData.observe(this, Observer {
            if (it != null && !it.isEmpty()) {
                adapter.add(it)
                collapsingToolbar.title = it[0].memberName
            } else {
                // 空の場合はもう Footer を表示しない
                adapter.showFooter = false
                adapter.scrollListener = null
            }
        })

        viewModel.resultLiveData.observe(this, Observer {
            when (it) {
                MemberDetailViewModel.Result.Failure -> {
                    Snackbar.make(coordinatorLayout, R.string.feed_failure, Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                }
            }
        })

        viewModel.getMember(memberId)
    }
}
