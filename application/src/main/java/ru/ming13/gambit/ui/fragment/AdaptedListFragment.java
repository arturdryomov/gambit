/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.ming13.gambit.ui.fragment;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import ru.ming13.gambit.R;


abstract class AdaptedListFragment<ListItemType> extends SherlockListFragment
{
	protected static final String LIST_ITEM_OBJECT_ID = "object";

	protected List<Map<String, Object>> list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		setRetainInstance(true);

		if (!isListInitialized()) {
			initializeList();
		}
	}

	private boolean isListInitialized() {
		return list != null;
	}

	private void initializeList() {
		list = new ArrayList<Map<String, Object>>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflateFragment(inflater, container);

		setUpListAdapter();

		return fragmentView;
	}

	protected View inflateFragment(LayoutInflater layoutInflater, ViewGroup fragmentContainer) {
		return layoutInflater.inflate(R.layout.fragment_list, fragmentContainer, false);
	}

	private void setUpListAdapter() {
		setListAdapter(buildListAdapter());
	}

	protected abstract SimpleAdapter buildListAdapter();

	protected void populateList(List<ListItemType> listContent) {
		list.clear();

		for (ListItemType listItemContent : listContent) {
			list.add(buildListItem(listItemContent));
		}

		refreshListContent();
	}

	protected abstract Map<String, Object> buildListItem(ListItemType itemObject);

	protected void refreshListContent() {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();
		listAdapter.notifyDataSetChanged();
	}

	protected void setEmptyListText(int textResourceId) {
		TextView emptyListTextView = (TextView) getListView().getEmptyView();

		emptyListTextView.setText(getString(textResourceId));
	}

	protected ListItemType getListItemObject(int listPosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> listItem = (Map<String, Object>) listAdapter.getItem(listPosition);

		return (ListItemType) listItem.get(LIST_ITEM_OBJECT_ID);
	}

	@Override
	public void onResume() {
		super.onResume();

		callListPopulation();
	}

	protected abstract void callListPopulation();

	protected int getListPosition(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo menuItemInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();

		return menuItemInfo.position;
	}
}