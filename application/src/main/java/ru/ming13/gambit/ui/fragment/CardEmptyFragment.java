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


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import ru.ming13.gambit.R;


public class CardEmptyFragment extends SherlockFragment
{
	public static CardEmptyFragment newInstance() {
		return new CardEmptyFragment();
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup fragmentContainer, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_card, fragmentContainer, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		setCardTextSize();
		setCardText();
	}

	private void setCardTextSize() {
		TextView cardTextView = (TextView) getView().findViewById(R.id.text);

		cardTextView.setTextSize(getResources().getDimension(R.dimen.text_size_empty_card));
	}

	private void setCardText() {
		TextView cardTextView = (TextView) getView().findViewById(R.id.text);
		String text = getString(R.string.empty_cards);

		cardTextView.setText(text);
	}
}
