package app.android.gambit.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import app.android.gambit.R;
import app.android.gambit.local.Card;


public class CardEditingActivity extends CardCreationActivity
{
	private Card card;
	private String frontSideText;
	private String backSideText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setActivityViewsInscriptions();

		setUpReceivedCardData();
	}

	@Override
	protected void performSubmitAction() {
		new CardUpdatingTask().execute();
	}

	private class CardUpdatingTask extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialogShowHelper progressDialogHelper;

		@Override
		protected void onPreExecute() {
			progressDialogHelper = new ProgressDialogShowHelper();
			progressDialogHelper.show(activityContext, getString(R.string.loading_updating_card));
		}

		@Override
		protected Void doInBackground(Void... params) {
			card.setFrontSideText(frontSideText);
			card.setBackSideText(backSideText);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialogHelper.hide();

			finish();
		}
	}

	@Override
	protected void processReceivedData() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			card = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
		}
		else {
			UserAlerter.alert(activityContext, getString(R.string.error_unspecified));

			finish();
		}
	}

	private void setActivityViewsInscriptions() {
		Button confirmButton = (Button) findViewById(R.id.button_confirm);
		confirmButton.setText(R.string.button_update_card);
	}

	private void setUpReceivedCardData() {
		frontSideText = card.getFrontSideText();
		backSideText = card.getBackSideText();

		EditText frontSideTextEdit = (EditText) findViewById(R.id.edit_frond_side);
		EditText backSideTextEdit = (EditText) findViewById(R.id.edit_back_side);

		frontSideTextEdit.setText(frontSideText);
		backSideTextEdit.setText(backSideText);
	}
}
