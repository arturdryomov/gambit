package app.android.simpleflashcards.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import app.android.simpleflashcards.R;


public class FlashcardsDeckCreationActivity extends Activity
{
	private final Context activityContext = this;
	
	private String deckName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcards_deck_creation);
		
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
			
			if (isUserDataCurrect()) {
				// TODO: Call creation task
				
				ActivityStarter.start(activityContext, FlashcardsListActivity.class);
				
				finish();
			}
		}
	};
	
	private void readUserData() {
		EditText deckNameEdit = (EditText) findViewById(R.id.flashcardDeckNameEdit);
		
		deckName = deckNameEdit.getText().toString().trim();
	}
	
	private boolean isUserDataCurrect() {
		if (!isDeckNameCurrect()) {
			return false;
		}
		
		return true;
	}
	
	private boolean isDeckNameCurrect() {
		if (deckName.isEmpty()) {
			UserAlerter.alert(activityContext, getString(R.string.enterDeckName));
			
			return false;
		}
		
		// TODO: Call task to check existing decks on server
		
		return true;
	}
}
