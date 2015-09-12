package shts.jp.android.nogifeed.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Banner implements Parcelable {

    private static final String TAG = Banner.class.getSimpleName();
    public static final String KEY = Banner.class.getSimpleName();

    public Banner() {}

    /**
     * banner image thumbnail url.
     * ex) http://img.nogizaka46.com/www/imgslide/banner/banner96.png
     */
    public String thumurl;

    /**
     * banner text.
     * ex) <![CDATA[ 乃木坂46主演「初森ベマーズ」 ]]>
     */
    public String alttext;

    /**
     * banner big image url.
     * ex) http://img.nogizaka46.com/www/imgslide/banner/banner100.jpg
     */
    public String bnrurl;

    /**
     * banner link.
     * ex) http://www.tv-tokyo.co.jp/bemars/
     */
    public String linkurl;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("thumurl(").append(thumurl).append(")").append("\n");
        sb.append("alttext(").append(alttext).append(")").append("\n");
        sb.append("bnrurl(").append(bnrurl).append(")").append("\n");
        sb.append("linkurl(").append(linkurl).append(")").append("\n");
        return sb.toString();
    }

    protected Banner(Parcel in) {
        thumurl = in.readString();
        alttext = in.readString();
        bnrurl = in.readString();
        linkurl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumurl);
        dest.writeString(alttext);
        dest.writeString(bnrurl);
        dest.writeString(linkurl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Banner> CREATOR = new Parcelable.Creator<Banner>() {
        @Override
        public Banner createFromParcel(Parcel in) {
            return new Banner(in);
        }

        @Override
        public Banner[] newArray(int size) {
            return new Banner[size];
        }
    };
}
