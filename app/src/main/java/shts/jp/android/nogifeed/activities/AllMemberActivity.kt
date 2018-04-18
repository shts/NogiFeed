package shts.jp.android.nogifeed.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_all_member.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.adapters.MemberGridAdapter
import shts.jp.android.nogifeed.adapters.OnMemberClickListener
import shts.jp.android.nogifeed.adapters.OnMemberFavoriteListener
import shts.jp.android.nogifeed.db.Favorite2
import shts.jp.android.nogifeed.models.Member
import shts.jp.android.nogifeed.viewmodels.AllMemberViewModel

class AllMemberActivity : AppCompatActivity() {

    companion object {
        fun getStartIntent(context: Context): Intent =
                Intent(context, AllMemberActivity::class.java)
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AllMemberViewModel::class.java)
    }

    private var adapter: MemberGridAdapter = MemberGridAdapter().apply {
        clickListener = object : OnMemberClickListener {
            override fun onClickMember(member: Member) {
                this@AllMemberActivity.setResult(Activity.RESULT_OK)
                val exits = favorites?.any { member.id == it.memberId } ?: return
                if (exits) {
                    viewModel.delete(member)
                } else {
                    viewModel.insert(member)
                }
            }
        }
        favoriteListener = object : OnMemberFavoriteListener {
            override fun isFavorite(member: Member): Boolean {
                val favorites = favorites ?: return false
                return favorites.any { it.memberId == member.id }
            }
        }
    }

    private var favorites: List<Favorite2>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_member)

        viewModel.membersData.observe(this, Observer {
            if (it != null) adapter.add(it)
        })
        viewModel.favorites.observe(this, Observer {
            if (it != null) {
                favorites = it
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.processing.observe(this, Observer {
            if (it != null) refreshLayout.isRefreshing = it
        })
        viewModel.result.observe(this, Observer {
            if (it != null && !it) {
                Toast.makeText(this, R.string.feed_failure, Toast.LENGTH_SHORT).show()
            }
        })

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_clear_purple_700_18dp)

        memberGridView.adapter = adapter
        memberGridView.layoutManager = GridLayoutManager(this, 3)

        refreshLayout.setOnRefreshListener {
            adapter.clear()
            viewModel.getAllMembers()
        }

        viewModel.getAllMembers()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
