package android.shts.jp.nogifeed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.activities.BaseActivity;
import android.shts.jp.nogifeed.adapters.BindableAdapter;
import android.shts.jp.nogifeed.utils.IntentUtils;
import android.shts.jp.nogifeed.utils.PicassoHelper;
import android.shts.jp.nogifeed.utils.TrackerUtils;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends Fragment {

    private static final String TAG = AboutFragment.class.getSimpleName();
    private static final String URL_ICON = "https://avatars1.githubusercontent.com/u/7928836?v=3&s=460";
    private ListView mAboutList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupAboutListAdapter() {
        List<AboutItem> abouts = new ArrayList<AboutItem>();
        abouts.add(new AboutItem(getResources().getString(R.string.about_item_share),
                R.drawable.ic_social_share, new OnClickListener() {
            @Override
            public void onClick() {
                IntentUtils.recomendApp(getActivity());
            }
        }));
        abouts.add(new AboutItem(getResources().getString(R.string.about_item_rate),
                R.drawable.ic_action_thumb_up, new OnClickListener() {
            @Override
            public void onClick() {
                IntentUtils.rateApp(getActivity());
            }
        }));
        abouts.add(new AboutItem(getResources().getString(R.string.about_item_mention),
                R.drawable.ic_communication_messenger, new OnClickListener() {
            @Override
            public void onClick() {
                IntentUtils.inquiryApp(getActivity());
            }
        }));
        mAboutList.setAdapter(new AboutListAdapter(getActivity(), abouts));
    }

    private void setupListHeader(LayoutInflater inflater, ListView listView) {
        View view = inflater.inflate(R.layout.about_header, null);

        ImageView developerIcon = (ImageView) view.findViewById(R.id.developer_icon);
        developerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.showDeveloper(getActivity());
                TrackerUtils.sendTrack(getActivity(), TAG, "OnClicked", "developerIcon");
            }
        });
        PicassoHelper.loadAndCircleTransform(getActivity(), developerIcon, URL_ICON);

        listView.addHeaderView(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, null);
        mAboutList = (ListView) view.findViewById(R.id.about_list);
        mAboutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView listView = (ListView) adapterView;
                AboutItem aboutItem = (AboutItem) listView.getItemAtPosition(i);

                if (aboutItem != null && aboutItem.listener != null) {
                    aboutItem.listener.onClick();
                    TrackerUtils.sendTrack(getActivity(), TAG, "OnClicked", aboutItem.title);
                }
            }
        });
        setupListHeader(inflater, mAboutList);
        setupAboutListAdapter();
        return view;
    }

    class AboutItem {
        public final String title;
        public final int iconRes;
        public final OnClickListener listener;
        AboutItem(String title, int iconRes, OnClickListener listener) {
            this.title = title;
            this.iconRes = iconRes;
            this.listener = listener;
        }
    }
    public interface OnClickListener {
        public void onClick();
    }

    static class AboutListAdapter extends BindableAdapter {

        class ViewHolder {
            TextView textView;
            ImageView imageView;
            ViewHolder(View view) {
                textView = (TextView) view.findViewById(R.id.title);
                imageView = (ImageView) view.findViewById(R.id.icon);
            }
        }

        public AboutListAdapter(Context context, List<AboutItem> list) {
            super(context, list);
        }

        @Override
        public View newView(LayoutInflater inflater, int position, ViewGroup container) {
            View view = inflater.inflate(R.layout.list_item_about_action, null);
            final ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(Object item, int position, View view) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            AboutItem aboutLocalItem = (AboutItem) item;
            holder.imageView.setImageResource(aboutLocalItem.iconRes);
            holder.textView.setText(aboutLocalItem.title);
        }
    }
}
