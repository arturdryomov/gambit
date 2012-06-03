package app.android.gambit.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import app.android.gambit.R;
import app.android.gambit.local.Deck;


public class CardCreationActivity extends FormActivity
{
	private Deck deck;

	protected String frontSideText;
	protected String backSideText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_card_creation);
		super.onCreate(savedInstanceState);

		processReceivedData();
	}

	@Override
	protected void readUserDataFromFields() {
		frontSideText = getTextFromEdit(R.id.edit_front_side);
		backSideText = getTextFromEdit(R.id.edit_back_side);
	}

	@Override
	protected String getUserDataErrorMessage() {
		String errorMessage;

		errorMessage = getFrontSideTextErrorMessage();
		if (!errorMessage.isEmpty()) {
			return errorMessage;
		}

		errorMessage = getBackSideTextErrorMessage();
		if (!errorMessage.isEmpty()) {
			return errorMessage;
		}

		return errorMessage;
	}

	private String getFrontSideTextErrorMessage() {
		if (frontSideText.isEmpty()) {
			return getString(R.string.error_empty_card_front_text);
		}

		return new String();
	}

	private String getBackSideTextErrorMessage() {
		if (backSideText.isEmpty()) {
			return getString(R.string.error_empty_card_back_text);
		}

		return new String();
	}

	@Override
	protected void performSubmitAction() {
		new CardCreationTask().execute();
	}

	private class CardCreationTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			deck.addNewCard(frontSideText, backSideText);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			finish();
		}
	}

	protected void processReceivedData() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			deck = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
		}
		else {
			UserAlerter.alert(activityContext, getString(R.string.error_unspecified));

			finish();
		}
	}
}
