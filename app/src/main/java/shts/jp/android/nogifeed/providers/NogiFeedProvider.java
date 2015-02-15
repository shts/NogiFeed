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

	private static final UriMatcher URI_MATCHER;
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(shts.jp.android.nogifeed.providers.NogiFeedContent.AUTHORITY, "favorite", FAVORITE);
	}
	private shts.jp.android.nogifeed.providers.NogiFeedDatabaseHelper mDBHelper;

	@Override
	public boolean onCreate() {
		mDBHelper = new shts.jp.android.nogifeed.providers.NogiFeedDatabaseHelper(getContext());
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (URI_MATCHER.match(uri) != FAVORITE) {
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
			rowID = db.replace(shts.jp.android.nogifeed.providers.NogiFeedContent.TABLE_FAVORITE, "NULL", values);

			if (rowID > 0) {
				Uri newUri = ContentUris.withAppendedId(
						shts.jp.android.nogifeed.providers.NogiFeedContent.Favorite.CONTENT_URI, rowID);
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
				count = db.delete(shts.jp.android.nogifeed.providers.NogiFeedContent.TABLE_FAVORITE, whereClause,
						whereArgs);
			} else {
				count = db.delete(shts.jp.android.nogifeed.providers.NogiFeedContent.TABLE_FAVORITE, " "
						+ shts.jp.android.nogifeed.providers.NogiFeedContent.Favorite.KEY_ID + " like '%'", null);
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
			count = db.update(shts.jp.android.nogifeed.providers.NogiFeedContent.TABLE_FAVORITE, values, where,
					whereArgs);
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
		qb.setTables(shts.jp.android.nogifeed.providers.NogiFeedContent.TABLE_FAVORITE);

		Log.d("query", uri.toString());
		String orderBy;
		switch (URI_MATCHER.match(uri)) {
		case FAVORITE:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = shts.jp.android.nogifeed.providers.NogiFeedContent.Favorite.KEY_ID + " DESC";
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
			return shts.jp.android.nogifeed.providers.NogiFeedContent.Favorite.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}
}
