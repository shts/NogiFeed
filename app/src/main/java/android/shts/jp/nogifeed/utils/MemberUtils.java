package android.shts.jp.nogifeed.utils;

import android.shts.jp.nogifeed.models.Details;
import android.shts.jp.nogifeed.models.Member;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 未使用
 */
public class MemberUtils {

    private static final String TAG = MemberUtils.class.getSimpleName();

    public static Member createMemberFrom(String memberFeedUrl) {
        // http://blog.nogizaka46.com/ami.noujo/2014/09/020454.php
        final String[] name = StringUtils.createFullNameFrom(memberFeedUrl);
        final String firstName = name[StringUtils.INDEX_FIRST_NAME];
        final String lastName = name[StringUtils.INDEX_LAST_NAME];

        final String profileUrl = UrlUtils.getMemberProfileUrl(firstName, lastName);
        final Details details = getDetails(profileUrl);
        final List<String> tags = getTags(profileUrl);

        return new Member(firstName, lastName, details, tags);
    }

      private static Details getDetails(String profileUrl) {

        Document document = null;
        try {
            document = Jsoup.connect(profileUrl).get();
        } catch (IOException e) {
            Log.e(TAG, "failed to connect");
        }

        if (document == null) {
            return null;
        }

        Element details = document.select("dl.clearfix").first();
        Element element = details.getAllElements().first();

        // get dd tag text
        Elements elts = element.getElementsByTag("dd");
        final String birthday = elts.get(0).text();
        final String blood = elts.get(1).text();
        final String constellation = elts.get(2).text();
        final String height = elts.get(3).text();

        return new Details(birthday, blood, constellation, height);
    }

    private static List<String> getTags(String profileUrl) {

        Document document = null;
        try {
            document = Jsoup.connect(profileUrl).get();
        } catch (IOException e) {
            Log.e(TAG, "failed to connect");
        }

        if (document == null) {
            return null;
        }

        List<String> tags = new ArrayList<String>();

        Element divStatus = document.select("div.status").first();
        Element status = divStatus.getAllElements().first();
        // get div tag text
        Elements divElements = status.getElementsByTag("div");
        boolean skipOnceTime = true;
        for (Element e : divElements) {
            if (skipOnceTime) {
                skipOnceTime = false;
                continue;
            }
            tags.add(e.text());
        }
        return tags;
    }

}
