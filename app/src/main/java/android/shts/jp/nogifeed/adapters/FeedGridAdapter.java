package android.shts.jp.nogifeed.adapters;

import android.content.Context;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.models.Entries;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * show all feed grid.
 */
public class FeedGridAdapter extends RecyclableAdapter {

    private final Context mContext;
    private final Entries mEntries;

    public FeedGridAdapter(Context context, Entries entries) {
        super(context, entries);
        mContext = context;
        mEntries = entries;

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView profileImageView;
        public ImageView favoriteImageView;
        ViewHolder (View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.member_name);
            profileImageView = (ImageView) view.findViewById(R.id.profile_image);
            favoriteImageView = (ImageView) view.findViewById(R.id.favorite_icon);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Object object) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup viewGroup) {
        return null;
    }
}
