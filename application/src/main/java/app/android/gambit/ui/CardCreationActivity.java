package app.android.gambit.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
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
		EditText frontSideEdit = (EditText) findViewById(R.id.edit_frond_side);
		EditText backSideEdit = (EditText) findViewById(R.id.edit_back_side);

		frontSideText = frontSideEdit.getText().toString().trim();
		backSideText = backSideEdit.getText().toString().trim();
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
		private ProgressDialogShowHelper progressDialogHelper;

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.loading_creating_card));
		}

		@Override
		protected Void doInBackground(Void... params) {
			deck.addNewCard(frontSideText, backSideText);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialogHelper.hide();

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
