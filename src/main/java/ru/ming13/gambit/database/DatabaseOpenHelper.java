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
import android.support.annotation.NonNull;

import ru.ming13.gambit.util.SqlBuilder;

public class DatabaseOpenHelper extends SQLiteOpenHelper
{
	private final Context context;

	public DatabaseOpenHelper(@NonNull Context context) {
		this(context, DatabaseSchema.DATABASE_NAME);
	}

	public DatabaseOpenHelper(@NonNull Context context, @NonNull String databasePath) {
		super(context, databasePath, null, DatabaseSchema.Versions.CURRENT);

		this.context = context.getApplicationContext();
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		createTables(database);

		createDefaults(database);
	}

	private void createTables(SQLiteDatabase database) {
		createTable(database, DatabaseSchema.Tables.DECKS, buildDecksTableDescription());
		createTable(database, DatabaseSchema.Tables.CARDS, buildCardsTableDescription());
	}

	private void createTable(SQLiteDatabase database, String tableName, String tableDescription) {
		database.execSQL(SqlBuilder.buildTableCreationClause(tableName, tableDescription));
	}

	private String buildDecksTableDescription() {
		return SqlBuilder.buildTableDescription(
			SqlBuilder.buildColumnDescription(
				DatabaseSchema.DecksColumns._ID, DatabaseSchema.DecksColumnsParameters._ID),
			SqlBuilder.buildColumnDescription(
				DatabaseSchema.DecksColumns.TITLE, DatabaseSchema.DecksColumnsParameters.TITLE),
			SqlBuilder.buildColumnDescription(
				DatabaseSchema.DecksColumns.CURRENT_CARD_INDEX, DatabaseSchema.DecksColumnsParameters.CURRENT_CARD_INDEX));
	}

	private String buildCardsTableDescription() {
		return SqlBuilder.buildTableDescription(
			SqlBuilder.buildColumnDescription(
				DatabaseSchema.CardsColumns._ID, DatabaseSchema.CardsColumnsParameters._ID),
			SqlBuilder.buildColumnDescription(
				DatabaseSchema.CardsColumns.DECK_ID, DatabaseSchema.CardsColumnsParameters.DECK_ID),
			SqlBuilder.buildColumnDescription(
				DatabaseSchema.CardsColumns.FRONT_SIDE_TEXT, DatabaseSchema.CardsColumnsParameters.FRONT_SIDE_TEXT),
			SqlBuilder.buildColumnDescription(
				DatabaseSchema.CardsColumns.BACK_SIDE_TEXT, DatabaseSchema.CardsColumnsParameters.BACK_SIDE_TEXT),
			SqlBuilder.buildColumnDescription(
				DatabaseSchema.CardsColumns.ORDER_INDEX, DatabaseSchema.CardsColumnsParameters.ORDER_INDEX));
	}

	private void createDefaults(SQLiteDatabase database) {
		DatabaseDefaults.at(context, database).writeDeck();
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldDatabaseVersion, int newDatabaseVersion) {
	}
}
