package android.shts.jp.nogifeed.adapters;

import android.content.Context;
import android.content.Intent;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.activities.MemberDetailActivity;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.utils.DateUtils;
import android.shts.jp.nogifeed.utils.PicassoHelper;
import android.shts.jp.nogifeed.utils.UrlUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FeedListAdapter extends BindableAdapter<Entry> {

    private static final String TAG = FeedListAdapter.class.getSimpleName();

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

    public FeedListAdapter(Context context, List<Entry> entries) {
        super(context, entries);
    }

    public View newView(LayoutInflater inflater, final int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_entry, container, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    public void bindView(final Entry item, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        // TODO: need to profile image chache
//        if (item.profileImage == null) {
//            String profileImageUrl = UrlUtils.getMemberImageUrl(item.link);
//            Log.d(TAG, "profileImageUrl : " + profileImageUrl);
//            if (profileImageUrl == null) {
//                // kenkyusei
//            } else {
//                PicassoHelper.loadAndCircleTransform(getContext(), holder.profileImageView, profileImageUrl);
//            }
//
//            holder.profileImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
//
//        } else {
//            holder.profileImageView.setImageBitmap(item.profileImage);
//        }
        final String profileImageUrl = UrlUtils.getMemberImageUrl(item.link);
        Log.d(TAG, "profileImageUrl : " + profileImageUrl);
        if (profileImageUrl == null) {
            // kenkyusei
            holder.profileImageView.setImageResource(R.drawable.kensyusei);
            holder.profileImageView.setOnClickListener(null);

        } else {
            PicassoHelper.loadAndCircleTransform(getContext(), holder.profileImageView, profileImageUrl);

            holder.profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getContext(), MemberDetailActivity.class);
                    i.putExtra(Entry.KEY, item);
                    getContext().startActivity(i);
                }
            });
        }

        holder.titleTextView.setText(item.title);
        holder.authorNameTextView.setText(item.name);
        holder.updatedTextView.setText(DateUtils.formatUpdated(item.updated));
    }
}
