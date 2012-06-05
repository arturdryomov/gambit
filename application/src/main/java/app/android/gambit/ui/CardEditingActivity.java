package app.android.gambit.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import app.android.gambit.R;
import app.android.gambit.local.Card;


public class CardEditingActivity extends CardCreationActivity
{
	private Card card;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpReceivedCardData();
	}

	@Override
	protected void performSubmitAction() {
		new UpdateCardTask().execute();
	}

	private class UpdateCardTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			card.setFrontSideText(frontSideText);
			card.setBackSideText(backSideText);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			finish();
		}
	}

	@Override
	protected void processReceivedData() {
		try {
			card = (Card) IntentProcessor.getMessage(this);
		}
		catch (IntentCorruptedException e) {
			UserAlerter.alert(activityContext, R.string.error_unspecified);

			finish();
		}
	}

	private void setUpReceivedCardData() {
		EditText frontSideTextEdit = (EditText) findViewById(R.id.edit_front_side_text);
		EditText backSideTextEdit = (EditText) findViewById(R.id.edit_back_side_text);

		frontSideTextEdit.setText(card.getFrontSideText());
		backSideTextEdit.setText(card.getBackSideText());
	}
}
