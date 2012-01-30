package app.android.simpleflashcards.ui;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.simpleflashcards.R;


public class CardsListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private static final String LIST_ITEM_FRONT_TEXT = "front_text";
	private static final String LIST_ITEM_BACK_TEXT = "back_text";

	private int deckId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards);

		processActivityMessage();

		initializeActionbar();
		initializeList();
	}

	private void processActivityMessage() {
		deckId = ActivityMessager.getMessageFromActivity(this);
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
			ActivityMessager
				.startActivityWithMessage(activityContext, CardCreationActivity.class, deckId);
		}
	};

	@Override
	protected void initializeList() {
		SimpleAdapter flashcardsAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.cards_list_item, new String[] { LIST_ITEM_FRONT_TEXT, LIST_ITEM_BACK_TEXT },
			new int[] { R.id.title, R.id.description });

		setListAdapter(flashcardsAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	protected void addItemToList(Object itemData) {
		// TODO Auto-generated method stub

	}
}
