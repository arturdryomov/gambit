package app.android.gambit.ui;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import app.android.gambit.R;
import app.android.gambit.local.AlreadyExistsException;
import app.android.gambit.local.DbProvider;
import app.android.gambit.local.Deck;


public class DeckCreationActivity extends FormActivity
{
	protected String deckName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_deck_creation);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void readUserDataFromFields() {
		deckName = getTextFromEdit(R.id.edit_deck_name);
	}

	@Override
	protected String getUserDataErrorMessage() {
		return getDeckNameErrorMessage();
	}

	private String getDeckNameErrorMessage() {
		if (deckName.isEmpty()) {
			return getString(R.string.error_empty_deck_name);
		}

		return new String();
	}

	@Override
	protected void performSubmitAction() {
		new CreateDeckTask().execute();
	}

	private class CreateDeckTask extends AsyncTask<Void, Void, String>
	{
		private Deck deck;

		@Override
		protected String doInBackground(Void... params) {
			try {
				deck = DbProvider.getInstance().getDecks().createDeck(deckName);
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.error_deck_already_exists);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (errorMessage.isEmpty()) {
				callCardsEditing(deck);

				finish();
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	private void callCardsEditing(Deck deck) {
		Intent callIntent = IntentFactory.createCardsEditingIntent(activityContext, deck);
		startActivity(callIntent);
	}
}
