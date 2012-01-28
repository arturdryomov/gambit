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


public class FlashcardsListActivity extends ListActivity
{
	private final Context activityContext = this;
	
	private ArrayList<HashMap<String, Object>> flashcardsData;
	
	private static final String LIST_ITEM_FRONT_TEXT = "front_text";
	private static final String LIST_ITEM_BACK_TEXT = "back_text";
	
	public FlashcardsListActivity() {
		super();
		
		flashcardsData = new ArrayList<HashMap<String, Object>>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcards);
		
		initializeActionbar();
		initializeFlashcardsList();
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
			ActivityStarter.start(activityContext, FlashcardCreationActivity.class);
		}
	};
	
	private void initializeFlashcardsList() {
		SimpleAdapter flashcardsAdapter = new SimpleAdapter(activityContext, flashcardsData,
			R.layout.flashcards_list_item, new String[] { LIST_ITEM_FRONT_TEXT, LIST_ITEM_BACK_TEXT },
			new int[] { R.id.title, R.id.description });
		
		setListAdapter(flashcardsAdapter);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	
	private void addItemToFlashcardsList(String frontText, String backText) {
		HashMap<String, Object> flashcardItem = new HashMap<String, Object>();
		
		flashcardItem.put(LIST_ITEM_FRONT_TEXT, frontText);
		flashcardItem.put(LIST_ITEM_BACK_TEXT, backText);
		
		flashcardsData.add(flashcardItem);
	}
	
	private void updateFlashcardsList() {
		((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
	}
}
