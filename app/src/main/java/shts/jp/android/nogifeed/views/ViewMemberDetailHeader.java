package shts.jp.android.nogifeed.views;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.api.NogiFeedApiClient;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.providers.FavoriteContentObserver;
import shts.jp.android.nogifeed.providers.dao.Favorites;
import shts.jp.android.nogifeed.utils.PicassoHelper;

public class ViewMemberDetailHeader extends LinearLayout {

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

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public void setup(int memberId) {
        if (Favorites.exist(getContext(), memberId)) {
            favoriteIcon.setVisibility(View.VISIBLE);
        } else {
            favoriteIcon.setVisibility(View.GONE);
        }
        subscriptions.add(NogiFeedApiClient.getMember(memberId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Member>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Member member) {
                        if (TextUtils.isEmpty(member.getImageUrl())) {
                            profileImageView.setImageResource(R.drawable.kensyusei);
                            birthdayTextView.setVisibility(View.GONE);
                            bloodTypeTextView.setVisibility(View.GONE);
                            constellationTextView.setVisibility(View.GONE);
                            heightTextView.setVisibility(View.GONE);
                            tags.setVisibility(View.GONE);
                        } else {
                            final String profileImageUrl = member.getImageUrl();
                            PicassoHelper.loadAndCircleTransform(
                                    getContext(), profileImageView, profileImageUrl);

                            nameSubTextView.setText(member.getNameSub());

                            final Resources res = getContext().getResources();
                            birthdayTextView.setText(res.getString(R.string.property_name_birthday, member.getBirthday()));
                            bloodTypeTextView.setText(res.getString(R.string.property_name_blood_type, member.getBloodType()));
                            constellationTextView.setText(res.getString(R.string.property_name_constellation, member.getConstellation()));
                            heightTextView.setText(res.getString(R.string.property_name_height, member.getHeight()));

                            List<String> statusList = member.getStatus();
                            if (statusList != null && !statusList.isEmpty()) {
                                tags.setTags(member.getStatus());
                            }
                        }
                        nameMainTextView.setText(member.getNameMain());
                    }
                }));
    }

    private FavoriteContentObserver favoriteContentObserver = new FavoriteContentObserver() {
        @Override
        public void onChangeState(@State int state) {
            if (state == State.ADD) {
                favoriteIcon.setVisibility(View.VISIBLE);
            } else {
                favoriteIcon.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        favoriteContentObserver.register(getContext());
    }

    @Override
    protected void onDetachedFromWindow() {
        favoriteContentObserver.unregister(getContext());
        super.onDetachedFromWindow();
    }
}
