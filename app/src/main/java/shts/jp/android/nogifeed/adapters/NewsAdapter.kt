package shts.jp.android.nogifeed.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_news.view.*
import shts.jp.android.nogifeed.R
import shts.jp.android.nogifeed.entities.News
import shts.jp.android.nogifeed.entities.NewsList


interface OnNewsClickListener {
    fun onClickNews(news: News)
}

class NewsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var clickListener: OnNewsClickListener? = null

    private val newsList: NewsList = NewsList()

    fun add(newsList: NewsList) {
        this.newsList.addAll(newsList)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            NewsViewHolder.newInstance(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            val news = newsList[position]
            rootView.setOnClickListener { clickListener?.onClickNews(news) }
            newsIcon.setImageResource(news.newsType.iconResource)
            newsTitle.text = news.title
            newsDate.text = news.date
        }
    }

    override fun getItemCount(): Int = newsList.size
}

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun newInstance(parent: ViewGroup): NewsViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_news, parent, false)
            return NewsViewHolder(itemView)
        }
    }
}
