package android.shts.jp.nogifeed.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by saitoushouta on 2014/09/23.
 */
public class Details implements Parcelable {

    public static final String KEY = Details.class.getSimpleName();
    private static final String TAG = Details.class.getSimpleName();

    public String birthday;
    public String blood;
    public String constellation;
    public String height;

    public Details(String birthday, String blood,
                   String constellation, String height) {
        this.birthday = birthday;
        this.blood = blood;
        this.constellation = constellation;
        this.height = height;
    }

    private Details(Parcel parcel) {
        birthday = parcel.readString();
        blood = parcel.readString();
        constellation = parcel.readString();
        height = parcel.readString();
    }

    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(birthday);
        parcel.writeString(blood);
        parcel.writeString(constellation);
        parcel.writeString(height);
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<Details> CREATOR = new Creator<Details>() {
        @Override
        public Details createFromParcel(Parcel parcel) {
            return new Details(parcel);
        }

        @Override
        public Details[] newArray(int size) {
            return new Details[size];
        }

    };

}

