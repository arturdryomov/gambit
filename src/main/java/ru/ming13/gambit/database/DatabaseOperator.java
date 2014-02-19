package ru.ming13.gambit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.ming13.gambit.util.SqlBuilder;

public class DatabaseOperator
{
	private static final String DATABASE_ALIAS = "db";
	private static final String DATABASE_PREFIX = "database";

	private final Context context;

	public static DatabaseOperator with(Context context) {
		return new DatabaseOperator(context);
	}

	private DatabaseOperator(Context context) {
		this.context = context;
	}

	public void writeDatabaseContents(OutputStream databaseContentsStream) {
		try {
			FileUtils.copyFile(buildDatabaseFile(), databaseContentsStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File buildDatabaseFile() {
		return context.getDatabasePath(DatabaseSchema.DATABASE_NAME).getAbsoluteFile();
	}

	public void readDatabaseContents(InputStream databaseContentsStream) {
		File sourceDatabaseFile = buildDatabaseFile(databaseContentsStream);

		SQLiteDatabase sourceDatabase = new DatabaseOpenHelper(context, sourceDatabaseFile.getAbsolutePath()).getReadableDatabase();
		SQLiteDatabase destinationDatabase = new DatabaseOpenHelper(context).getWritableDatabase();

		deleteDatabaseContents(destinationDatabase);
		insertDatabaseContents(destinationDatabase, sourceDatabaseFile);

		sourceDatabase.close();
		destinationDatabase.close();

		sourceDatabaseFile.delete();
	}

	private File buildDatabaseFile(InputStream databaseContentsStream) {
		try {
			File databaseFile = File.createTempFile(DATABASE_PREFIX, null, context.getCacheDir());
			FileUtils.copyInputStreamToFile(databaseContentsStream, databaseFile);
			return databaseFile;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void deleteDatabaseContents(SQLiteDatabase database) {
		try {
			database.beginTransaction();

			database.execSQL(SqlBuilder.buildDeletionClause(DatabaseSchema.Tables.CARDS));
			database.execSQL(SqlBuilder.buildDeletionClause(DatabaseSchema.Tables.DECKS));

			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	private void insertDatabaseContents(SQLiteDatabase database, File databaseFile) {
		database.execSQL(SqlBuilder.buildAttachingClause(databaseFile.getAbsolutePath(), DATABASE_ALIAS));

		try {
			database.beginTransaction();

			database.execSQL(SqlBuilder.buildInsertionClause(DatabaseSchema.Tables.DECKS, DATABASE_ALIAS));
			database.execSQL(SqlBuilder.buildInsertionClause(DatabaseSchema.Tables.CARDS, DATABASE_ALIAS));

			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}

		database.execSQL(SqlBuilder.buildDetachingClause(DATABASE_ALIAS));
	}
}
