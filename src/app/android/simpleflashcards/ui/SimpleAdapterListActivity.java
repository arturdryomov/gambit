package app.android.simpleflashcards.ui;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.widget.SimpleAdapter;


public abstract class SimpleAdapterListActivity extends ListActivity
{
	protected ArrayList<HashMap<String, Object>> listData;

	public SimpleAdapterListActivity() {
		super();

		listData = new ArrayList<HashMap<String, Object>>();
	}

	protected abstract void initializeList();

	// TODO: Use it when models will be done
	// protected abstract void addItemToList(Object itemData);

	protected void updateList() {
		((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
	}
}
