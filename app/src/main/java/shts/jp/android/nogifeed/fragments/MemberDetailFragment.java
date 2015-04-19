package shts.jp.android.nogifeed.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import shts.jp.android.nogifeed.activities.MemberDetailActivity;
import shts.jp.android.nogifeed.adapters.MemberFeedListAdapter;
import shts.jp.android.nogifeed.api.AsyncRssClient;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.RssClientFinishListener;
import shts.jp.android.nogifeed.models.Entries;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.views.Showcase;

public class MemberDetailFragment extends ListFragment {

    private static final String TAG = MemberDetailFragment.class.getSimpleName();
    private static final int IMAGE_MAX_SIZE = 10;

    private Showcase mShowcase;
    private List<String> mImageUrls = new ArrayList<String>();
    private MemberDetailActivity mActivity;
    private Entry mEntry;
    private MemberFeedListAdapter mMemberFeedListAdapter;
    private String mFeedUrl;
    private Member mMember;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mEntry = bundle.getParcelable(Entry.KEY);
        mMember = bundle.getParcelable(Member.KEY);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MemberDetailActivity) activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = getListView();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                shts.jp.android.nogifeed.common.Logger.v(TAG, "firstVisibleItem : " + firstVisibleItem);
                if (firstVisibleItem >= 1) {
                    mActivity.setActionBarDrawableAlpha(255);
                    shts.jp.android.nogifeed.common.Logger.v(TAG, "firstVisibleItem >= 1");
                } else {
                    View header = view.getChildAt(0);
                    int height = header == null ? 0 : header.getHeight();
                    shts.jp.android.nogifeed.common.Logger.v(TAG, "height : " + height);
                    if (height <= 0) {
                        mActivity.setActionBarDrawableAlpha(0);
                    } else {
                        int alpha = Math.abs(255 * header.getTop() / height);
                        mActivity.setActionBarDrawableAlpha(alpha);
                        shts.jp.android.nogifeed.common.Logger.v(TAG, "onScroll : " + alpha);
                    }
                }
            }
        });
    }

    private void setupMemberFeedList(String feedUrl) {

        AsyncRssClient.read(mActivity.getApplicationContext(), feedUrl, new RssClientFinishListener() {
            @Override
            public void onSuccessWrapper(int statusCode, Header[] headers, Entries entries) {
            }

            @Override
            public void onFailureWrapper(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Logger.w(TAG, "failed to get member feed. statusCode(" + statusCode + ")");
            }

            @Override
            public void onFinish(Entries entries) {
                if (entries == null || entries.isEmpty()) {
                    Logger.w(TAG, "failed to get member feed");

                } else {
                    setupShowcase(entries);
                    setupAdapter(entries);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mEntry != null) {
            mFeedUrl = shts.jp.android.nogifeed.utils.UrlUtils.getMemberFeedUrl(mEntry.link);
            setupMemberFeedList(mFeedUrl);
        }
        if (mMember != null) {
            mFeedUrl = mMember.feedUrl;
            setupMemberFeedList(mFeedUrl);
        }
    }


    private void setupAdapter(shts.jp.android.nogifeed.models.Entries entries) {
        mMemberFeedListAdapter = new shts.jp.android.nogifeed.adapters.MemberFeedListAdapter(getActivity(), entries);
        setListAdapter(mMemberFeedListAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                shts.jp.android.nogifeed.models.Entry entry = (shts.jp.android.nogifeed.models.Entry) getListView().getItemAtPosition(position);
                shts.jp.android.nogifeed.utils.IntentUtils.startBlogActivity(mActivity, entry);
                shts.jp.android.nogifeed.utils.TrackerUtils.sendTrack(getActivity(), TAG,
                        "OnClicked", "-> Blog : " + "entry(" + entry.toString() + ")");
            }
        });
    }

    private void setupShowcase(shts.jp.android.nogifeed.models.Entries entries) {
        for (int i = 0; i < entries.size(); i++) {
            shts.jp.android.nogifeed.models.Entry e = entries.get(i);
            List<String> images = shts.jp.android.nogifeed.utils.JsoupUtils.getThumbnailImageUrls(
                    e.content, IMAGE_MAX_SIZE - mImageUrls.size());
            shts.jp.android.nogifeed.utils.ArrayUtils.concatenation(images, mImageUrls);
            if (mImageUrls.size() >= IMAGE_MAX_SIZE) {
                break;
            }
        }

        shts.jp.android.nogifeed.common.Logger.v(TAG, "setupShowcase : url(" + mImageUrls.toString() + ")");

        int height = (int) ( /*240*/ 300 * mActivity.getResources().getDisplayMetrics().density);
        mShowcase = new shts.jp.android.nogifeed.views.Showcase(getActivity(), mImageUrls, new shts.jp.android.nogifeed.views.Showcase.FavoriteChangeListener() {
            @Override
            public void onCheckdChanged(CompoundButton compoundButton, boolean isChecked) {
                shts.jp.android.nogifeed.utils.DataStoreUtils.favorite(getActivity(), mFeedUrl, isChecked);
            }
        });
        mShowcase.setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height
        ));
        mShowcase.setFavorite(shts.jp.android.nogifeed.utils.DataStoreUtils.alreadyExist(getActivity(), mFeedUrl));
        ListView listView = getListView();
        listView.addHeaderView(mShowcase, null, false);
    }
}
