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

package ru.ming13.gambit.cursor;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.venmo.cursor.IterableCursorWrapper;

import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.provider.GambitContract;

public class CardsCursor extends IterableCursorWrapper<Card>
{
	public CardsCursor(@NonNull Cursor cursor) {
		super(cursor);
	}

	@Override
	public Card peek() {
		long cardId = getLong(GambitContract.Cards._ID, CursorDefaults.LONG);
		String cardFrontSideText = getString(GambitContract.Cards.FRONT_SIDE_TEXT, CursorDefaults.STRING);
		String cardBackSideText = getString(GambitContract.Cards.BACK_SIDE_TEXT, CursorDefaults.STRING);

		return new Card(cardId, cardFrontSideText, cardBackSideText);
	}
}
