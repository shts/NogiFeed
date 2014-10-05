package android.shts.jp.nogifeed.adapters;

import android.content.Context;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.models.Entry;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class MemberFeedListAdapter extends BindableAdapter<Entry> {

    private static final String TAG = MemberFeedListAdapter.class.getSimpleName();

    public class ViewHolder {

        TextView textView;

        public ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.title);
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
        holder.textView.setText(item.title);
    }

}
