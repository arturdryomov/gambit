package app.android.simpleflashcards;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class FlashcardsDecksListActivity extends ListActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcards_decks);
		
		initializeActionbar();
	}
	
	private void initializeActionbar() {
		ImageButton updateButton = (ImageButton) findViewById(R.id.updateButton);
		updateButton.setOnClickListener(updateListener);
		
		ImageButton newItemCreationButton = (ImageButton) findViewById(R.id.itemCreationButton);
		newItemCreationButton.setOnClickListener(deckCreationListener);
	}
	
	private OnClickListener updateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO: Call update task
		}
	};
	
	private OnClickListener deckCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			callActivity(FlashcardsDeckCreationActivity.class);
		}
	};
	
	private void callActivity(Class<?> cls) {
		Intent callIntent = new Intent(getApplicationContext(), cls);
		startActivity(callIntent);
	}
}
