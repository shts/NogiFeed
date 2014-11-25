package android.shts.jp.nogifeed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.adapters.BindableAdapter;
import android.shts.jp.nogifeed.models.Member;
import android.shts.jp.nogifeed.utils.IntentUtils;
import android.shts.jp.nogifeed.utils.JsoupUtils;
import android.shts.jp.nogifeed.utils.PicassoHelper;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

// TODO: クリック時の動作を追加する。レイアウトを調整する。お気に入り機能を追加する。
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
                Member member = (Member) mMemberList.getItemAtPosition(i);
                IntentUtils.startMemberDetailActivity(getActivity(), member);
            }
        });
        setupAdapter();
        return view;
    }

    private void setupAdapter() {

        JsoupUtils.getAllMembers(new JsoupUtils.JsoupListener() {
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
            View view = inflater.inflate(R.layout.list_item_member, null);
            final ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(Object item, int position, View view) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            final Member member = (Member) item;
            Log.i(TAG, member.toString());

            holder.titleTextView.setText(member.fullName);
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
