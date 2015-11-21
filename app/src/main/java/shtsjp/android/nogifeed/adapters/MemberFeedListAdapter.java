package shts.jp.android.nogifeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.NotYetRead;
import shts.jp.android.nogifeed.utils.DateUtils;


public class MemberFeedListAdapter extends BindableAdapter<Entry> {

    private static final String TAG = MemberFeedListAdapter.class.getSimpleName();

    public class ViewHolder {
        TextView titleTextView;
        TextView authorNameTextView;
        TextView updatedTextView;
        View unreadMarker;

        public ViewHolder(View view) {
            titleTextView = (TextView) view.findViewById(R.id.title);
            authorNameTextView = (TextView) view.findViewById(R.id.authorname);
            updatedTextView = (TextView) view.findViewById(R.id.updated);
            unreadMarker = view.findViewById(R.id.marker);
        }
    }

    public MemberFeedListAdapter(Context context, List<Entry> entries) {
        super(context, entries);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_member_entry, container, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(Entry entry, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();
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

}
