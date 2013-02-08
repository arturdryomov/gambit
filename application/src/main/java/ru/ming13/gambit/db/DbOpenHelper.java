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

package ru.ming13.gambit.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbOpenHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "gambit.db";

	private final Context context;

	public DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DbSchema.Versions.CURRENT);

		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);

		writeExampleDeck(db);
	}

	private void createTables(SQLiteDatabase db) {
		createTable(db, DbSchema.Tables.DECKS, buildDecksTableDescription());
		createTable(db, DbSchema.Tables.CARDS, buildCardsTableDescription());
	}

	private void createTable(SQLiteDatabase db, String tableName, String tableDescription) {
		db.execSQL(String.format("create table %s (%s)", tableName, tableDescription));
	}

	private String buildDecksTableDescription() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(
			String.format("%s %s, ", DbSchema.DecksColumns._ID, DbSchema.DecksColumnsParameters._ID));
		queryBuilder.append(
			String.format("%s %s, ", DbSchema.DecksColumns.TITLE, DbSchema.DecksColumnsParameters.TITLE));
		queryBuilder.append(String.format("%s %s", DbSchema.DecksColumns.CURRENT_CARD_INDEX,
			DbSchema.DecksColumnsParameters.CURRENT_CARD_INDEX));

		return queryBuilder.toString();
	}

	private String buildCardsTableDescription() {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append(
			String.format("%s %s, ", DbSchema.CardsColumns._ID, DbSchema.CardsColumnsParameters._ID));
		queryBuilder.append(String.format("%s %s, ", DbSchema.CardsColumns.DECK_ID,
			DbSchema.CardsColumnsParameters.DECK_ID));
		queryBuilder.append(String.format("%s %s, ", DbSchema.CardsColumns.FRONT_SIDE_TEXT,
			DbSchema.CardsColumnsParameters.FRONT_SIDE_TEXT));
		queryBuilder.append(String.format("%s %s, ", DbSchema.CardsColumns.BACK_SIDE_TEXT,
			DbSchema.CardsColumnsParameters.BACK_SIDE_TEXT));
		queryBuilder.append(String.format("%s %s", DbSchema.CardsColumns.ORDER_INDEX,
			DbSchema.CardsColumnsParameters.ORDER_INDEX));

		return queryBuilder.toString();
	}

	private void writeExampleDeck(SQLiteDatabase db) {
		ExampleDeckWriter.writeDeck(context, db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldDatabaseVersion, int newDatabaseVersion) {
		switch (oldDatabaseVersion) {
			case DbSchema.Versions.LATEST_WITHOUT_DECK_CARDS_CASCADE_DELETION:
				migrateFromCardsNotCascadeDeletion(db);
				break;

			case DbSchema.Versions.LATEST_WITH_CAMEL_NAMING_STYLE:
				migrateFromCamelNamingStyle(db);
				break;

			case DbSchema.Versions.LATEST_WITH_UPDATE_TIME_SUPPORT:
				migrateFromUpdateTimeSupport(db);
				break;

			default:
				migrateFromUnknownDatabaseVersion(db);
				break;
		}
	}

	private void migrateFromCardsNotCascadeDeletion(SQLiteDatabase db) {
		createTemporaryTable(db, buildTemporaryTableName(DbSchema.Tables.CARDS),
			buildCardsTableDescription());
		copyTableContents(db, DbSchema.Tables.CARDS, buildTemporaryTableName(DbSchema.Tables.CARDS));

		dropTable(db, DbSchema.Tables.CARDS);
		createTable(db, DbSchema.Tables.CARDS, buildCardsTableDescription());

		copyTableContents(db, buildTemporaryTableName(DbSchema.Tables.CARDS), DbSchema.Tables.CARDS);
		dropTable(db, buildTemporaryTableName(DbSchema.Tables.CARDS));

		db.setTransactionSuccessful();
	}

	private void createTemporaryTable(SQLiteDatabase db, String tableName, String tableDescription) {
		db.execSQL(String.format("create temporary table %s (%s)", tableName, tableDescription));
	}

	private String buildTemporaryTableName(String originalTableName) {
		return String.format("%sTemporary", originalTableName);
	}

	private void copyTableContents(SQLiteDatabase db, String departureTableName, String destinationTableName) {
		db.execSQL(
			String.format("insert into %s select * from %s", destinationTableName, departureTableName));
	}

	private void migrateFromCamelNamingStyle(SQLiteDatabase db) {
		dropTables(db);
		createTables(db);
	}

	private void dropTables(SQLiteDatabase db) {
		dropTable(db, DbSchema.Tables.DECKS);
		dropTable(db, DbSchema.Tables.CARDS);
		dropTable(db, DbSchema.Tables.DB_LAST_UPDATE_TIME);
	}

	private void dropTable(SQLiteDatabase db, String tableName) {
		db.execSQL(String.format("drop table %s", tableName));
	}

	private void migrateFromUpdateTimeSupport(SQLiteDatabase db) {
		dropTable(db, DbSchema.Tables.DB_LAST_UPDATE_TIME);
	}

	private void migrateFromUnknownDatabaseVersion(SQLiteDatabase db) {
		dropTables(db);
		createTables(db);
	}

	@Override
	public SQLiteDatabase getWritableDatabase() {
		SQLiteDatabase db = super.getWritableDatabase();

		db.execSQL(buildForeignKeysEnablingQuery());

		return db;
	}

	private String buildForeignKeysEnablingQuery() {
		return "pragma foreign_keys = on";
	}
}
