package shts.jp.android.nogifeed.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.activities.MemberDetailActivity;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.utils.DateUtils;
import shts.jp.android.nogifeed.utils.PicassoHelper;
import shts.jp.android.nogifeed.utils.TrackerUtils;

public class FavoriteFeedListAdapter extends RecyclableAdapter<Entry> {

    private static final String TAG = FavoriteFeedListAdapter.class.getSimpleName();
    private final Context context;

    public FavoriteFeedListAdapter(Context context, List list) {
        super(context, list);
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView backgroundImageView;
        ImageView profileImageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView updatedTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.card_title);
            authorTextView = (TextView) view.findViewById(R.id.authorname);
            backgroundImageView = (ImageView) view.findViewById(R.id.card_background);
            profileImageView = (ImageView) view.findViewById(R.id.profile_image);
            updatedTextView = (TextView) view.findViewById(R.id.updated);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Object object) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        final Entry entry = (Entry) object;
        holder.titleTextView.setText(entry.getTitle());
        holder.authorTextView.setText(entry.getAuthor());
        holder.updatedTextView.setText(DateUtils.dateToString(entry.getPublishedDate()));
        List<String> urls = entry.getUploadedThumbnailUrlList();
        if (urls != null && !urls.isEmpty()) {
            PicassoHelper.load(
                    context, holder.backgroundImageView, urls.get(0));
        } else {
            holder.backgroundImageView.setImageResource(R.drawable.noimage);
        }

        holder.backgroundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(BlogActivity.getStartIntent(context, entry.getObjectId()));
                TrackerUtils.sendTrack(context, TAG,
                        "OnClicked", "-> Blog : " + "entry(" + entry.toString() + ")");
            }
        });

        final String profileImageUrl = entry.getAuthorImageUrl();
        if (!TextUtils.isEmpty(profileImageUrl)) {
            PicassoHelper.loadAndCircleTransform(
                    context, holder.profileImageView, profileImageUrl);
            holder.profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(MemberDetailActivity
                            .getStartIntent(context, entry.getAuthorId()));
                    TrackerUtils.sendTrack(context, TAG,
                            "OnClicked", "-> Detail : " + "entry(" + entry.toString() + ")");
                }
            });
        } else {
            Logger.w(TAG, "profileImageUrl is empty");
            holder.profileImageView.setImageResource(R.drawable.kensyusei);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.list_item_card, viewGroup, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                p.setMargins(8, 8, 8, 8);
                view.requestLayout();
            }
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
}
