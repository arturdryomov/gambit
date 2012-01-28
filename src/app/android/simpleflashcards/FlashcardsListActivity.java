package app.android.simpleflashcards;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class FlashcardsListActivity extends ListActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcards);
		
		initializeActionbar();
	}
	
	private void initializeActionbar() {
		ImageButton updateButton = (ImageButton) findViewById(R.id.updateButton);
		updateButton.setOnClickListener(updateListener);
		
		ImageButton newItemCreationButton = (ImageButton) findViewById(R.id.itemCreationButton);
		newItemCreationButton.setOnClickListener(flashcardCreationListener);
	}
	
	private OnClickListener updateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO: Call update task
		}
	};
	
	private OnClickListener flashcardCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			callActivity(FlashcardCreationActivity.class);
		}
	};
	
	private void callActivity(Class<?> cls) {
		Intent callIntent = new Intent(getApplicationContext(), cls);
		startActivity(callIntent);
	}
}
