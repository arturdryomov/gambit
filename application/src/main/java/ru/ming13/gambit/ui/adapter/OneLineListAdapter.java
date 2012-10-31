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

package ru.ming13.gambit.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.ming13.gambit.R;


abstract class OneLineListAdapter<T> extends ArrayAdapter<T>
{
	private final LayoutInflater layoutInflater;

	public OneLineListAdapter(Context context) {
		super(context, R.layout.list_item_one_line);

		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView listItemText = (TextView) getView(convertView, parent);

		listItemText.setText(buildListItemText(position));

		return listItemText;
	}

	protected abstract String buildListItemText(int position);

	private View getView(View convertView, ViewGroup parent) {
		if (convertView != null) {
			return convertView;
		}

		return layoutInflater.inflate(R.layout.list_item_one_line, parent, false);
	}
}
