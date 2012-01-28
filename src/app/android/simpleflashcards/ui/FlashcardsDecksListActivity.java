package app.android.simpleflashcards.ui;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.simpleflashcards.R;


public class FlashcardsDecksListActivity extends ListActivity
{
	private final Context activityContext = this;
	
	private ArrayList<HashMap<String, Object>> decksData;
	
	private static final String LIST_ITEM_TEXT_ID = "text";
	
	public FlashcardsDecksListActivity() {
		super();
		
		decksData = new ArrayList<HashMap<String, Object>>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcards_decks);
		
		initializeActionbar();
		initializeDecksList();
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
			ActivityStarter.start(activityContext, FlashcardsDeckCreationActivity.class);
		}
	};
	
	private void initializeDecksList() {
		SimpleAdapter decksAdapter = new SimpleAdapter(activityContext, decksData,
			R.layout.flashcards_decks_list_item, new String[] { LIST_ITEM_TEXT_ID },
			new int[] { R.id.text });
		
		setListAdapter(decksAdapter);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	
	private void addItemToDecksList(String text) {
		HashMap<String, Object> deckItem = new HashMap<String, Object>();
		
		deckItem.put(LIST_ITEM_TEXT_ID, text);
		
		decksData.add(deckItem);
	}
	
	private void updateDecksList() {
		((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
	}
}
