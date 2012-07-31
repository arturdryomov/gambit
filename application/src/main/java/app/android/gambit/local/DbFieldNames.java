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

package app.android.gambit.local;


final class DbFieldNames
{
	private DbFieldNames() {
	}

	public static final String ID = "_id";

	public static final String DECK_TITLE = "title";
	public static final String DECK_CURRENT_CARD_INDEX = "current_card_index";

	public static final String CARD_DECK_ID = "deck_id";
	public static final String CARD_FRONT_SIDE_TEXT = "front_page_side";
	public static final String CARD_BACK_SIDE_TEXT = "back_page_side";
	public static final String CARD_ORDER_INDEX = "order_index";

	public static final String DB_LAST_UPDATE_TIME = "update_time";
}
