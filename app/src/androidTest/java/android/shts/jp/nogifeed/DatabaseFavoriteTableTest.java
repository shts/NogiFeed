package android.shts.jp.nogifeed;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.shts.jp.nogifeed.providers.NogiFeedContent;
import android.test.AndroidTestCase;

public class DatabaseFavoriteTableTest extends AndroidTestCase {

    public void test_insert() throws Exception {
        final ContentResolver cr = getContext().getContentResolver();
        // ---------------------------------------------------------------
        // Create data
        // ---------------------------------------------------------------
        ContentValues cv = new ContentValues();
        cv.put(NogiFeedContent.Favorite.KEY_LINK, "http://blog.nogizaka46.com/yuuri.saito/atom.xml");
        cv.put(NogiFeedContent.Favorite.KEY_LINK, "http://blog.nogizaka46.com/miona.hori/atom.xml");
        cv.put(NogiFeedContent.Favorite.KEY_LINK, "http://blog.nogizaka46.com/asuka.saito/atom.xml");

        cr.insert(NogiFeedContent.Favorite.CONTENT_URI, cv);

        // ---------------------------------------------------------------
        // Check data
        // ---------------------------------------------------------------
        Cursor c = cr.query(NogiFeedContent.Favorite.CONTENT_URI, null, null, null, null);

        c.moveToFirst();
        String link = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.Favorite.KEY_LINK));
        assertEquals("http://blog.nogizaka46.com/yuuri.saito/atom.xml", link);

        c.moveToNext();
        link = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.Favorite.KEY_LINK));
        assertEquals("http://blog.nogizaka46.com/miona.hori/atom.xml", link);

        c.moveToNext();
        link = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.Favorite.KEY_LINK));
        assertEquals("http://blog.nogizaka46.com/asuka.saito/atom.xml", link);

        c.close();
    }

    public void test_delete() throws Exception {
        final ContentResolver cr = getContext().getContentResolver();
        // ---------------------------------------------------------------
        // Initialize
        // ---------------------------------------------------------------
        cr.delete(NogiFeedContent.Favorite.CONTENT_URI, null, null);

        ContentValues cv = new ContentValues();
        cv.put(NogiFeedContent.Favorite.KEY_LINK, "http://blog.nogizaka46.com/yuuri.saito/atom.xml");
        cv.put(NogiFeedContent.Favorite.KEY_LINK, "http://blog.nogizaka46.com/miona.hori/atom.xml");
        cv.put(NogiFeedContent.Favorite.KEY_LINK, "http://blog.nogizaka46.com/asuka.saito/atom.xml");

        cr.insert(NogiFeedContent.Favorite.CONTENT_URI, cv);

        // ---------------------------------------------------------------
        // Create data
        // ---------------------------------------------------------------
        String whare = NogiFeedContent.Favorite.KEY_LINK + "=?";
        String[] selectionArgs =  {
                "http://blog.nogizaka46.com/miona.hori/atom.xml",
        };

        cr.delete(NogiFeedContent.Favorite.CONTENT_URI, whare, selectionArgs);

        // ---------------------------------------------------------------
        // Check data
        // ---------------------------------------------------------------
        Cursor c = cr.query(NogiFeedContent.Favorite.CONTENT_URI, null, null, null, null);

        c.moveToFirst();
        String link = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.Favorite.KEY_LINK));
        assertEquals("http://blog.nogizaka46.com/yuuri.saito/atom.xml", link);

        c.moveToNext();
        link = c.getString(c.getColumnIndexOrThrow(NogiFeedContent.Favorite.KEY_LINK));
        assertEquals("http://blog.nogizaka46.com/asuka.saito/atom.xml", link);

        assertEquals(false, c.moveToNext());
    }
}
