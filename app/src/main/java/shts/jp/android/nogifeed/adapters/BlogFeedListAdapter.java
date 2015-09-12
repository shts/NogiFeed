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
import shts.jp.android.nogifeed.entities.BlogEntry;
import shts.jp.android.nogifeed.utils.DataStoreUtils;
import shts.jp.android.nogifeed.utils.IntentUtils;
import shts.jp.android.nogifeed.utils.PicassoHelper;
import shts.jp.android.nogifeed.utils.TrackerUtils;

public class BlogFeedListAdapter extends BindableAdapter<BlogEntry> {

    private static final String TAG = BlogFeedListAdapter.class.getSimpleName();

    private OnPageMaxScrolledListener mOnPageMaxScrolledListener;

    public interface OnPageMaxScrolledListener {
        public void onScrolledMaxPage();
    }

    public class ViewHolder {
        ImageView profileImageView;
        ImageView favoriteImageView;
        TextView titleTextView;
        TextView authorNameTextView;
        TextView updatedTextView;

        public ViewHolder(View view) {
            profileImageView = (ImageView) view.findViewById(R.id.profile_image);
            favoriteImageView = (ImageView) view.findViewById(R.id.favorite_icon);
            titleTextView = (TextView) view.findViewById(R.id.title);
            authorNameTextView = (TextView) view.findViewById(R.id.authorname);
            updatedTextView = (TextView) view.findViewById(R.id.updated);
        }
    }

    public BlogFeedListAdapter(Context context, List<BlogEntry> list) {
        super(context, list);
    }

    public void setOnPageMaxScrolled(OnPageMaxScrolledListener listener) {
        mOnPageMaxScrolledListener = listener;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_entry, container, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final BlogEntry blogEntry, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        final String profileImageUrl = blogEntry.getProfileImageUrl();
        Logger.d(TAG, "profileImageUrl : " + profileImageUrl);
        if (profileImageUrl == null) {
            // kenkyusei
            holder.profileImageView.setImageResource(R.drawable.kensyusei);
        } else {
            PicassoHelper.loadAndCircleTransform(
                    getContext(), holder.profileImageView, profileImageUrl);
        }
        holder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: profile画像を押下中に色を変更するようにする
                IntentUtils.startMemberDetailActivity(getContext(), blogEntry);
                TrackerUtils.sendTrack(getContext(), TAG,
                        "OnClicked", "-> Detail : " + "entry(" + blogEntry.toString() + ")");
            }
        });

        holder.titleTextView.setText(blogEntry.title);
        holder.authorNameTextView.setText(blogEntry.author);
//        holder.updatedTextView.setText(DateUtils.formatUpdated(blogEntry.date));
        holder.updatedTextView.setText(blogEntry.date);
        boolean isFavorite =
                DataStoreUtils.alreadyExist(getContext(), blogEntry.getFeedUrl());
        holder.favoriteImageView.setVisibility(isFavorite ? View.VISIBLE : View.GONE);

        if (getCount() - 1 <= position) {
            mOnPageMaxScrolledListener.onScrolledMaxPage();
        }
    }
}
