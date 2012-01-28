package app.android.simpleflashcards.ui;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.simpleflashcards.R;


public class FlashcardsDecksListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private static final String LIST_ITEM_TEXT_ID = "text";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcards_decks);

		initializeActionbar();
		initializeList();
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

	@Override
	protected void initializeList() {
		SimpleAdapter decksAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.flashcards_decks_list_item, new String[] { LIST_ITEM_TEXT_ID },
			new int[] { R.id.text });

		setListAdapter(decksAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
}
