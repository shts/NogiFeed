package android.shts.jp.nogifeed.adapters;

import android.content.Context;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.utils.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class MemberFeedListAdapter extends BindableAdapter<Entry> {

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

    public MemberFeedListAdapter(Context context, List<Entry> entries) {
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
    public void bindView(Entry item, int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.titleTextView.setText(item.title);
        holder.authorNameTextView.setText(item.name);
        holder.updatedTextView.setText(DateUtils.formatUpdated(item.updated));
    }

}
