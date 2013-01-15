package ru.ming13.gambit.local.provider;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import ru.ming13.gambit.local.sqlite.DbOpenHelper;
import ru.ming13.gambit.local.sqlite.DbTableNames;


public class DecksProvider extends ContentProvider
{
	private SQLiteOpenHelper databaseHelper;

	@Override
	public boolean onCreate() {
		databaseHelper = new DbOpenHelper(getContext());

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArguments, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		queryBuilder.setTables(DbTableNames.DECKS);

		switch (Uris.MATCHER.match(uri)) {
			case Uris.Codes.DECKS:
				break;

			default:
				throw new IllegalArgumentException("Unsupported URI.");
		}

		SQLiteDatabase database = databaseHelper.getReadableDatabase();

		return queryBuilder.query(database, projection, selection, selectionArguments, null, null,
			sortOrder);
	}

	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Uri uri, String s, String[] strings) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
		throw new UnsupportedOperationException();
	}
}
