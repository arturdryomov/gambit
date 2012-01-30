package app.android.simpleflashcards.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import app.android.simpleflashcards.R;


public class FlashcardsDeckEditingActivity extends Activity
{
	private final Context activityContext = this;

	private String deckName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcards_deck_editing);

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
				// TODO: Call saving task
			} else {
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
}
