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


import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import ru.ming13.gambit.R;
import ru.ming13.gambit.ui.util.AdapterFiller;


abstract class AdaptedListFragment<T> extends SherlockListFragment
{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_list, container, false);

		setUpListAdapter();

		return fragmentView;
	}

	private void setUpListAdapter() {
		setListAdapter(buildListAdapter());
	}

	protected abstract ArrayAdapter buildListAdapter();

	protected void populateList(List<T> listContent) {
		getAdapter().clear();
		AdapterFiller.fill(getAdapter(), listContent);
	}

	@SuppressWarnings("unchecked")
	protected ArrayAdapter<T> getAdapter() {
		return (ArrayAdapter<T>) getListAdapter();
	}

	protected void setEmptyListText(int textResourceId) {
		TextView emptyListTextView = (TextView) getListView().getEmptyView();

		emptyListTextView.setText(getString(textResourceId));
	}

	@Override
	public void onStart() {
		super.onStart();

		callListPopulation();
	}

	protected abstract void callListPopulation();

	protected int getListPosition(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo menuItemInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();

		return menuItemInfo.position;
	}

	protected long getListItemId(MenuItem menuItem) {
		AdapterView.AdapterContextMenuInfo menuItemInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();

		return menuItemInfo.id;
	}
}