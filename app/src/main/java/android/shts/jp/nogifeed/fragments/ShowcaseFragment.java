package android.shts.jp.nogifeed.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.activities.MemberDetailActivity;
import android.shts.jp.nogifeed.views.Showcase;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ShowcaseFragment extends ListFragment {

    private static final String TAG = ShowcaseFragment.class.getSimpleName();

    final static int[] sImages = {};

    final static String[] sTitle = {
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
            "ListItem",
    };

    private Showcase mShowcase;
    private List<String> mImageUrls;
    private MemberDetailActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageUrls = new ArrayList<String>();
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/10/04/8645274/0000.jpeg");
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/10/02/5549652/0000.jpeg");
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/09/28/0545386/0000.jpeg");
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/09/28/0545386/0003.jpeg");
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/09/28/0545386/0004.jpeg");
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/09/28/0545386/0006.jpeg");
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/09/26/1811651/0000.jpeg");
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/09/27/7125362/0000.jpeg");
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/09/27/7125362/0002.jpeg");
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/09/23/7508027/0000.jpeg");
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/09/23/9962712/0000.jpeg");
        mImageUrls.add("http://img.nogizaka46.com/blog/rina.ikoma/img/2014/09/20/0854676/0001.jpeg");

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MemberDetailActivity) activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int height = (int) (240 * getResources().getDisplayMetrics().density);
        mShowcase = new Showcase(getActivity(), mImageUrls);
        mShowcase.setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height
        ));

        ListView listView = getListView();
        listView.addHeaderView(mShowcase, null, false);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Log.d(TAG, "firstVisibleItem : " + firstVisibleItem);
                if (firstVisibleItem >= 1) {
                    mActivity.setActionBarDrawableAlpha(255);
                    Log.d(TAG, "firstVisibleItem >= 1");
                } else {
                    View header = view.getChildAt(0);
                    int height = header == null ? 0 : header.getHeight();
                    Log.i(TAG, "height : " + height);
                    if (height <= 0) {
                        mActivity.setActionBarDrawableAlpha(0);
                    } else {
                        int alpha = Math.abs(255 * header.getTop() / height);
                        mActivity.setActionBarDrawableAlpha(alpha);
                        Log.d(TAG, "onScroll : " + alpha);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mShowcase.setImageResources(sImages);

        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, sTitle));
    }

}
