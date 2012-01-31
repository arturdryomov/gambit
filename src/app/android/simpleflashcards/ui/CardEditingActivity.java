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
import app.android.simpleflashcards.models.Card;
import app.android.simpleflashcards.models.Deck;
import app.android.simpleflashcards.models.ModelsException;


public class CardEditingActivity extends Activity
{
	private final Context activityContext = this;

	private String frontSideText;
	private String backSideText;

	private int cardId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_editing);

		processActivityMessage();

		initializeBodyControls();

		new SetupExistingCardDataTask().execute();
	}

	private void processActivityMessage() {
		cardId = ActivityMessager.getMessageFromActivity(this);
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
				new CardUpdatingTask().execute();
			}
			else {
				UserAlerter.alert(activityContext, userDataErrorMessage);
			}
		}
	};

	private void readUserData() {
		EditText frontSideEdit = (EditText) findViewById(R.id.frondSideEdit);
		EditText backSideEdit = (EditText) findViewById(R.id.backSideEdit);

		frontSideText = frontSideEdit.getText().toString().trim();
		backSideText = backSideEdit.getText().toString().trim();
	}

	private String getUserDataErrorMessage() {
		String errorMesage;

		errorMesage = getFrontSideTextErrorMessage();
		if (!errorMesage.isEmpty()) {
			return errorMesage;
		}

		errorMesage = getBackSideTextErrorMessage();
		if (!errorMesage.isEmpty()) {
			return errorMesage;
		}

		return errorMesage;
	}

	private String getFrontSideTextErrorMessage() {
		if (frontSideText.isEmpty()) {
			return getString(R.string.enterFrontText);
		}

		return new String();
	}

	private String getBackSideTextErrorMessage() {
		if (backSideText.isEmpty()) {
			return getString(R.string.enterBackText);
		}

		return new String();
	}

	private class SetupExistingCardDataTask extends AsyncTask<Void, Void, String>
	{
		private ProgressDialogShowHelper progressDialogHelper;

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.gettingDeckName));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				Deck deck = SimpleFlashcardsApplication.getInstance().getDecks().getDeckByCardId(cardId);
				Card card = deck.getCardById(cardId);

				frontSideText = card.getFrontSideText();
				backSideText = card.getBackSideText();
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.isEmpty()) {
				updateCardInfo();
			}
			else {
				UserAlerter.alert(activityContext, result);
			}

			progressDialogHelper.hide();
		}
	}

	private void updateCardInfo() {
		EditText frontSideTextEdit = (EditText) findViewById(R.id.frondSideEdit);
		EditText backSideTextEdit = (EditText) findViewById(R.id.backSideEdit);

		frontSideTextEdit.setText(frontSideText);
		backSideTextEdit.setText(backSideText);
	}

	private class CardUpdatingTask extends AsyncTask<Void, Void, String>
	{
		private ProgressDialogShowHelper progressDialogHelper;

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.updatingCard));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				Deck deck = SimpleFlashcardsApplication.getInstance().getDecks().getDeckByCardId(cardId);
				Card card = deck.getCardById(cardId);

				card.setFrontSideText(frontSideText);
				card.setBackSideText(backSideText);
			}
			catch (ModelsException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialogHelper.hide();

			if (result.isEmpty()) {
				finish();
			}
			else {
				UserAlerter.alert(activityContext, result);
			}
		}
	}
}
