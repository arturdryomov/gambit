package ru.ming13.gambit.fragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import ru.ming13.gambit.R;
import ru.ming13.gambit.bus.BusProvider;
import ru.ming13.gambit.bus.CardAssembledEvent;
import ru.ming13.gambit.bus.CardLoadedEvent;
import ru.ming13.gambit.bus.OperationSavedEvent;
import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.task.CardLoadingTask;
import ru.ming13.gambit.util.Fragments;

public class CardOperationFragment extends Fragment
{
	public static CardOperationFragment newInstance() {
		return newInstance(null);
	}

	public static CardOperationFragment newInstance(Uri cardUri) {
		CardOperationFragment fragment = new CardOperationFragment();

		fragment.setArguments(buildArguments(cardUri));

		return fragment;
	}

	private static Bundle buildArguments(Uri cardUri) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(Fragments.Arguments.URI, cardUri);

		return arguments;
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup fragmentContainer, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_card_operation, fragmentContainer, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpCard();
	}

	private void setUpCard() {
		if (isCardUriCorrect()) {
			CardLoadingTask.execute(getActivity().getContentResolver(), getCardUri());
		}
	}

	private boolean isCardUriCorrect() {
		return getCardUri() != null;
	}

	private Uri getCardUri() {
		return getArguments().getParcelable(Fragments.Arguments.URI);
	}

	@Subscribe
	public void onCardLoaded(CardLoadedEvent event) {
		setUpCard(event.getCard());
	}

	private void setUpCard(Card card) {
		getCardFrontSideTextView().append(card.getFrontSideText());
		getCardBackSideTextView().append(card.getBackSideText());
	}

	private TextView getCardFrontSideTextView() {
		return (TextView) getView().findViewById(R.id.edit_front_side_text);
	}

	private TextView getCardBackSideTextView() {
		return (TextView) getView().findViewById(R.id.edit_back_side_text);
	}

	@Subscribe
	public void onOperationSaved(OperationSavedEvent event) {
		saveCard();
	}

	private void saveCard() {
		if (isCardCorrect()) {
			assembleCard();
		} else {
			showErrorMessage();
		}
	}

	private boolean isCardCorrect() {
		return !getCardFrontSideText().isEmpty() && !getCardBackSideText().isEmpty();
	}

	private String getCardFrontSideText() {
		return getCardFrontSideTextView().getText().toString().trim();
	}

	private String getCardBackSideText() {
		return getCardBackSideTextView().getText().toString().trim();
	}

	private void assembleCard() {
		Card card = new Card(getCardFrontSideText(), getCardBackSideText());

		BusProvider.getBus().post(new CardAssembledEvent(card));
	}

	private void showErrorMessage() {
		if (getCardFrontSideText().isEmpty()) {
			getCardFrontSideTextView().setError(getString(R.string.error_empty_field));
		}

		if (getCardBackSideText().isEmpty()) {
			getCardBackSideTextView().setError(getString(R.string.error_empty_field));
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}
}
