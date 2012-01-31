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
import app.android.simpleflashcards.models.Deck;
import app.android.simpleflashcards.models.ModelsException;


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

	private OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			readUserDataFromFields();

			String userDataErrorMessage = getUserDataErrorMessage();

			if (userDataErrorMessage.isEmpty()) {
				new FlashcardsDeckCreationTask().execute();
			}
			else {
				UserAlerter.alert(activityContext, userDataErrorMessage);
			}
		}
	};

	private void readUserDataFromFields() {
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

		return new String();
	}

	private class FlashcardsDeckCreationTask extends AsyncTask<Void, Void, String>
	{
		private ProgressDialogShowHelper progressDialogHelper;

		private int deckId;

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.creatingDeck));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				Deck deck = SimpleFlashcardsApplication.getInstance().getDecks().addNewDeck(deckName);
				deckId = deck.getId();
			}
			catch (ModelsException e) {
				return e.getMessage();
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialogHelper.hide();

			if (result.isEmpty()) {
				ActivityMessager.startActivityWithMessage(activityContext, CardsListActivity.class, deckId);
				finish();
			}
			else {
				UserAlerter.alert(activityContext, result);
			}
		}
	}
}
