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
import shts.jp.android.nogifeed.entities.Entry;
import shts.jp.android.nogifeed.models.Favorite;
import shts.jp.android.nogifeed.utils.DataStoreUtils;
import shts.jp.android.nogifeed.utils.DateUtils;
import shts.jp.android.nogifeed.utils.IntentUtils;
import shts.jp.android.nogifeed.utils.PicassoHelper;
import shts.jp.android.nogifeed.utils.TrackerUtils;
import shts.jp.android.nogifeed.utils.UrlUtils;

public class FeedListAdapter extends BindableAdapter<Entry> {

    private static final String TAG = FeedListAdapter.class.getSimpleName();

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

    public FeedListAdapter(Context context, List<Entry> entries) {
        super(context, entries);
    }

    public View newView(LayoutInflater inflater, final int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_entry, container, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    public void bindView(final Entry entry, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        final String profileImageUrl = UrlUtils.getMemberImageUrlFromFeedUrl(entry.link);
        Logger.d(TAG, "profileImageUrl : " + profileImageUrl);
        if (profileImageUrl == null) {
            // kenkyusei
            holder.profileImageView.setImageResource(R.drawable.kensyusei);
        } else {
            PicassoHelper.loadAndCircleTransform(getContext(), holder.profileImageView, profileImageUrl);
        }
        holder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: profile画像を押下中に色を変更するようにする
                IntentUtils.startMemberDetailActivity(getContext(), entry);
                TrackerUtils.sendTrack(getContext(), TAG,
                        "OnClicked", "-> Detail : " + "entry(" + entry.toString() + ")");
            }
        });

        holder.titleTextView.setText(entry.title);
        holder.authorNameTextView.setText(entry.name);
        holder.updatedTextView.setText(DateUtils.formatUpdated(entry.updated));
        boolean isFavorite =
                Favorite.exist(getContext(), UrlUtils.getMemberFeedUrl(entry.link));
        holder.favoriteImageView.setVisibility(isFavorite ? View.VISIBLE : View.GONE);
    }
}
