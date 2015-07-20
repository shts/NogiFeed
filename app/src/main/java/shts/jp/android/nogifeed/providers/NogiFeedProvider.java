package shts.jp.android.nogifeed.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class NogiFeedProvider extends ContentProvider {

	private static final int FAVORITE = 1;
    private static final int PROFILE_WIDGET = 2;
    private static final int UNREAD = 3;

	private static final UriMatcher URI_MATCHER;
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(NogiFeedContent.AUTHORITY, NogiFeedContent.TABLE_FAVORITE, FAVORITE);
        URI_MATCHER.addURI(NogiFeedContent.AUTHORITY, NogiFeedContent.TABLE_PROFILE_WIDGET, PROFILE_WIDGET);
        URI_MATCHER.addURI(NogiFeedContent.AUTHORITY, NogiFeedContent.TABLE_UNREAD, UNREAD);
	}
	private NogiFeedDatabaseHelper mDBHelper;

	@Override
	public boolean onCreate() {
		mDBHelper = new NogiFeedDatabaseHelper(getContext());
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if ((URI_MATCHER.match(uri) != FAVORITE)
                && (URI_MATCHER.match(uri) != PROFILE_WIDGET)
                && (URI_MATCHER.match(uri) != UNREAD)) {
			throw new IllegalArgumentException("Unknown URL *** " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		long rowID;
		switch (URI_MATCHER.match(uri)) {
		case FAVORITE:
			rowID = db.replace(NogiFeedContent.TABLE_FAVORITE, "NULL", values);

			if (rowID > 0) {
				Uri newUri = ContentUris.withAppendedId(NogiFeedContent.Favorite.CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(newUri, null);
				return newUri;
			}
			break;
        case PROFILE_WIDGET:
            rowID = db.replace(NogiFeedContent.TABLE_PROFILE_WIDGET, "NULL", values);

            if (rowID > 0) {
                Uri newUri = ContentUris.withAppendedId(NogiFeedContent.ProfileWidget.CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(newUri, null);
                return newUri;
            }
            break;
        case UNREAD:
            rowID = db.replace(NogiFeedContent.TABLE_UNREAD, "NULL", values);

            if (rowID > 0) {
                Uri newUri = ContentUris.withAppendedId(NogiFeedContent.UnRead.CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(newUri, null);
                return newUri;
            }
            break;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count;

		switch (URI_MATCHER.match(uri)) {
		case FAVORITE:
			if (whereClause != null || whereArgs != null) {
				count = db.delete(NogiFeedContent.TABLE_FAVORITE, whereClause, whereArgs);
			} else {
				count = db.delete(NogiFeedContent.TABLE_FAVORITE, " "
						+ NogiFeedContent.Favorite.KEY_ID + " like '%'", null);
			}
			break;
        case PROFILE_WIDGET:
            if (whereClause != null || whereArgs != null) {
                count = db.delete(NogiFeedContent.TABLE_PROFILE_WIDGET, whereClause, whereArgs);
            } else {
                count = db.delete(NogiFeedContent.TABLE_PROFILE_WIDGET, " "
                        + NogiFeedContent.ProfileWidget.KEY_ID + " like '%'", null);
            }
            break;
        case UNREAD:
            if (whereClause != null || whereArgs != null) {
                count = db.delete(NogiFeedContent.TABLE_UNREAD, whereClause, whereArgs);
            } else {
                count = db.delete(NogiFeedContent.TABLE_UNREAD, " "
                        + NogiFeedContent.UnRead.KEY_ID + " like '%'", null);
            }
            break;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {

		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count;
		switch (URI_MATCHER.match(uri)) {
		case FAVORITE:
			count = db.update(NogiFeedContent.TABLE_FAVORITE, values, where, whereArgs);
			break;
        case PROFILE_WIDGET:
            count = db.update(NogiFeedContent.TABLE_PROFILE_WIDGET, values, where, whereArgs);
            break;
        case UNREAD:
            count = db.update(NogiFeedContent.TABLE_UNREAD, values, where, whereArgs);
            break;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		Log.d("query", uri.toString());
		String orderBy;
		switch (URI_MATCHER.match(uri)) {
		case FAVORITE:
            qb.setTables(NogiFeedContent.TABLE_FAVORITE);
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = NogiFeedContent.Favorite.KEY_ID + " DESC";
			} else {
				orderBy = sortOrder;
			}
			break;
        case PROFILE_WIDGET:
            qb.setTables(NogiFeedContent.TABLE_PROFILE_WIDGET);
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = NogiFeedContent.ProfileWidget.KEY_ID + " DESC";
            } else {
                orderBy = sortOrder;
            }
            break;
        case UNREAD:
            qb.setTables(NogiFeedContent.TABLE_UNREAD);
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = NogiFeedContent.UnRead.KEY_ID + " DESC";
            } else {
                orderBy = sortOrder;
            }
            break;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}

		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = qb.query(db, projection, selection, selectionArgs,
				null, null, orderBy);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case FAVORITE:
			return NogiFeedContent.Favorite.CONTENT_TYPE;
        case PROFILE_WIDGET:
            return NogiFeedContent.ProfileWidget.CONTENT_TYPE;
        case UNREAD:
            return NogiFeedContent.UnRead.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}
}
