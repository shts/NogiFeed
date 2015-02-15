package shts.jp.android.nogifeed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.adapters.BindableAdapter;
import shts.jp.android.nogifeed.common.Logger;
import shts.jp.android.nogifeed.models.Member;
import shts.jp.android.nogifeed.utils.JsoupUtils;
import shts.jp.android.nogifeed.utils.PicassoHelper;

// TODO: クリック時の動作を追加する。レイアウトを調整する。お気に入り機能を追加する。
// TODO: 中央揃えにする！ 2014/12/09

/**
 * For add favorite member.
 */
public class AllMemberGridListFragment extends Fragment {

    private static final String TAG = AllMemberGridListFragment.class.getSimpleName();
    private GridView mMemberList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_member_list, null);
        mMemberList = (GridView) view.findViewById(R.id.gridview);
        mMemberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                shts.jp.android.nogifeed.models.Member member = (shts.jp.android.nogifeed.models.Member) mMemberList.getItemAtPosition(i);
                shts.jp.android.nogifeed.utils.IntentUtils.startMemberDetailActivity(getActivity(), member);
            }
        });
        setupAdapter();
        return view;
    }

    private void setupAdapter() {

        boolean ret = shts.jp.android.nogifeed.utils.JsoupUtils.getAllMembers(getActivity(), new JsoupUtils.GetMemberListener() {
            @Override
            public void onSuccess(List<Member> memberList) {
                mMemberList.setAdapter(new GridAdapter(getActivity(), memberList));
            }

            @Override
            public void onFailed() {
                // Show error toast
                Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                        Toast.LENGTH_SHORT).show();
            }
        });

        if (!ret) {
            // Show error toast
            Toast.makeText(getActivity(), getResources().getString(R.string.feed_failure),
                    Toast.LENGTH_SHORT).show();
        }

    }

    class GridAdapter extends BindableAdapter {

        private final Context mContext;
        private final List<Member> mMembers;

        GridAdapter(Context context, List<Member> members) {
            super(context, members);
            mContext = context;
            mMembers = members;
        }

        class ViewHolder {
            public TextView titleTextView;
            public ImageView profileImageView;
            public ImageView favoriteImageView;
            ViewHolder (View view) {
                titleTextView = (TextView) view.findViewById(R.id.member_name);
                profileImageView = (ImageView) view.findViewById(R.id.profile_image);
                favoriteImageView = (ImageView) view.findViewById(R.id.favorite_icon);
            }
        }

        @Override
        public View newView(LayoutInflater inflater, int position, ViewGroup container) {
            View view = inflater.inflate(R.layout.list_item_card, null);
            final ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(Object item, int position, View view) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            final Member member = (Member) item;
            Logger.i(TAG, member.toString());

            holder.titleTextView.setText(member.name);
            holder.favoriteImageView.setVisibility(
                    member.isFavorite(mContext) ? View.VISIBLE : View.GONE/*View.VISIBLE*/);

            if (TextUtils.isEmpty(member.profileImageUrl)) {
                holder.profileImageView.setImageResource(R.drawable.kensyusei);
            } else {
                PicassoHelper.loadAndCircleTransform(
                        mContext, holder.profileImageView, member.profileImageUrl);
            }
        }
    }
}
