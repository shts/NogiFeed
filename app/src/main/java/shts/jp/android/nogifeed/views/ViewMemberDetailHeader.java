package shts.jp.android.nogifeed.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.squareup.otto.Subscribe;

import me.gujun.android.taggroup.TagGroup;
import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.models.Favorite;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;
import shts.jp.android.nogifeed.utils.PicassoHelper;

public class ViewMemberDetailHeader extends LinearLayout {

    private static final String TAG = ViewMemberDetailHeader.class.getSimpleName();

    private View rootView;

    private ImageView profileImageView;
    private ImageView favoriteIcon;

    private TextView nameMainTextView;
    private TextView nameSubTextView;

    private TextView birthdayTextView;
    private TextView bloodTypeTextView;
    private TextView constellationTextView;
    private TextView heightTextView;

    private TagGroup tags;

    public ViewMemberDetailHeader(Context context) {
        this(context, null);
    }

    public ViewMemberDetailHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_member_detail_header, this);
        profileImageView = (ImageView) rootView.findViewById(R.id.profile_image);
        favoriteIcon = (ImageView) rootView.findViewById(R.id.favorite_icon);
        nameMainTextView = (TextView) rootView.findViewById(R.id.name_main);
        nameSubTextView = (TextView) rootView.findViewById(R.id.name_sub);
        birthdayTextView = (TextView) rootView.findViewById(R.id.birthday);
        bloodTypeTextView = (TextView) rootView.findViewById(R.id.blood_type);
        constellationTextView = (TextView) rootView.findViewById(R.id.constellation);
        heightTextView = (TextView) rootView.findViewById(R.id.height);
        tags = (TagGroup) rootView.findViewById(R.id.tags);
    }

    public void setup(final String memberObjectId) {

        if (Favorite.exist(memberObjectId)) {
            favoriteIcon.setVisibility(View.VISIBLE);
        } else {
            favoriteIcon.setVisibility(View.GONE);
        }

        Member.getReference(memberObjectId).fetchIfNeededInBackground(new GetCallback<Member>() {
            @Override
            public void done(Member member, ParseException e) {

                final Context context = getContext();
                PicassoHelper.loadAndCircleTransform(
                        context, profileImageView, member.getProfileImageUrl());

                nameMainTextView.setText(member.getNameMain());
                nameSubTextView.setText(member.getNameSub());

                final Resources res = context.getResources();
                birthdayTextView.setText(res.getString(R.string.property_name_birthday, member.getBirthday()));
                bloodTypeTextView.setText(res.getString(R.string.property_name_blood_type, member.getBloodType()));
                constellationTextView.setText(res.getString(R.string.property_name_constellation, member.getConstellation()));
                heightTextView.setText(res.getString(R.string.property_name_height, member.getHeight()));

                tags.setTags(member.getStatus());
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BusHolder.get().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        BusHolder.get().unregister(this);
        super.onDetachedFromWindow();
    }

    @Subscribe
    public void onChangedFavoriteState(Favorite.ChangedFavoriteState state) {
        if (state.e == null) {
            if (state.action == Favorite.ChangedFavoriteState.Action.ADD) {
                favoriteIcon.setVisibility(View.VISIBLE);
            } else {
                favoriteIcon.setVisibility(View.GONE);
            }
        }
    }

}
