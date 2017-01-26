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
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.providers.dao.Favorites;
import shts.jp.android.nogifeed.utils.PicassoHelper;

class AllMemberListAdapter extends BindableAdapter<Member> {

    private final Context context;

    private class ViewHolder {
        ImageView profileImageView;
        ImageView favoriteImageView;
        TextView authorNameTextView;

        ViewHolder(View view) {
            profileImageView = (ImageView) view.findViewById(R.id.profile_image);
            favoriteImageView = (ImageView) view.findViewById(R.id.favorite_icon);
            authorNameTextView = (TextView) view.findViewById(R.id.member_name);
        }
    }

    public AllMemberListAdapter(Context context, List<Member> list) {
        super(context, list);
        this.context = context;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_all_member, container, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(Member member, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.authorNameTextView.setText(member.getNameMain());
        holder.favoriteImageView.setVisibility(
                Favorites.exist(getContext(), member.getId()) ? View.VISIBLE : View.GONE);

        if (TextUtils.isEmpty(member.getImageUrl()) || position == 0) {
            holder.profileImageView.setImageResource(R.drawable.kensyusei);
        } else {
            PicassoHelper.loadAndCircleTransform(
                    context, holder.profileImageView, member.getImageUrl());
        }
    }
}
