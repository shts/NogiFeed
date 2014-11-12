package android.shts.jp.nogifeed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.shts.jp.nogifeed.adapters.BindableAdapter;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class AboutFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    static class AboutListAdapter extends BindableAdapter {

        public AboutListAdapter(Context context, List list) {
            super(context, list);
        }

        @Override
        public View newView(LayoutInflater inflater, int position, ViewGroup container) {
            return null;
        }

        @Override
        public void bindView(Object item, int position, View view) {

        }
    }
}
