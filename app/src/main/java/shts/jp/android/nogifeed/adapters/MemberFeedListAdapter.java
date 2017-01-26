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
import shts.jp.android.nogifeed.providers.dao.UnreadArticles;

public class MemberFeedListAdapter extends RecyclableAdapter<Entry> {

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView authorNameTextView;
        TextView updatedTextView;
        View unreadMarker;
        View root;

        ViewHolder(View view) {
            super(view);
            root = view;
            titleTextView = (TextView) view.findViewById(R.id.title);
            authorNameTextView = (TextView) view.findViewById(R.id.authorname);
            updatedTextView = (TextView) view.findViewById(R.id.updated);
            unreadMarker = view.findViewById(R.id.marker);
        }
    }

    public interface OnItemClickCallback {
        void onClick(Entry entry);
    }

    private OnItemClickCallback clickCallback;

    public void setClickCallback(OnItemClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    private Context context;

    public MemberFeedListAdapter(Context context, List<Entry> list) {
        super(context, list);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Object object) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Entry entry = (Entry) object;
        holder.titleTextView.setText(entry.getTitle());
        holder.authorNameTextView.setText(entry.getMemberName());
        holder.updatedTextView.setText(entry.getPublished());
        final boolean unread = UnreadArticles.exist(context, entry.getUrl());
        if (unread) {
            holder.unreadMarker.setVisibility(View.VISIBLE);
        } else {
            // not View.GONE
            holder.unreadMarker.setVisibility(View.INVISIBLE);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCallback != null) clickCallback.onClick(entry);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.list_item_member_entry, viewGroup, false);
        return new ViewHolder(view);
    }
}
