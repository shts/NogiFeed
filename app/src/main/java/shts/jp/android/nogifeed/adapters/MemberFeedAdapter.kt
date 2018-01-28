package shts.jp.android.nogifeed.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_member_entry.view.*
import kotlinx.android.synthetic.main.list_item_more_load.view.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.models.Entries
import shts.jp.android.nogifeed.models.Entry
import shts.jp.android.nogifeed.providers.dao.UnreadArticles

interface OnMemberEntryClickListener {
    fun onClickMemberEntry(entry: Entry)
}

class MemberFeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val TYPE_ENTRY = 0
        private val TYPE_FOOTER = 1
    }

    private val entries: Entries = Entries()

    var showFooter: Boolean = true
    var scrollListener: OnPageMaxScrolledListener? = null
    var clickListener: OnMemberEntryClickListener? = null

    fun add(entries: Entries) {
        this.entries.addAll(entries)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            if (viewType == TYPE_ENTRY) {
                MemberFeedViewHolder.newInstance(parent)
            } else {
                MoreLoadViewHolder.newInstance(parent)
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            scrollListener?.onScrolledMaxPage()
            val moreLoadViewHolder: MoreLoadViewHolder = holder as MoreLoadViewHolder
            moreLoadViewHolder.itemView.progressBar.visibility =
                    if (showFooter) View.VISIBLE else View.GONE
            moreLoadViewHolder.itemView.emptyEntryTextView.visibility =
                    if (showFooter) View.GONE else View.VISIBLE
            return
        }

        val entryViewHolder: MemberFeedViewHolder = holder as MemberFeedViewHolder
        val entry: Entry = entries[position]

        entryViewHolder.itemView.apply {
            rootView.setOnClickListener { clickListener?.onClickMemberEntry(entry) }
            val unread = UnreadArticles.exist(marker.context, entry.url)
            marker.visibility = if (unread) View.VISIBLE else View.INVISIBLE
            titleTextView.text = entry.title
            authorNameTextView.text = entry.memberName
            updatedTextView.text = entry.published
        }
    }

    override fun getItemViewType(position: Int): Int =
            if (position == itemCount - 1 && entries.size != 0) {
                TYPE_FOOTER
            } else {
                TYPE_ENTRY
            }

    override fun getItemCount(): Int =
            if (entries.size != 0) {
                entries.size + 1
            } else {
                0
            }
}

class MemberFeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup): MemberFeedViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_member_entry, parent, false)
            return MemberFeedViewHolder(itemView)
        }
    }
}

