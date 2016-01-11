package shts.jp.android.nogifeed.views.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import shts.jp.android.nogifeed.R;
import shts.jp.android.nogifeed.entities.News;

public class NewsTypeFilterDialog extends DialogFragment {

    private static final String TAG = NewsTypeFilterDialog.class.getSimpleName();

    /**
     * Callbacks for dialog button click
     */
    public interface Callbacks {
        public void onClickPositiveButton();
        public void onClickNegativeButton();
    }

    private Callbacks callbacks;

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity activity = getActivity();
        final String[] typeList = News.Type.getTypeList(activity);
        final boolean[] filter = News.Type.getFilter(activity);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.news_filter_dialog_title);
        builder.setMultiChoiceItems(typeList, filter,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        filter[which] = isChecked;
                    }
                });
        builder.setPositiveButton(R.string.news_filter_dialog_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        News.Type.setFilter(activity, filter);
                        if (callbacks != null) {
                            callbacks.onClickPositiveButton();
                        }
                    }
                });
        builder.setNegativeButton(R.string.news_filter_dialog_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callbacks != null) {
                            callbacks.onClickNegativeButton();
                        }
                    }
                });
        return builder.create();
    }

}
