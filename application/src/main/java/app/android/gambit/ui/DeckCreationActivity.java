package app.android.gambit.ui;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
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
		EditText deckNameEdit = (EditText) findViewById(R.id.edit_deck_name);

		deckName = deckNameEdit.getText().toString().trim();
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
		new DeckCreationTask().execute();
	}

	private class DeckCreationTask extends AsyncTask<Void, Void, String>
	{
		private ProgressDialogShowHelper progressDialogHelper;

		private Deck deck;

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.loading_creating_deck));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				deck = DbProvider.getInstance().getDecks().addNewDeck(deckName);
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.error_deck_already_exists);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			progressDialogHelper.hide();

			if (errorMessage.isEmpty()) {
				Intent callIntent = IntentFactory.createCardsListIntent(activityContext, deck);
				startActivity(callIntent);

				finish();
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}
}
