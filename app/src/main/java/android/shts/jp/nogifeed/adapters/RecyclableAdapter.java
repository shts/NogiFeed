package android.shts.jp.nogifeed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

public abstract class RecyclableAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<T> list;

    public RecyclableAdapter(Context context, List<T> list) {
        super();
        this.list = list;
        setup(context);
    }

    private void setup(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return onCreateViewHolder(inflater, viewGroup);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Object object = list.get(position);
        onBindViewHolder(viewHolder, object);
    }

    public abstract void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Object object);
    public abstract RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup viewGroup);

}
