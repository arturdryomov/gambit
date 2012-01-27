package app.android.simpleflashcards;


import android.app.ListActivity;
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
	
	void initializeActionbar() {
		ImageButton updateButton = (ImageButton) findViewById(R.id.updateButton);
		updateButton.setOnClickListener(updateListener);
		
		ImageButton newItemCreationButton = (ImageButton) findViewById(R.id.newItemCreationButton);
		newItemCreationButton.setOnClickListener(newItemCreationListener);
	}
	
	OnClickListener updateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO: Call update task
		}
	};
	
	OnClickListener newItemCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO: Call creation task
		}
	};
}
