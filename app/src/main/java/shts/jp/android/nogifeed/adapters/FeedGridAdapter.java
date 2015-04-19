package shts.jp.android.nogifeed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import shts.jp.android.nogifeed.R;

/**
 * show all feed grid for tablet.
 */
public class FeedGridAdapter extends RecyclableAdapter {

    private final Context mContext;
    private final shts.jp.android.nogifeed.models.Entries mEntries;

    public FeedGridAdapter(Context context, shts.jp.android.nogifeed.models.Entries entries) {
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
