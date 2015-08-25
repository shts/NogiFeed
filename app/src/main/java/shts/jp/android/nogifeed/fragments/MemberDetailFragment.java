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

import shts.jp.android.nogifeed.activities.BlogActivity;
import shts.jp.android.nogifeed.activities.MemberDetailActivity;
import shts.jp.android.nogifeed.adapters.MemberFeedListAdapter;
import shts.jp.android.nogifeed.api.AsyncRssClient;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.listener.RssClientFinishListener;
import shts.jp.android.nogifeed.models.BlogEntry;
import shts.jp.android.nogifeed.models.Entries;
import shts.jp.android.nogifeed.models.Entry;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.utils.ArrayUtils;
import shts.jp.android.nogifeed.utils.DataStoreUtils;
import shts.jp.android.nogifeed.utils.IntentUtils;
import shts.jp.android.nogifeed.utils.JsoupUtils;
import shts.jp.android.nogifeed.utils.TrackerUtils;
import shts.jp.android.nogifeed.utils.UrlUtils;
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
    private BlogEntry mBlogEntry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mEntry = bundle.getParcelable(Entry.KEY);
        mMember = bundle.getParcelable(Member.KEY);
        mBlogEntry = bundle.getParcelable(BlogEntry.KEY);
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
                Logger.v(TAG, "firstVisibleItem : " + firstVisibleItem);
                if (firstVisibleItem >= 1) {
                    mActivity.setActionBarDrawableAlpha(255);
                    Logger.v(TAG, "firstVisibleItem >= 1");
                } else {
                    View header = view.getChildAt(0);
                    int height = header == null ? 0 : header.getHeight();
                    Logger.v(TAG, "height : " + height);
                    if (height <= 0) {
                        mActivity.setActionBarDrawableAlpha(0);
                    } else {
                        int alpha = Math.abs(255 * header.getTop() / height);
                        mActivity.setActionBarDrawableAlpha(alpha);
                        Logger.v(TAG, "onScroll : " + alpha);
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
            mFeedUrl = UrlUtils.getMemberFeedUrl(mEntry.link);
        }
        if (mMember != null) {
            mFeedUrl = mMember.feedUrl;
        }
        if (mBlogEntry != null) {
            mFeedUrl = UrlUtils.getMemberFeedUrl(mBlogEntry.url);
        }
        setupMemberFeedList(mFeedUrl);
    }


    private void setupAdapter(Entries entries) {
        mMemberFeedListAdapter = new MemberFeedListAdapter(getActivity(), entries);
        setListAdapter(mMemberFeedListAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Entry entry = (Entry) getListView().getItemAtPosition(position);
                //IntentUtils.startBlogActivity(mActivity, entry);
                mActivity.startActivity(BlogActivity.getStartIntent(mActivity, mBlogEntry));
                TrackerUtils.sendTrack(getActivity(), TAG,
                        "OnClicked", "-> Blog : " + "entry(" + entry.toString() + ")");
            }
        });
    }

    private void setupShowcase(Entries entries) {
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            List<String> images = JsoupUtils.getThumbnailImageUrls(
                    e.content, IMAGE_MAX_SIZE - mImageUrls.size());
            ArrayUtils.concatenation(images, mImageUrls);
            if (mImageUrls.size() >= IMAGE_MAX_SIZE) {
                break;
            }
        }

        Logger.v(TAG, "setupShowcase : url(" + mImageUrls.toString() + ")");

        int height = (int) ( /*240*/ 300 * mActivity.getResources().getDisplayMetrics().density);
        mShowcase = new Showcase(getActivity(), mImageUrls, new shts.jp.android.nogifeed.views.Showcase.FavoriteChangeListener() {
            @Override
            public void onCheckdChanged(CompoundButton compoundButton, boolean isChecked) {
                DataStoreUtils.favorite(getActivity(), mFeedUrl, isChecked);
            }
        });
        mShowcase.setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height));
        mShowcase.setFavorite(DataStoreUtils.alreadyExist(getActivity(), mFeedUrl));
        ListView listView = getListView();
        listView.addHeaderView(mShowcase, null, false);
    }
}
