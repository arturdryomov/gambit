package app.android.gambit.ui;


import android.content.Context;
import android.content.Intent;
import app.android.gambit.local.Card;
import app.android.gambit.local.Deck;


class IntentFactory
{
	public static final String MESSAGE_ID;

	static {
		MESSAGE_ID = String.format("%s.message", IntentFactory.class.getPackage().getName());
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
