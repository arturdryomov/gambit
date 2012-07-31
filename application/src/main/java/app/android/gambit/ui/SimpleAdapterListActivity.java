package app.android.gambit.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListActivity;


abstract class SimpleAdapterListActivity extends SherlockListActivity
{
	protected final List<HashMap<String, Object>> listData;
	protected static final String LIST_ITEM_OBJECT_ID = "object";

	public SimpleAdapterListActivity() {
		super();

		listData = new ArrayList<HashMap<String, Object>>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initializeList();
	}

	protected abstract void initializeList();

	protected void fillList(List<?> itemsData) {
		listData.clear();

		for (Object itemData : itemsData) {
			addItemToList(itemData);
		}

		updateList();
	}

	protected abstract void addItemToList(Object itemData);

	protected void updateList() {
		((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
	}

	protected void setEmptyListText(int textId) {
		TextView emptyListSign = (TextView) getListView().getEmptyView();
		String text = getString(textId);

		emptyListSign.setText(text);
	}

	protected Object getObject(int listPosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) listAdapter.getItem(listPosition);

		return adapterItem.get(LIST_ITEM_OBJECT_ID);
	}
}
