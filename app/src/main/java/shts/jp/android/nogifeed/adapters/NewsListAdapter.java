package shts.jp.android.nogifeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.entities.News;

public class NewsListAdapter extends BindableAdapter<News> {

    private static final String TAG = NewsListAdapter.class.getSimpleName();

    private static class ViewHolder {

        ImageView iconImageView;
        TextView titleTextView;
        TextView dateTextView;

        ViewHolder(View view) {
            iconImageView = (ImageView) view.findViewById(R.id.news_icon);
            titleTextView = (TextView) view.findViewById(R.id.news_title);
            dateTextView = (TextView) view.findViewById(R.id.news_date);
        }
    }

    public NewsListAdapter(Context context, List<News> list) {
        super(context, list);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_news, container, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(News news, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        if (news != null) {
            holder.titleTextView.setText(news.title);
            holder.dateTextView.setText(news.date);
            final News.Type type = news.getNewsType();
            if (type != null) {
                holder.iconImageView.setImageResource(news.getNewsType().getIconResource());
            } else {
                Logger.w(TAG, "news type is null. iconType(" + news.iconType + ")");
            }
        }
    }
}
