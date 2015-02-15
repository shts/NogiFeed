package shts.jp.android.nogifeed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import shts.jp.android.nogifeed.R;


public class MemberFeedListAdapter extends shts.jp.android.nogifeed.adapters.BindableAdapter<shts.jp.android.nogifeed.models.Entry> {

    private static final String TAG = MemberFeedListAdapter.class.getSimpleName();

    public class ViewHolder {
        TextView titleTextView;
        TextView authorNameTextView;
        TextView updatedTextView;

        public ViewHolder(View view) {
            titleTextView = (TextView) view.findViewById(R.id.title);
            authorNameTextView = (TextView) view.findViewById(R.id.authorname);
            updatedTextView = (TextView) view.findViewById(R.id.updated);
        }
    }

    public MemberFeedListAdapter(Context context, List<shts.jp.android.nogifeed.models.Entry> entries) {
        super(context, entries);
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_member_entry, container, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(shts.jp.android.nogifeed.models.Entry item, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.titleTextView.setText(item.title);
        holder.authorNameTextView.setText(item.name);
        holder.updatedTextView.setText(shts.jp.android.nogifeed.utils.DateUtils.formatUpdated(item.updated));
    }

}
