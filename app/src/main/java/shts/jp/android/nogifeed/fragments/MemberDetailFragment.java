package shts.jp.android.nogifeed.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.activities.MemberDetailActivity;
import shts.jp.android.nogifeed.adapters.MemberFeedListAdapter;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.Favorite;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.models.eventbus.BusHolder;
import shts.jp.android.nogifeed.utils.TrackerUtils;
import shts.jp.android.nogifeed.views.Showcase;

/**
 * // TODO: このフィードに含まれないほど昔の記事に未読があった場合、既読にすること
 */
public class MemberDetailFragment extends ListFragment {

    private static final String TAG = MemberDetailFragment.class.getSimpleName();
    private static final int IMAGE_MAX_SIZE = 10;

    private Showcase mShowcase;
    private List<String> mImageUrls = new ArrayList<>();
    private MemberFeedListAdapter mMemberFeedListAdapter;
    private String memberObjectId;

    public static MemberDetailFragment newMemberDetailFragment(String memberObjectId) {
        MemberDetailFragment memberDetailFragment = new MemberDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("memberObjectId", memberObjectId);
        memberDetailFragment.setArguments(bundle);
        return memberDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        memberObjectId = bundle.getString("memberObjectId");
        Member.getReference(memberObjectId).fetchIfNeededInBackground(new GetCallback<Member>() {
            @Override
            public void done(Member member, ParseException e) {
                if (e != null || member == null) {
                    Logger.e(TAG, "cannot get member");
                } else {
                    Entry.findById(30, 0, member.getObjectId());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(this);
    }

    @Subscribe
    public void onChangedFavorite(Favorite.ChangedFavoriteState callback) {
        mMemberFeedListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Logger.v(TAG, "firstVisibleItem : " + firstVisibleItem);
                if (firstVisibleItem >= 1) {
                    ((MemberDetailActivity) getActivity()).setActionBarDrawableAlpha(255);
                    Logger.v(TAG, "firstVisibleItem >= 1");
                } else {
                    View header = view.getChildAt(0);
                    int height = header == null ? 0 : header.getHeight();
                    Logger.v(TAG, "height : " + height);
                    if (height <= 0) {
                        ((MemberDetailActivity) getActivity()).setActionBarDrawableAlpha(0);
                    } else {
                        int alpha = Math.abs(255 * header.getTop() / height);
                        ((MemberDetailActivity) getActivity()).setActionBarDrawableAlpha(alpha);
                        Logger.v(TAG, "onScroll : " + alpha);
                    }
                }
            }
        });
    }

    @Subscribe
    public void onGotAllEntries(Entry.GotAllEntryCallback.FindById callback) {
        Logger.v(TAG, "onGotAllEntries");
        if (callback.e == null) {
            setupShowcase(callback.entries);
            setupAdapter(callback.entries);
        }
    }

    private void setupAdapter(final List<Entry> entries) {
        Logger.v(TAG, "setupAdapter");
        mMemberFeedListAdapter = new MemberFeedListAdapter(getActivity(), entries);
        setListAdapter(mMemberFeedListAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Entry entry = (Entry) getListView().getItemAtPosition(position);
                getActivity().startActivity(
                        BlogActivity.getStartIntent(getActivity(), entry.getObjectId()));
                TrackerUtils.sendTrack(getActivity(), TAG,
                        "OnClicked", "-> Blog : " + "entry(" + entry.toString() + ")");
            }
        });
    }

    private void setupShowcase(final List<Entry> entries) {
        Logger.v(TAG, "setupShowcase");
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            List<String> images = e.getUploadedThumbnailUrlList();
            mImageUrls.addAll(images);
            if (mImageUrls.size() >= IMAGE_MAX_SIZE) {
                break;
            }
        }
        Logger.v(TAG, "setupShowcase : url(" + mImageUrls.toString() + ")");
        int height = (int) (/*240*/ 300 * getActivity().getResources().getDisplayMetrics().density);
        mShowcase = new Showcase(getActivity(), mImageUrls);
        mShowcase.setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height));
        Logger.v(TAG, "favorite : id(" + memberObjectId + ") favorite("
                + Favorite.exist(memberObjectId) + ")");
        mShowcase.setFavorite(Favorite.exist(memberObjectId));
        mShowcase.setOnCheckedChangeListener(new Showcase.FavoriteChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Logger.d(TAG, "isChecked(" + isChecked + ")");
                if (isChecked) {
                    Favorite.add(memberObjectId);
                } else {
                    Favorite.removeMember(memberObjectId);
                }
            }
        });
        getListView().addHeaderView(mShowcase, null, false);
    }
}
