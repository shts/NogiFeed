package shts.jp.android.nogifeed.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_entry.view.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.models.Entries
import shts.jp.android.nogifeed.models.Entry
import shts.jp.android.nogifeed.providers.dao.Favorites
import shts.jp.android.nogifeed.utils.PicassoHelper

interface OnPageMaxScrolledListener {
    fun onScrolledMaxPage() {}
}

interface OnEntryClickListener {
    fun onClickEntry(entry: Entry) {}
    fun onClickProfileImage(entry: Entry) {}
}

class AllFeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val TYPE_ENTRY = 0
        private val TYPE_FOOTER = 1
    }

    private val entries: Entries = Entries()

    var scrollListener: OnPageMaxScrolledListener? = null
    var clickListener: OnEntryClickListener? = null

    fun add(entries: Entries) {
        this.entries.addAll(entries)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            if (viewType == TYPE_ENTRY) {
                EntryViewHolder.newInstance(parent)
            } else {
                MoreLoadViewHolder.newInstance(parent)
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            scrollListener?.onScrolledMaxPage()
            return
        }

        val entryViewHolder: EntryViewHolder = holder as EntryViewHolder
        val entry: Entry = entries[position]

        entryViewHolder.itemView.apply {
            rootView.setOnClickListener { clickListener?.onClickEntry(entry) }
            PicassoHelper.loadAndCircleTransform(
                    profileImage, entry.memberImageUrl, R.drawable.kensyusei)
            profileImage.setOnClickListener { clickListener?.onClickProfileImage(entry) }

            val favorite = Favorites.exist(favoriteIcon.context, entry.memberId)
            favoriteIcon.visibility = if (favorite) View.VISIBLE else View.GONE

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

class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup): EntryViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_entry, parent, false)
            return EntryViewHolder(itemView)
        }
    }
}