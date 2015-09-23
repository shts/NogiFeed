package android.shts.jp.nogifeed;

import junit.framework.TestCase;

import shts.jp.android.nogifeed.entities.NogizakaUrl;

public class NogizakaUrlTest extends TestCase {

    public void testArticle() throws Exception {
        // not have 'u'
        String url = "http://blog.nogizaka46.com/kana.nakada/2014/12/021877.php";
        NogizakaUrl.Article article = new NogizakaUrl.Article(url);
        assertEquals(url, article.text());
        assertEquals("http://img.nogizaka46.com/www/smph/member/img/nakadakana_prof.jpg", article.toProfileImage().text());

        // have 'u'
        url = "http://blog.nogizaka46.com/karin.itou/smph/2015/09/024939.php";
        article = new NogizakaUrl.Article(url);
        assertEquals(url, article.text());
        assertEquals("http://img.nogizaka46.com/www/smph/member/img/itoukarin_prof.jpg", article.toProfileImage().text());
        url = "http://blog.nogizaka46.com/marika.ito/smph/2015/09/024949.php";
        article = new NogizakaUrl.Article(url);
        assertEquals(url, article.text());
        assertEquals("http://img.nogizaka46.com/www/smph/member/img/itoumarika_prof.jpg", article.toProfileImage().text());

        // only first name
        url = "http://blog.nogizaka46.com/staff/smph/2015/09/024935.php";
        article = new NogizakaUrl.Article(url);
        assertEquals(url, article.text());
        assertEquals("", article.toProfileImage().text());
    }
    public void testAllrticle() throws Exception {
        // not have 'u'
        String url = "http://blog.nogizaka46.com/manatsu.akimoto/smph/";
        NogizakaUrl.AllArticles allArticles = new NogizakaUrl.AllArticles(url);
        assertEquals(url, allArticles.text());
        assertEquals("http://img.nogizaka46.com/www/smph/member/img/akimotomanatsu_prof.jpg", allArticles.toProfileImage().text());

        // have 'u'
        url = "http://blog.nogizaka46.com/misa.eto/smph/";
        allArticles = new NogizakaUrl.AllArticles(url);
        assertEquals(url, allArticles.text());
        assertEquals("http://img.nogizaka46.com/www/smph/member/img/etoumisa_prof.jpg", allArticles.toProfileImage().text());

        // only first name
        url = "http://blog.nogizaka46.com/staff/smph/";
        allArticles = new NogizakaUrl.AllArticles(url);
        assertEquals(url, allArticles.text());
        assertEquals("", allArticles.toProfileImage().text());
    }
    public void testFeed() throws Exception {
        // not have 'u'
        String url = "http://blog.nogizaka46.com/kana.nakada/atom.xml";
        NogizakaUrl.Feed feed = new NogizakaUrl.Feed(url);
        assertEquals(url, feed.text());
        assertEquals("http://img.nogizaka46.com/www/smph/member/img/nakadakana_prof.jpg", feed.toProfileImage().text());
        // have 'u'
        url = "http://blog.nogizaka46.com/marika.ito/atom.xml";
        feed = new NogizakaUrl.Feed(url);
        assertEquals(url, feed.text());
        assertEquals("http://img.nogizaka46.com/www/smph/member/img/itoumarika_prof.jpg", feed.toProfileImage().text());

        url = "http://blog.nogizaka46.com/staff/atom.xml";
        feed = new NogizakaUrl.Feed(url);
        assertEquals(url, feed.text());
        assertEquals("", feed.toProfileImage().text());

        url = "http://blog.nogizaka46.com/kana.nakada/2014/12/021877.php";
        NogizakaUrl.Article article = new NogizakaUrl.Article(url);
        feed = NogizakaUrl.Feed.from(article);
        assertEquals("http://blog.nogizaka46.com/kana.nakada/atom.xml", feed.text());
        assertEquals("http://img.nogizaka46.com/www/smph/member/img/nakadakana_prof.jpg", feed.toProfileImage().text());

        url = "http://blog.nogizaka46.com/manatsu.akimoto/smph/";
        NogizakaUrl.AllArticles allArticles = new NogizakaUrl.AllArticles(url);
        feed = NogizakaUrl.Feed.from(allArticles);
        assertEquals("http://blog.nogizaka46.com/manatsu.akimoto/atom.xml", feed.text());
        assertEquals("http://img.nogizaka46.com/www/smph/member/img/akimotomanatsu_prof.jpg", feed.toProfileImage().text());

    }
}
