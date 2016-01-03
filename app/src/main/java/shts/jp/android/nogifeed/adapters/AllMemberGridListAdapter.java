package shts.jp.android.nogifeed.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Favorite;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.utils.PicassoHelper;

public class AllMemberGridListAdapter extends BindableAdapter<Member> {

    private static final String TAG = AllMemberGridListAdapter.class.getSimpleName();

    public AllMemberGridListAdapter(Context context, List<Member> list) {
        super(context, list);
    }

    class ViewHolder {
        public TextView titleTextView;
        public ImageView profileImageView;
        public ImageView favoriteImageView;
        ViewHolder (View view) {
            titleTextView = (TextView) view.findViewById(R.id.member_name);
            profileImageView = (ImageView) view.findViewById(R.id.profile_image);
            favoriteImageView = (ImageView) view.findViewById(R.id.favorite_icon);
        }
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_member, null);
        final ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(Member member, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        holder.titleTextView.setText(member.getNameMain());
        holder.favoriteImageView.setVisibility(
                Favorite.exist(member.getObjectId()) ? View.VISIBLE : View.GONE);

        if (TextUtils.isEmpty(member.getProfileImageUrl())) {
            holder.profileImageView.setImageResource(R.drawable.kensyusei);
        } else {
            PicassoHelper.loadAndCircleTransform(
                    getContext(), holder.profileImageView, member.getProfileImageUrl());
        }
    }
}
