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
import app.android.simpleflashcards.SimpleFlashcardsApplication;
import app.android.simpleflashcards.models.AlreadyExistsException;
import app.android.simpleflashcards.models.Deck;
import app.android.simpleflashcards.models.ModelsException;


public class DeckEditingActivity extends Activity
{
	private final Context activityContext = this;

	private Deck deck;
	private int deckId;
	private String deckName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deck_editing);

		processActivityMessage();

		initializeBodyControls();

		new SetupReceivedDeckTask().execute();
	}

	private void processActivityMessage() {
		deckId = ActivityMessager.getMessageFromActivity(this);
	}

	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(confirmListener);
	}

	private OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			readUserData();

			String userDataErrorMessage = getUserDataErrorMessage();

			if (userDataErrorMessage.isEmpty()) {
				new DeckUpdatingTask().execute();
			}
			else {
				UserAlerter.alert(activityContext, userDataErrorMessage);
			}
		}
	};

	private void readUserData() {
		EditText deckNameEdit = (EditText) findViewById(R.id.flashcardDeckNameEdit);

		deckName = deckNameEdit.getText().toString().trim();
	}

	private String getUserDataErrorMessage() {
		return getDeckNameErrorMessage();
	}

	private String getDeckNameErrorMessage() {
		if (deckName.isEmpty()) {
			return getString(R.string.enterDeckName);
		}

		// TODO: Call task to check existing decks on server

		return new String();
	}

	private class SetupReceivedDeckTask extends AsyncTask<Void, Void, String>
	{
		ProgressDialogShowHelper progressDialogHelper;

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.gettingDeckName));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				deck = SimpleFlashcardsApplication.getInstance().getDecks().getDeckById(deckId);
				deckName = deck.getTitle();
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String result) {
			updateDeckName();

			progressDialogHelper.hide();

			if (!result.isEmpty()) {
				UserAlerter.alert(activityContext, result);
			}
		}
	}

	private void updateDeckName() {
		EditText deckNameEdit = (EditText) findViewById(R.id.flashcardDeckNameEdit);
		deckNameEdit.setText(deckName);
	}

	private class DeckUpdatingTask extends AsyncTask<Void, Void, String>
	{
		ProgressDialogShowHelper progressDialogHelper;

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
		protected void onPostExecute(String result) {
			progressDialogHelper.hide();

			if (!result.isEmpty()) {
				UserAlerter.alert(activityContext, result);
			}
			else {
				finish();
			}
		}
	}
}
