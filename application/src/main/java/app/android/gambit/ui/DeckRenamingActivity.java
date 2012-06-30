package app.android.gambit.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import app.android.gambit.R;
import app.android.gambit.local.AlreadyExistsException;
import app.android.gambit.local.Deck;


public class DeckRenamingActivity extends DeckCreationActivity
{
	private Deck deck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		processReceivedDeck();
		setUpReceivedDeckData();
	}

	@Override
	protected void performSubmitAction() {
		new UpdateDeckTask().execute();
	}

	private class UpdateDeckTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				deck.setTitle(deckName);
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.error_deck_already_exists);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			if (TextUtils.isEmpty(errorMessage)) {
				finish();
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	private void processReceivedDeck() {
		try {
			deck = (Deck) IntentProcessor.getMessage(this);
		}
		catch (IntentCorruptedException e) {
			UserAlerter.alert(activityContext, R.string.error_unspecified);

			finish();
		}
	}

	private void setUpReceivedDeckData() {
		EditText deckNameEdit = (EditText) findViewById(R.id.edit_deck_name);
		deckNameEdit.setText(deck.getTitle());
	}
}
