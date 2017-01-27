package shts.jp.android.nogifeed.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.activities.MemberDetailActivity;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.providers.dao.Favorites;
import shts.jp.android.nogifeed.utils.PicassoHelper;

public class AllFeedListAdapter extends BindableAdapter<Entry> {

    private static final String TAG = AllFeedListAdapter.class.getSimpleName();

    private OnPageMaxScrolledListener pageMaxScrolledListener;

    public interface OnPageMaxScrolledListener {
        public void onScrolledMaxPage();
    }

    public AllFeedListAdapter(Context context, List<Entry> list) {
        super(context, list);
    }

    public void setPageMaxScrolledListener(OnPageMaxScrolledListener listener) {
        pageMaxScrolledListener = listener;
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

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_entry, container, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final Entry entry, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        final String profileImageUrl = entry.getMemberImageUrl();
        if (TextUtils.isEmpty(profileImageUrl)) {
            // kenkyusei
            holder.profileImageView.setImageResource(R.drawable.kensyusei);
        } else {
            PicassoHelper.loadAndCircleTransform(getContext(), holder.profileImageView, profileImageUrl);
        }
        holder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MemberDetailActivity.getStartIntent(
                        getContext(), entry.getMemberId());
                getContext().startActivity(intent);
            }
        });

        holder.titleTextView.setText(entry.getTitle());
        holder.authorNameTextView.setText(entry.getMemberName());
        holder.updatedTextView.setText(entry.getPublished());
        holder.favoriteImageView.setVisibility(
                Favorites.exist(getContext(), entry.getMemberId()) ? View.VISIBLE : View.GONE);

        if (getCount() - 1 <= position) {
            pageMaxScrolledListener.onScrolledMaxPage();
        }
    }

    public void add(List<Entry> entries) {
        for (Entry e : entries) {
            add(e);
        }
    }
}
