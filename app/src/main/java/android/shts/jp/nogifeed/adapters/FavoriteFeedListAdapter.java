package android.shts.jp.nogifeed.adapters;

import android.content.Context;
import android.os.Build;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.utils.DateUtils;
import android.shts.jp.nogifeed.utils.PicassoHelper;
import android.shts.jp.nogifeed.utils.StringUtils;
import android.shts.jp.nogifeed.utils.UrlUtils;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

// TODO: お気に入りメンバーがいないときは EmptyView を表示する
public class FavoriteFeedListAdapter extends RecyclableAdapter<Entry> {

    private static final String TAG = FavoriteFeedListAdapter.class.getSimpleName();
    private final Context mContext;

    public FavoriteFeedListAdapter(Context context, List list) {
        super(context, list);
        mContext = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView backgroudImageView;
        ImageView profileImageView;
        TextView titleTextView;
        TextView autherTextView;
        TextView updatedTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.card_title);
            autherTextView = (TextView) view.findViewById(R.id.authorname);
            backgroudImageView = (ImageView) view.findViewById(R.id.card_background);
            profileImageView = (ImageView) view.findViewById(R.id.profile_image);
            updatedTextView = (TextView) view.findViewById(R.id.updated);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Object object) {
        ViewHolder holder = (ViewHolder) viewHolder;
        Entry entry = (Entry) object;
        holder.titleTextView.setText(entry.title);
        holder.autherTextView.setText(entry.name);
        holder.updatedTextView.setText(DateUtils.formatUpdated(entry.published));
        final List<String> urls = StringUtils.getThumnailImageUrls(entry.content, 1);
        if (urls != null && !urls.isEmpty()) {
            PicassoHelper.load(
                    mContext, holder.backgroudImageView, urls.get(0));
        }
        final String profileImageUrl = UrlUtils.getMemberImageUrl(entry.link);
        Log.d(TAG, "profileImageUrl : " + profileImageUrl);
        if (!TextUtils.isEmpty(profileImageUrl)) {
            PicassoHelper.loadAndCircleTransform(
                    mContext, holder.profileImageView, profileImageUrl);
        } else {
            Log.w(TAG, "profileImageUrl is empty");
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
