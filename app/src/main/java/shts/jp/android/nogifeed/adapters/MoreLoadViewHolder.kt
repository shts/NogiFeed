package shts.jp.android.nogifeed.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import shts.jp.android.nogifeed.R

class MoreLoadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup): MoreLoadViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_more_load, parent, false)
            return MoreLoadViewHolder(itemView)
        }
    }
}