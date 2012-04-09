package app.android.simpleflashcards.ui;


import android.content.Context;
import android.content.Intent;
import app.android.simpleflashcards.models.Card;
import app.android.simpleflashcards.models.Deck;


public class IntentFactory
{
	public static final String MESSAGE_ID;

	static {
		MESSAGE_ID = String.format("%s.message", IntentFactory.class.getPackage().getName());
	}

	public static Intent createSyncSetupIntent(Context context) {
		Intent intent = new Intent(context, SyncSetupActivity.class);

		return intent;
	}

	public static Intent createSettingsIntent(Context context) {
		Intent intent = new Intent(context, SettingsActivity.class);

		return intent;
	}

	public static Intent createDeckCreationIntent(Context context) {
		Intent intent = new Intent(context, DeckCreationActivity.class);

		return intent;
	}

	public static Intent createDeckEditingIntent(Context context, Deck deck) {
		Intent intent = new Intent(context, DeckRenamingActivity.class);
		intent.putExtra(MESSAGE_ID, deck);

		return intent;
	}

	public static Intent createCardsListIntent(Context context, Deck deck) {
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
