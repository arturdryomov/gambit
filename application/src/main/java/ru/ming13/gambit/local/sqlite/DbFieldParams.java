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


final class DbFieldParams
{
	private DbFieldParams() {
	}

	public static final String ID = "integer primary key autoincrement not null unique";

	public static final String INDEX = "int not null";

	public static final String DECK_TITLE = "text not null unique";
	public static final String DECK_FOREIGN_ID;

	static {
		final String foreignKeyMask = "integer not null references %s(%s)";

		DECK_FOREIGN_ID = String.format(foreignKeyMask, DbTableNames.DECKS, DbFieldNames.ID);
	}

	public static final String CARD_TEXT = "text not null";

	public static final String DB_LAST_UPDATE_TIME = "text not null unique";
}
