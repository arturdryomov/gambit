package app.android.gambit.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import app.android.gambit.R;
import app.android.gambit.local.AlreadyExistsException;
import app.android.gambit.local.DbProvider;
import app.android.gambit.local.Deck;
import app.android.gambit.local.DbException;


public class DeckCreationActivity extends Activity
{
	private final Context activityContext = this;

	private String deckName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deck_creation);

		initializeBodyControls();
	}

	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(confirmListener);
	}

	private final OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			readUserDataFromFields();

			String userDataErrorMessage = getUserDataErrorMessage();

			if (userDataErrorMessage.isEmpty()) {
				callDeckCreation();
			}
			else {
				UserAlerter.alert(activityContext, userDataErrorMessage);
			}
		}

		private void callDeckCreation() {
			new DeckCreationTask().execute();
		}
	};

	private void readUserDataFromFields() {
		EditText deckNameEdit = (EditText) findViewById(R.id.deckNameEdit);

		deckName = deckNameEdit.getText().toString().trim();
	}

	private String getUserDataErrorMessage() {
		return getDeckNameErrorMessage();
	}

	private String getDeckNameErrorMessage() {
		if (deckName.isEmpty()) {
			return getString(R.string.enterDeckName);
		}

		return new String();
	}

	private class DeckCreationTask extends AsyncTask<Void, Void, String>
	{
		private ProgressDialogShowHelper progressDialogHelper;

		private Deck deck;

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.creatingDeck));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				deck = DbProvider.getInstance().getDecks().addNewDeck(deckName);
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.deckAlreadyExists);
			}
			catch (DbException e) {
				return getString(R.string.someError);
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
