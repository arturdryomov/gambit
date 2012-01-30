package app.android.simpleflashcards.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import app.android.simpleflashcards.R;


public class FlashcardCreationActivity extends Activity
{
	private final Context activityContext = this;

	private String frontSideText;
	private String backSideText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcard_creation);

		initializeBodyControls();
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
				// TODO: Call flashcard creation task

				finish();
			} else {
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
}
