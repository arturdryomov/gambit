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


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.otto.Subscribe;
import org.apache.commons.lang3.StringUtils;
import ru.ming13.gambit.R;
import ru.ming13.gambit.ui.bus.BusProvider;
import ru.ming13.gambit.ui.bus.DeckExistsEvent;
import ru.ming13.gambit.ui.bus.DeckQueriedEvent;
import ru.ming13.gambit.ui.bus.DeckRenamedEvent;
import ru.ming13.gambit.ui.bus.DeckRenamingCancelledEvent;
import ru.ming13.gambit.ui.task.DeckQueryingTask;
import ru.ming13.gambit.ui.task.DeckRenamingTask;


public class DeckRenamingFragment extends FormFragment
{
	private Uri deckUri;

	private String deckOriginalTitle;
	private String deckTitle;

	public static DeckRenamingFragment newInstance(Uri deckUri) {
		DeckRenamingFragment deckRenamingFragment = new DeckRenamingFragment();

		deckRenamingFragment.setArguments(buildArguments(deckUri));

		return deckRenamingFragment;
	}

	private static Bundle buildArguments(Uri deckUri) {
		Bundle bundle = new Bundle();

		bundle.putParcelable(FragmentArguments.DECK_URI, deckUri);

		return bundle;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		deckUri = getArguments().getParcelable(FragmentArguments.DECK_URI);
	}

	@Override
	public void onStart() {
		super.onStart();

		queryDeckOriginalTitle();
	}

	private void queryDeckOriginalTitle() {
		DeckQueryingTask.execute(getActivity().getContentResolver(), deckUri);
	}

	@Subscribe
	public void onDeckQueried(DeckQueriedEvent deckQueriedEvent) {
		deckOriginalTitle = deckQueriedEvent.getDeckTitle();

		setUpDeckOriginalTitle();
	}

	private void setUpDeckOriginalTitle() {
		if (StringUtils.isBlank(getTextFromEdit(R.id.edit_deck_title))) {
			setTextToEdit(R.id.edit_deck_title, deckOriginalTitle);
		}
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
		return StringUtils.isNotBlank(deckTitle);
	}

	@Override
	protected void performAcceptAction() {
		if (isDeckTitleNotChanged()) {
			BusProvider.getInstance().post(new DeckRenamedEvent());
			return;
		}

		DeckRenamingTask.execute(getActivity().getContentResolver(), deckUri, deckTitle);
	}

	private boolean isDeckTitleNotChanged() {
		return deckTitle.equals(deckOriginalTitle);
	}

	@Override
	protected void setUpErrorMessages() {
		if (StringUtils.isBlank(deckTitle)) {
			setErrorToEdit(R.id.edit_deck_title, R.string.error_empty_field);
		}
	}

	@Subscribe
	public void onDeckExists(DeckExistsEvent deckExistsEvent) {
		setErrorToEdit(R.id.edit_deck_title, R.string.error_deck_already_exists);
	}

	@Override
	protected void performCancelAction() {
		BusProvider.getInstance().post(new DeckRenamingCancelledEvent());
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
