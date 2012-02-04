package app.android.simpleflashcards.ui;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import app.android.simpleflashcards.R;
import app.android.simpleflashcards.models.AlreadyExistsException;
import app.android.simpleflashcards.models.Deck;
import app.android.simpleflashcards.models.ModelsException;


public class DeckEditingActivity extends Activity
{
	private final Context activityContext = this;

	private Deck deck;
	private String deckName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deck_editing);

		initializeBodyControls();

		processReceivedDeck();
		setUpReceivedDeckData();
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
				new DeckUpdatingTask().execute();
			}
			else {
				UserAlerter.alert(activityContext, userDataErrorMessage);
			}
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

	private class DeckUpdatingTask extends AsyncTask<Void, Void, String>
	{
		private ProgressDialogShowHelper progressDialogHelper;

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.updatingDeck));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				deck.setTitle(deckName);
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.deckAlreadyExists);
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			progressDialogHelper.hide();

			if (errorMessage.isEmpty()) {
				finish();
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	private void processReceivedDeck() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			deck = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
		}
		else {
			UserAlerter.alert(activityContext, getString(R.string.someError));

			finish();
		}
	}

	private void setUpReceivedDeckData() {
		deckName = deck.getTitle();

		EditText deckNameEdit = (EditText) findViewById(R.id.deckNameEdit);
		deckNameEdit.setText(deckName);
	}
}
