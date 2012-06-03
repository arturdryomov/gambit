package app.android.gambit.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListActivity;


abstract class SimpleAdapterListActivity extends SherlockListActivity
{
	protected final List<HashMap<String, Object>> listData;

	public SimpleAdapterListActivity() {
		super();

		listData = new ArrayList<HashMap<String, Object>>();
	}

	protected abstract void initializeList();

	protected void fillList(List<?> itemsData) {
		listData.clear();

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
