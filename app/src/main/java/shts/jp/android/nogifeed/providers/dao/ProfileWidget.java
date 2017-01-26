package shts.jp.android.nogifeed.providers.dao;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfileWidget implements Parcelable {

    private static final String TAG = ProfileWidget.class.getSimpleName();

    int id;
    int widgetId;
    int memberId;

    public ProfileWidget(int id, int widgetId, int memberId) {
        this.id = id;
        this.widgetId = widgetId;
        this.memberId = memberId;
    }

    public int getId() {
        return id;
    }

    public int getWidgetId() {
        return widgetId;
    }

    public int getMemberId() {
        return memberId;
    }

    protected ProfileWidget(Parcel in) {
        id = in.readInt();
        widgetId = in.readInt();
        memberId = in.readInt();
    }

    public static final Creator<ProfileWidget> CREATOR = new Creator<ProfileWidget>() {
        @Override
        public ProfileWidget createFromParcel(Parcel in) {
            return new ProfileWidget(in);
        }

        @Override
        public ProfileWidget[] newArray(int size) {
            return new ProfileWidget[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(widgetId);
        dest.writeInt(memberId);
    }
}
