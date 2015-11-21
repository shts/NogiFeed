package shts.jp.android.nogifeed.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

@ParseClassName("Member")
public class Member extends ParseObject {

    private static final String TAG = Member.class.getSimpleName();

    public static final String KEY = Member.class.getSimpleName();

    public Member() {}

    public static Member getReference(String objectId) {
        return ParseObject.createWithoutData(Member.class, objectId);
    }

    public static ParseQuery<Member> getQuery() {
        ParseQuery<Member> query = ParseQuery.getQuery(Member.class);
        return query;
    }

    public String getNameMain() {
        return getString("name_main");
    }

    public String getNameSub() {
        return getString("name_sub");
    }

    public String getBlogUrl() {
        return getString("blog_url");
    }

    public String getBloodType() {
        return getString("blood_type");
    }

    public String getConstellation() {
        return getString("constellation");
    }

    public String getHeight() {
        return getString("height");
    }

    public String getProfileImageUrl() {
        return getString("image_url");
    }

    public String getRssUrl() {
        return getString("rss_url");
    }

    public List<String> getStatus() {
        return getList("status");
    }

    public String getBirthday() {
        return getString("birthday");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name_main(").append(getNameMain()).append(") ");
        sb.append("name_sub(").append(getNameSub()).append(") ");
        sb.append("blog_url(").append(getBlogUrl()).append(") ");
        sb.append("blood_type(").append(getBloodType()).append(") ");
        sb.append("constellation(").append(getConstellation()).append(") ");
        sb.append("height(").append(getHeight()).append(") ");
        sb.append("image_url(").append(getProfileImageUrl()).append(") ");
        sb.append("rss_url(").append(getRssUrl()).append(") ");
        sb.append("status(").append(getStatus()).append(") ");
        sb.append("birthday(").append(getBirthday()).append(") ");
        return sb.toString();
    }
}
