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

package ru.ming13.gambit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.ming13.gambit.util.DefaultDeckWriter;
import ru.ming13.gambit.util.SqlBuilder;

public class DatabaseOpenHelper extends SQLiteOpenHelper
{
	private final Context context;

	public DatabaseOpenHelper(Context context) {
		super(context, DatabaseSchema.DATABASE_NAME, null, DatabaseSchema.Versions.CURRENT);

		this.context = context.getApplicationContext();
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		createTables(database);

		createDefaultDeck(database);
	}

	private void createTables(SQLiteDatabase database) {
		createTable(database, DatabaseSchema.Tables.DECKS, buildDecksTableDescription());
		createTable(database, DatabaseSchema.Tables.CARDS, buildCardsTableDescription());
	}

	private void createTable(SQLiteDatabase database, String tableName, String tableDescription) {
		database.execSQL(SqlBuilder.buildCreateTableClause(tableName, tableDescription));
	}

	private String buildDecksTableDescription() {
		return SqlBuilder.buildTableDescription(
			SqlBuilder.buildTableColumnDescription(
				DatabaseSchema.DecksColumns._ID, DatabaseSchema.DecksColumnsParameters._ID),
			SqlBuilder.buildTableColumnDescription(
				DatabaseSchema.DecksColumns.TITLE, DatabaseSchema.DecksColumnsParameters.TITLE),
			SqlBuilder.buildTableColumnDescription(
				DatabaseSchema.DecksColumns.CURRENT_CARD_INDEX, DatabaseSchema.DecksColumnsParameters.CURRENT_CARD_INDEX));
	}

	private String buildCardsTableDescription() {
		return SqlBuilder.buildTableDescription(
			SqlBuilder.buildTableColumnDescription(
				DatabaseSchema.CardsColumns._ID, DatabaseSchema.CardsColumnsParameters._ID),
			SqlBuilder.buildTableColumnDescription(
				DatabaseSchema.CardsColumns.DECK_ID, DatabaseSchema.CardsColumnsParameters.DECK_ID),
			SqlBuilder.buildTableColumnDescription(
				DatabaseSchema.CardsColumns.FRONT_SIDE_TEXT, DatabaseSchema.CardsColumnsParameters.FRONT_SIDE_TEXT),
			SqlBuilder.buildTableColumnDescription(
				DatabaseSchema.CardsColumns.BACK_SIDE_TEXT, DatabaseSchema.CardsColumnsParameters.BACK_SIDE_TEXT),
			SqlBuilder.buildTableColumnDescription(
				DatabaseSchema.CardsColumns.ORDER_INDEX, DatabaseSchema.CardsColumnsParameters.ORDER_INDEX));
	}

	private void createDefaultDeck(SQLiteDatabase database) {
		DefaultDeckWriter.writeDeck(context, database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldDatabaseVersion, int newDatabaseVersion) {
		switch (oldDatabaseVersion) {
			case DatabaseSchema.Versions.LATEST_WITH_CAMEL_NAMING_STYLE:
				dropTables(database);
				createTables(database);
				break;

			case DatabaseSchema.Versions.LATEST_WITH_UPDATE_TIME_SUPPORT:
				migrateFromUpdateTimeSupport(database);
				migrateFromDeckCardsWithoutCascadeDeletion(database);
				break;

			case DatabaseSchema.Versions.LATEST_WITHOUT_DECK_CARDS_CASCADE_DELETION:
				migrateFromDeckCardsWithoutCascadeDeletion(database);
				break;

			default:
				dropTables(database);
				createTables(database);
				break;
		}
	}

	private void dropTables(SQLiteDatabase database) {
		dropTable(database, DatabaseSchema.Tables.DECKS);
		dropTable(database, DatabaseSchema.Tables.CARDS);
		dropTable(database, DatabaseSchema.Tables.DB_LAST_UPDATE_TIME);
	}

	private void dropTable(SQLiteDatabase database, String tableName) {
		database.execSQL(SqlBuilder.buildDropTableClause(tableName));
	}

	private void migrateFromUpdateTimeSupport(SQLiteDatabase database) {
		dropTable(database, DatabaseSchema.Tables.DB_LAST_UPDATE_TIME);
	}

	private void migrateFromDeckCardsWithoutCascadeDeletion(SQLiteDatabase database) {
		createTempTable(database, buildTempTableName(DatabaseSchema.Tables.CARDS), buildCardsTableDescription());
		copyTableContents(database, DatabaseSchema.Tables.CARDS, buildTempTableName(DatabaseSchema.Tables.CARDS));

		dropTable(database, DatabaseSchema.Tables.CARDS);
		createTable(database, DatabaseSchema.Tables.CARDS, buildCardsTableDescription());

		copyTableContents(database, buildTempTableName(DatabaseSchema.Tables.CARDS), DatabaseSchema.Tables.CARDS);
		dropTable(database, buildTempTableName(DatabaseSchema.Tables.CARDS));
	}

	private void createTempTable(SQLiteDatabase database, String tableName, String tableDescription) {
		database.execSQL(SqlBuilder.buildCreateTempTableClause(tableName, tableDescription));
	}

	private String buildTempTableName(String originalTableName) {
		return String.format("%sTemp", originalTableName);
	}

	private void copyTableContents(SQLiteDatabase database, String departureTableName, String destinationTableName) {
		database.execSQL(SqlBuilder.buildCopyTableClause(departureTableName, destinationTableName));
	}

	@Override
	public SQLiteDatabase getWritableDatabase() {
		SQLiteDatabase database = super.getWritableDatabase();

		database.execSQL(SqlBuilder.buildPragmaForeignKeysClause());

		return database;
	}
}
