package android.shts.jp.nogifeed.adapters;

import android.content.Context;
import android.shts.jp.nogifeed.R;
import android.shts.jp.nogifeed.models.Entry;
import android.shts.jp.nogifeed.utils.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by saitoushouta on 2014/08/31.
 */
public class AllFeedListAdapter extends ArrayAdapter<Entry> {


    public class ViewHolder {
        TextView titleTextView;
        TextView authornameTextView;
        TextView updatedTextView;

        public ViewHolder(View view) {
            titleTextView = (TextView) view.findViewById(R.id.title);
            authornameTextView = (TextView) view.findViewById(R.id.authorname);
            updatedTextView = (TextView) view.findViewById(R.id.updated);
        }
    }

    public AllFeedListAdapter(Context context, List<Entry> entries) {
        super(context, -1, entries);
        setup();
    }

    private void setup() {
        mInflater = LayoutInflater.from(getContext());
    }

    private LayoutInflater mInflater;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = newView(mInflater, position, parent);
        bindView(getItem(position), position, convertView);
        return convertView;
    }

    public View newView(LayoutInflater inflater, final int position, ViewGroup container) {
        View view = inflater.inflate(R.layout.list_item_entry, container, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    public void bindView(final Entry item, final int position, View view) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        holder.titleTextView.setText(item.title);
        holder.authornameTextView.setText(item.name);
        holder.updatedTextView.setText(DateUtils.formatUpdated(item.updated));
    }
}
