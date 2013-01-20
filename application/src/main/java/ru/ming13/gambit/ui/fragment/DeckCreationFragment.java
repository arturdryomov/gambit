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


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.otto.Subscribe;
import org.apache.commons.lang3.StringUtils;
import ru.ming13.gambit.R;
import ru.ming13.gambit.ui.bus.BusProvider;
import ru.ming13.gambit.ui.bus.DeckCreationCancelledEvent;
import ru.ming13.gambit.ui.bus.DeckExistsEvent;
import ru.ming13.gambit.ui.task.DeckCreationTask;


public class DeckCreationFragment extends FormFragment
{
	private String deckTitle;

	public static DeckCreationFragment newInstance() {
		return new DeckCreationFragment();
	}

	@Override
	protected View inflateFragment(LayoutInflater layoutInflater, ViewGroup fragmentContainer) {
		return layoutInflater.inflate(R.layout.fragment_deck_operation, fragmentContainer, false);
	}

	@Override
	protected void readUserDataFromFields() {
		deckTitle = getTextFromEdit(R.id.edit_deck_title);
	}

	@Override
	protected boolean isUserDataCorrect() {
		return isDeckTitleNotEmpty();
	}

	private boolean isDeckTitleNotEmpty() {
		return StringUtils.isNotBlank(deckTitle);
	}

	@Override
	protected void performAcceptAction() {
		DeckCreationTask.execute(getActivity().getContentResolver(), deckTitle);
	}

	@Override
	protected void setUpErrorMessages() {
		if (!isDeckTitleNotEmpty()) {
			setErrorToEdit(R.id.edit_deck_title, R.string.error_empty_field);
		}
	}

	@Subscribe
	public void onDeckExists(DeckExistsEvent deckExistsEvent) {
		setErrorToEdit(R.id.edit_deck_title, R.string.error_deck_already_exists);
	}

	@Override
	protected void performCancelAction() {
		BusProvider.getInstance().post(new DeckCreationCancelledEvent());
	}

	@Override
	public void onResume() {
		super.onResume();

		BusProvider.getInstance().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		BusProvider.getInstance().unregister(this);
	}
}
