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

package ru.ming13.gambit.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.CardCreationCalledEvent;


public class CardEmptyFragment extends Fragment implements View.OnClickListener
{
	public static CardEmptyFragment newInstance() {
		return new CardEmptyFragment();
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup fragmentContainer, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_card_empty, fragmentContainer, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpCardListener();
	}

	private void setUpCardListener() {
		getCreateCardsButton().setOnClickListener(this);
	}

	private Button getCreateCardsButton() {
		return (Button) getView().findViewById(R.id.button_create_cards);
	}

	@Override
	public void onClick(View view) {
		BusProvider.getBus().post(new CardCreationCalledEvent());
	}
}
