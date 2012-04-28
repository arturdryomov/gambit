package app.android.gambit.ui;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import app.android.gambit.R;
import app.android.gambit.local.Card;
import app.android.gambit.local.DbException;


public class CardEditingActivity extends Activity
{
	private final Context activityContext = this;

	private Card card;
	private String frontSideText;
	private String backSideText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_editing);

		initializeBodyControls();

		processReceivedCard();
		setUpReceivedCardData();
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
				callCardUpdating();
			}
			else {
				UserAlerter.alert(activityContext, userDataErrorMessage);
			}
		}

		private void callCardUpdating() {
			new CardUpdatingTask().execute();
		}
	};

	private void readUserDataFromFields() {
		EditText frontSideEdit = (EditText) findViewById(R.id.frondSideEdit);
		EditText backSideEdit = (EditText) findViewById(R.id.backSideEdit);

		frontSideText = frontSideEdit.getText().toString().trim();
		backSideText = backSideEdit.getText().toString().trim();
	}

	private String getUserDataErrorMessage() {
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
				card.setFrontSideText(frontSideText);
				card.setBackSideText(backSideText);
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
				finish();
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	private void processReceivedCard() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			card = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
		}
		else {
			UserAlerter.alert(activityContext, getString(R.string.someError));

			finish();
		}
	}

	private void setUpReceivedCardData() {
		frontSideText = card.getFrontSideText();
		backSideText = card.getBackSideText();

		EditText frontSideTextEdit = (EditText) findViewById(R.id.frondSideEdit);
		EditText backSideTextEdit = (EditText) findViewById(R.id.backSideEdit);

		frontSideTextEdit.setText(frontSideText);
		backSideTextEdit.setText(backSideText);
	}
}
