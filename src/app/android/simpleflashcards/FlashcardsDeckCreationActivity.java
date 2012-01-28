package app.android.simpleflashcards;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class FlashcardsDeckCreationActivity extends Activity
{
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
				
				callActivity(FlashcardsListActivity.class);
				
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
			alertUser("Enter deck name");
			
			return false;
		}
		
		// TODO: Call task to check existing decks on server
		
		return true;
	}
	
	private void alertUser(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
	}
	
	private void callActivity(Class<?> cls) {
		Intent callIntent = new Intent(getApplicationContext(), cls);
		startActivity(callIntent);
	}
}
