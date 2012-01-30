package app.android.simpleflashcards.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public abstract class SimpleAdapterListActivity extends ListActivity
{
	protected ArrayList<HashMap<String, Object>> listData;

	public SimpleAdapterListActivity() {
		super();

		listData = new ArrayList<HashMap<String, Object>>();
	}

	protected abstract void initializeList();

	protected void addItemsToList(List<?> itemsData) {
		for (Object itemData : itemsData) {
			addItemToList(itemData);
		}
	}

	protected abstract void addItemToList(Object itemData);

	protected void updateList() {
		((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
	}
	
	protected void setEmptyListText(String text) {
		TextView textView = (TextView) getListView().getEmptyView();
		
		textView.setText(text);
	}
}
