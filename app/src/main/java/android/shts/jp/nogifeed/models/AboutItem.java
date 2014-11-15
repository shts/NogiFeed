package android.shts.jp.nogifeed.models;

import android.shts.jp.nogifeed.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class AboutItem {

    public interface OnAboutItemClickListener {
        public void onClick(View view);
    }

    public abstract View newView(LayoutInflater inflater, int position, ViewGroup container);
    public abstract void bindView(Object item, int position, View view);

    public static class TextWithIcon extends AboutItem {
        public final String title;
        public final int iconRes;
        public final OnAboutItemClickListener listener;
        public TextWithIcon(String title, int iconRes, OnAboutItemClickListener listener) {
            this.title = title;
            this.iconRes = iconRes;
            this.listener = listener;
        }
        private class ViewHolder {
            ImageView imageView;
            TextView textView;
            ViewHolder(View view) {
                imageView = (ImageView) view.findViewById(R.id.icon);
                textView = (TextView) view.findViewById(R.id.title);
            }
        }
        @Override
        public View newView(LayoutInflater inflater, int position, ViewGroup container) {
            View view = inflater.inflate(R.layout.list_item_about_action, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClick(view);
                    }
                }
            });
            final ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
            return null;
        }

        @Override
        public void bindView(Object item, int position, View view) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            TextWithIcon textWithIcon = (TextWithIcon) item;
            holder.textView.setText(textWithIcon.title);
            holder.imageView.setImageResource(textWithIcon.iconRes);
        }
    }

    public static class TwoTextLine extends AboutItem {
        public final String title;
        public final String summary;
        public final OnAboutItemClickListener listener;
        public TwoTextLine(String title, String summary, OnAboutItemClickListener listener) {
            this.title = title;
            this.summary = summary;
            this.listener = listener;
        }
        private class ViewHolder {
            TextView textTitleView;
            TextView textSummaryView;
            ViewHolder(View view) {
                textTitleView = (TextView) view.findViewById(R.id.title);
                textSummaryView = (TextView) view.findViewById(R.id.summary);
            }
        }
        @Override
        public View newView(LayoutInflater inflater, int position, ViewGroup container) {
            View view = inflater.inflate(R.layout.list_item_about, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClick(view);
                    }
                }
            });
            final ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
            return view;
        }
        @Override
        public void bindView(Object item, int position, View view) {
            final ViewHolder holder = (ViewHolder) view.getTag();
            TwoTextLine twoTextLine = (TwoTextLine) item;
            holder.textTitleView.setText(twoTextLine.title);
            holder.textSummaryView.setText(twoTextLine.summary);
        }
    }
}
