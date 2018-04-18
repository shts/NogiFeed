package shts.jp.android.nogifeed.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_favorite.view.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.models.Entries
import shts.jp.android.nogifeed.models.Entry
import shts.jp.android.nogifeed.utils.PicassoHelper

class FavoriteAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    fun clear() {
        this.entries.clear()
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            if (viewType == TYPE_ENTRY) {
                FavoriteViewHolder.newInstance(parent)
            } else {
                MoreLoadViewHolder.newInstance(parent)
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            scrollListener?.onScrolledMaxPage()
            return
        }

        val entryViewHolder: FavoriteViewHolder = holder as FavoriteViewHolder
        val entry: Entry = entries[position]

        entryViewHolder.itemView.apply {
            rootView.setOnClickListener { clickListener?.onClickEntry(entry) }

            if (entry.originalThumbnailUrls.isEmpty()) {
                entryThumbnailView.setImageResource(R.drawable.noimage)
            } else {
                PicassoHelper.load(entryThumbnailView,
                        entry.originalThumbnailUrls[0], R.drawable.noimage)
            }
            PicassoHelper.loadAndCircleTransform(
                    profileImageView, entry.memberImageUrl, R.drawable.kensyusei)

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

class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup): FavoriteViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_favorite, parent, false)
            return FavoriteViewHolder(itemView)
        }
    }
}
