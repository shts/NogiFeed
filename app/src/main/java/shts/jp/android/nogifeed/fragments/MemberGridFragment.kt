package shts.jp.android.nogifeed.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_member_grid.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.activities.MemberDetailActivity
import shts.jp.android.nogifeed.adapters.MemberGridAdapter
import shts.jp.android.nogifeed.adapters.OnMemberClickListener
import shts.jp.android.nogifeed.api.NogiFeedApiClient
import shts.jp.android.nogifeed.models.Member
import shts.jp.android.nogifeed.providers.FavoriteContentObserver

class MemberGridFragment : Fragment() {

    companion object {
        fun newInstance(): MemberGridFragment = MemberGridFragment()
    }

    private val subscriptions: CompositeSubscription = CompositeSubscription()
    private var adapter: MemberGridAdapter = MemberGridAdapter().apply {
        clickListener = object : OnMemberClickListener {
            override fun onClickMember(member: Member) {
                startActivity(MemberDetailActivity.getStartIntent(context, member))
            }
        }
    }

    private val observer = object : FavoriteContentObserver() {
        override fun onChangeState(@State state: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_member_grid, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        memberGridView.adapter = adapter
        memberGridView.layoutManager = GridLayoutManager(context, 3)

        refreshLayout.setOnRefreshListener { getAllMembers() }
        refreshLayout.isRefreshing = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getAllMembers()
    }

    override fun onResume() {
        super.onResume()
        observer.register(context)
    }

    override fun onPause() {
        observer.unregister(context)
        super.onPause()
    }

    override fun onDestroyView() {
        observer.unregister(context)
        subscriptions.unsubscribe()
        super.onDestroyView()
    }

    private fun getAllMembers() {
        subscriptions.add(NogiFeedApiClient.getAllMembers()
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
