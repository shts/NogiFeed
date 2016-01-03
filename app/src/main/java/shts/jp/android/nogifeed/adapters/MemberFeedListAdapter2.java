package shts.jp.android.nogifeed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.NotYetRead;
import shts.jp.android.nogifeed.utils.DateUtils;

public class MemberFeedListAdapter2 extends RecyclableAdapter<Entry> {

    private static final String TAG = MemberFeedListAdapter2.class.getSimpleName();

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView authorNameTextView;
        TextView updatedTextView;
        View unreadMarker;

        public ViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.title);
            authorNameTextView = (TextView) view.findViewById(R.id.authorname);
            updatedTextView = (TextView) view.findViewById(R.id.updated);
            unreadMarker = view.findViewById(R.id.marker);
        }
    }

    public MemberFeedListAdapter2(Context context, List<Entry> list) {
        super(context, list);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Object object) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Entry entry = (Entry) object;
        holder.titleTextView.setText(entry.getTitle());
        holder.authorNameTextView.setText(entry.getAuthor());
        holder.updatedTextView.setText(DateUtils.dateToString(entry.getPublishedDate()));
        final boolean unread = NotYetRead.isRead(entry.getBlogUrl());
        if (unread) {
            holder.unreadMarker.setVisibility(View.VISIBLE);
        } else {
            // not View.GONE
            holder.unreadMarker.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.list_item_member_entry, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
}
