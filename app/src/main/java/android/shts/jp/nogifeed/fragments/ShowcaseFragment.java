package android.shts.jp.nogifeed.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.shts.jp.nogifeed.activities.MemberDetailActivity;
import android.shts.jp.nogifeed.adapters.MemberFeedListAdapter;
import android.shts.jp.nogifeed.api.AsyncRssClient;
import android.shts.jp.nogifeed.listener.RssClientListener;
import android.shts.jp.nogifeed.models.Entries;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.utils.ArrayUtils;
import android.shts.jp.nogifeed.utils.LogUtils;
import android.shts.jp.nogifeed.utils.StringUtils;
import android.shts.jp.nogifeed.utils.UrlUtils;
import android.shts.jp.nogifeed.views.Showcase;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class ShowcaseFragment extends ListFragment {

    private static final String TAG = ShowcaseFragment.class.getSimpleName();
    private static final int IMAGE_MAX_SIZE = 10;

    private Showcase mShowcase;
    private List<String> mImageUrls = new ArrayList<String>();
    private MemberDetailActivity mActivity;
    private Entry mEntry;
    private MemberFeedListAdapter mMemberFeedListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mEntry = bundle.getParcelable(Entry.KEY);
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
                LogUtils.log(TAG, "firstVisibleItem : " + firstVisibleItem);
                if (firstVisibleItem >= 1) {
                    mActivity.setActionBarDrawableAlpha(255);
                    LogUtils.log(TAG, "firstVisibleItem >= 1");
                } else {
                    View header = view.getChildAt(0);
                    int height = header == null ? 0 : header.getHeight();
                    LogUtils.log(TAG, "height : " + height);
                    if (height <= 0) {
                        mActivity.setActionBarDrawableAlpha(0);
                    } else {
                        int alpha = Math.abs(255 * header.getTop() / height);
                        mActivity.setActionBarDrawableAlpha(alpha);
                        LogUtils.log(TAG, "onScroll : " + alpha);
                    }
                }
            }
        });
    }

    private void setupMemberFeedList(String feedUrl) {
        AsyncRssClient.read(UrlUtils.getMemberFeedUrl(feedUrl), new RssClientListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, Entries entries) {
                setupShowcase(entries);
                setupAdapter(entries);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w(TAG, "failed to get member feed. statusCode(" + statusCode + ")");
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupMemberFeedList(mEntry.link);
    }


    private void setupAdapter(Entries entries) {
        mMemberFeedListAdapter = new MemberFeedListAdapter(getActivity(), entries);
        setListAdapter(mMemberFeedListAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: ブログページへジャンプする
            }
        });
    }

    private void setupShowcase(Entries entries) {
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            List<String> images = StringUtils.getThumnailImage(
                    e.content, IMAGE_MAX_SIZE - mImageUrls.size());
            ArrayUtils.concatenation(images, mImageUrls);
            if (mImageUrls.size() >= IMAGE_MAX_SIZE) {
                break;
            }
        }

        LogUtils.log(TAG, "setupShowcase : url(" + mImageUrls.toString() + ")");

        int height = (int) ( /*240*/ 300 * getResources().getDisplayMetrics().density);
        mShowcase = new Showcase(getActivity(), mImageUrls, new Showcase.FavoriteChangeListener() {
            @Override
            public void onCheckdChanged(CompoundButton compoundButton, boolean b) {
                // TODO: add favorite function.
                // Toast.makeText(getActivity(), "favorite : " + b, Toast.LENGTH_SHORT).show();
            }
        });
        mShowcase.setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height
        ));
        ListView listView = getListView();
        listView.addHeaderView(mShowcase, null, false);
    }
}
