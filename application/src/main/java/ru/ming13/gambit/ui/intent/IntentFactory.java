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

package ru.ming13.gambit.ui.intent;


import android.content.Context;
import android.content.Intent;
import ru.ming13.gambit.local.Card;
import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.ui.activity.CardCreationActivity;
import ru.ming13.gambit.ui.activity.CardEditingActivity;
import ru.ming13.gambit.ui.activity.CardsListActivity;
import ru.ming13.gambit.ui.activity.CardsViewingActivity;
import ru.ming13.gambit.ui.activity.DeckCreationActivity;
import ru.ming13.gambit.ui.activity.DeckRenamingActivity;


public final class IntentFactory
{
	public static final String MESSAGE_ID;

	static {
		MESSAGE_ID = String.format("%s.message", IntentFactory.class.getPackage().getName());
	}

	private IntentFactory() {
	}

	public static Intent createDeckCreationIntent(Context context) {
		return new Intent(context, DeckCreationActivity.class);
	}

	public static Intent createDeckEditingIntent(Context context, Deck deck) {
		Intent intent = new Intent(context, DeckRenamingActivity.class);
		intent.putExtra(MESSAGE_ID, deck);

		return intent;
	}

	public static Intent createCardsEditingIntent(Context context, Deck deck) {
		Intent intent = new Intent(context, CardsListActivity.class);
		intent.putExtra(MESSAGE_ID, deck);

		return intent;
	}

	public static Intent createCardCreationIntent(Context context, Deck deck) {
		Intent intent = new Intent(context, CardCreationActivity.class);
		intent.putExtra(MESSAGE_ID, deck);

		return intent;
	}

	public static Intent createCardEditingIntent(Context context, Card card) {
		Intent intent = new Intent(context, CardEditingActivity.class);
		intent.putExtra(MESSAGE_ID, card);

		return intent;
	}

	public static Intent createCardsViewingIntent(Context context, Deck deck) {
		Intent intent = new Intent(context, CardsViewingActivity.class);
		intent.putExtra(MESSAGE_ID, deck);

		return intent;
	}
}
