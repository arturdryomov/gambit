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


abstract class TwoLineListAdapter<T> extends ArrayAdapter<T>
{
	private static final class ViewHolder
	{
		public TextView firstLineText;
		public TextView secondLineText;
	}

	private final LayoutInflater layoutInflater;

	public TwoLineListAdapter(Context context) {
		super(context, R.layout.list_item_two_line);

		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItemView = getView(convertView, parent);
		ViewHolder viewHolder = getViewHolder(listItemView);

		viewHolder.firstLineText.setText(buildListItemFirstLineText(position));
		viewHolder.secondLineText.setText(buildListItemSecondLineText(position));

		return listItemView;
	}

	private View getView(View convertView, ViewGroup parent) {
		if (convertView != null) {
			return convertView;
		}

		return layoutInflater.inflate(R.layout.list_item_two_line, parent, false);
	}

	protected abstract String buildListItemFirstLineText(int position);

	protected abstract String buildListItemSecondLineText(int position);

	private ViewHolder getViewHolder(View view) {
		if (view.getTag() != null) {
			return (ViewHolder) view.getTag();
		}

		return buildViewHolder(view);
	}

	private ViewHolder buildViewHolder(View view) {
		ViewHolder viewHolder = new ViewHolder();

		viewHolder.firstLineText = (TextView) view.findViewById(R.id.text_first_line);
		viewHolder.secondLineText = (TextView) view.findViewById(R.id.text_second_line);

		return viewHolder;
	}
}
