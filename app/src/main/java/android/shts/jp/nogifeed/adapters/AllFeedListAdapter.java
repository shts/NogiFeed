package android.shts.jp.nogifeed.adapters;

import android.content.Context;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.utils.DateUtils;
import android.shts.jp.nogifeed.utils.PicassoHelper;
import android.shts.jp.nogifeed.utils.UrlUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AllFeedListAdapter extends ArrayAdapter<Entry> {

    private static final String TAG = AllFeedListAdapter.class.getSimpleName();

    public class ViewHolder {
        ImageView profileImageView;
        TextView titleTextView;
        TextView authorNameTextView;
        TextView updatedTextView;

        public ViewHolder(View view) {
            profileImageView = (ImageView) view.findViewById(R.id.profile_image);
            titleTextView = (TextView) view.findViewById(R.id.title);
            authorNameTextView = (TextView) view.findViewById(R.id.authorname);
            updatedTextView = (TextView) view.findViewById(R.id.updated);
        }
    }

    public AllFeedListAdapter(Context context, List<Entry> entries) {
        super(context, -1, entries);
        setup();
    }

    private void setup() {
        mInflater = LayoutInflater.from(getContext());
    }

    private LayoutInflater mInflater;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = newView(mInflater, position, parent);
        bindView(getItem(position), position, convertView);
        return convertView;
    }

    public View newView(LayoutInflater inflater, final int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_entry, container, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    public void bindView(Entry item, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        // TODO: need to profile image chache
        if (item.profileImage == null) {
            String profileImageUrl = UrlUtils.getMemberImageUrl(item.link);
            Log.d(TAG, "profileImageUrl : " + profileImageUrl);
            if (profileImageUrl == null) {
                // kenkyusei
            } else {
                PicassoHelper.loadAndCircleTransform(getContext(), holder.profileImageView, profileImageUrl);
            }
        } else {
            holder.profileImageView.setImageBitmap(item.profileImage);
        }
        holder.titleTextView.setText(item.title);
        holder.authorNameTextView.setText(item.name);
        holder.updatedTextView.setText(DateUtils.formatUpdated(item.updated));
    }
}
