package android.shts.jp.nogifeed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.adapters.BindableAdapter;
import android.shts.jp.nogifeed.utils.PicassoHelper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: ListView の上部はListView().addHeaderView() で追加するようにしないとスクロールができない
        /*
        * developer
        * This app summary
        * Share this app
        * Rate this app
        * App Version
        * Credit
        *
        *
        *
        *
        * */

        // avater image
        // https://avatars1.githubusercontent.com/u/7928836?v=3&s=460

    }

    private ListView mAboutList;
    private ImageView mAvaterImage;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, null);
        mAboutList = (ListView) view.findViewById(R.id.about_list);

        List<AboutItem> abouts = new ArrayList<AboutItem>();
        for (int i = 0; i < 20; i++) {
            abouts.add(new AboutItem("title : " + i, "summary : " + i));
        }

        mAboutList.setAdapter(new AboutListAdapter(getActivity(), abouts));

        mAvaterImage = (ImageView) view.findViewById(R.id.developer_icon);
        PicassoHelper.loadAndCircleTransform(getActivity(), mAvaterImage, "https://avatars1.githubusercontent.com/u/7928836?v=3&s=460");

        return view;
    }

    class AboutItem {
        String title;
        String summary;
        AboutItem(String title, String summary) {
            this.title = title;
            this.summary = summary;
        }
    }

    static class AboutListAdapter extends BindableAdapter {

        class ViewHolder {
            TextView titleTextView;
            TextView summaryTextView;
            ViewHolder(View view) {
                titleTextView = (TextView) view.findViewById(R.id.title);
                summaryTextView = (TextView) view.findViewById(R.id.summary);
            }
        }

        public AboutListAdapter(Context context, List<AboutItem> list) {
            super(context, list);
        }

        @Override
        public View newView(LayoutInflater inflater, int position, ViewGroup container) {
            View view = inflater.inflate(R.layout.list_item_about, container, false);
            final ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(Object item, int position, View view) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            AboutItem aboutItem = (AboutItem) item;
            holder.titleTextView.setText(aboutItem.title);
            holder.summaryTextView.setText(aboutItem.summary);
        }
    }
}
