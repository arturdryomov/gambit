/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.ming13.gambit.local.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ru.ming13.gambit.local.DbException;


public class DbOpenHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "gambit.db";

	public DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DbVersions.CURRENT);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();

		try {
			createTables(db);

			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}

	private void createTables(SQLiteDatabase db) {
		db.execSQL(buildDecksTableCreationQuery());
		db.execSQL(buildCardsTableCreationQuery());
	}

	private String buildDecksTableCreationQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("create table %s ", DbTableNames.DECKS));

		queryBuilder.append("(");
		queryBuilder.append(String.format("%s %s, ", DbFieldNames.ID, DbFieldParams.ID));
		queryBuilder.append(
			String.format("%s %s, ", DbFieldNames.DECK_TITLE, DbFieldParams.DECK_TITLE));
		queryBuilder.append(
			String.format("%s %s", DbFieldNames.DECK_CURRENT_CARD_INDEX, DbFieldParams.INDEX));
		queryBuilder.append(")");

		return queryBuilder.toString();
	}

	private String buildCardsTableCreationQuery() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(String.format("create table %s ", DbTableNames.CARDS));

		queryBuilder.append("(");

		queryBuilder.append(String.format("%s %s, ", DbFieldNames.ID, DbFieldParams.ID));
		queryBuilder.append(
			String.format("%s %s, ", DbFieldNames.CARD_DECK_ID, DbFieldParams.DECK_FOREIGN_ID));
		queryBuilder.append(
			String.format("%s %s, ", DbFieldNames.CARD_FRONT_SIDE_TEXT, DbFieldParams.CARD_TEXT));
		queryBuilder.append(
			String.format("%s %s, ", DbFieldNames.CARD_BACK_SIDE_TEXT, DbFieldParams.CARD_TEXT));
		queryBuilder.append(String.format("%s %s", DbFieldNames.CARD_ORDER_INDEX, DbFieldParams.INDEX));

		queryBuilder.append(")");

		return queryBuilder.toString();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldDatabaseVersion, int newDatabaseVersion) {
		switch (oldDatabaseVersion) {
			case DbVersions.LATEST_WITH_CAMEL_NAMING_STYLE:
				migrateFromCamelNamingStyle(db);
				break;

			case DbVersions.LATEST_WITH_UPDATE_TIME_SUPPORT:
				migrateFromUpdateTimeSupport(db);
				break;

			default:
				throw new DbException();
		}
	}

	private void migrateFromCamelNamingStyle(SQLiteDatabase db) {
		db.beginTransaction();

		try {
			dropTables(db);
			createTables(db);

			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}

	private void dropTables(SQLiteDatabase db) {
		dropTable(db, DbTableNames.DECKS);
		dropTable(db, DbTableNames.CARDS);
		dropTable(db, DbTableNames.DB_LAST_UPDATE_TIME);
	}

	private void dropTable(SQLiteDatabase db, String tableName) {
		db.execSQL(String.format("drop table %s", tableName));
	}

	private void migrateFromUpdateTimeSupport(SQLiteDatabase db) {
		db.beginTransaction();

		try {
			dropTable(db, DbTableNames.DB_LAST_UPDATE_TIME);

			db.setTransactionSuccessful();
		}
		finally {
			db.endTransaction();
		}
	}
}
