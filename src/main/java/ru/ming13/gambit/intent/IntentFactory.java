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

package ru.ming13.gambit.intent;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import ru.ming13.gambit.R;
import ru.ming13.gambit.activity.CardCreationActivity;
import ru.ming13.gambit.activity.CardEditingActivity;
import ru.ming13.gambit.activity.CardsListActivity;
import ru.ming13.gambit.activity.CardsPagerActivity;
import ru.ming13.gambit.activity.DeckCreationActivity;
import ru.ming13.gambit.activity.DeckEditingActivity;
import ru.ming13.gambit.activity.DecksListActivity;
import ru.ming13.gambit.activity.LicensesActivity;


public final class IntentFactory
{
	private IntentFactory() {
	}

	public static Intent createDecksIntent(Context context) {
		return new Intent(context, DecksListActivity.class);
	}

	public static Intent createDeckCreationIntent(Context context) {
		return new Intent(context, DeckCreationActivity.class);
	}

	public static Intent createDeckRenamingIntent(Context context, Uri deckUri) {
		Intent intent = new Intent(context, DeckEditingActivity.class);
		intent.putExtra(IntentExtras.DECK_URI, deckUri);

		return intent;
	}

	public static Intent createCardsIntent(Context context, Uri deckUri) {
		Intent intent = new Intent(context, CardsListActivity.class);
		intent.putExtra(IntentExtras.DECK_URI, deckUri);

		return intent;
	}

	public static Intent createCardCreationIntent(Context context, Uri cardsUri) {
		Intent intent = new Intent(context, CardCreationActivity.class);
		intent.putExtra(IntentExtras.CARDS_URI, cardsUri);

		return intent;
	}

	public static Intent createCardModificationIntent(Context context, Uri cardUri) {
		Intent intent = new Intent(context, CardEditingActivity.class);
		intent.putExtra(IntentExtras.CARD_URI, cardUri);

		return intent;
	}

	public static Intent createCardsPagerIntent(Context context, Uri deckUri) {
		Intent intent = new Intent(context, CardsPagerActivity.class);
		intent.putExtra(IntentExtras.DECK_URI, deckUri);

		return intent;
	}

	public static Intent createLicensesIntent(Context context) {
		return new Intent(context, LicensesActivity.class);
	}

	public static Intent createFeedbackEmailIntent(Context context) {
		String feedbackAddress = context.getString(R.string.email_feedback_address);
		String feedbackSubject = context.getString(R.string.email_feedback_subject);

		Uri emailUri = Uri.parse(
			context.getString(R.string.email_uri_format, feedbackAddress, feedbackSubject));

		return new Intent(Intent.ACTION_SENDTO, emailUri);
	}

	public static Intent createGooglePlayAppIntent(Context context) {
		Uri googlePlayUri = Uri.parse(context.getString(R.string.url_app_google_play));

		return new Intent(Intent.ACTION_VIEW, googlePlayUri);
	}

	public static Intent createGooglePlayWebIntent(Context context) {
		Uri googlePlayUri = Uri.parse(context.getString(R.string.url_web_google_play));

		return new Intent(Intent.ACTION_VIEW, googlePlayUri);
	}
}
