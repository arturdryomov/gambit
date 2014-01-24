package ru.ming13.gambit.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ru.ming13.gambit.R;
import ru.ming13.gambit.provider.GambitContract;

public class DecksAdapter extends CursorAdapter
{
	private final LayoutInflater layoutInflater;

	public DecksAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor decksCursor, ViewGroup viewGroup) {
		View deckView = buildDeckView(viewGroup);

		setUpDeckInformation(decksCursor, deckView);

		return deckView;
	}

	private View buildDeckView(ViewGroup viewGroup) {
		return layoutInflater.inflate(R.layout.list_item, viewGroup, false);
	}

	private void setUpDeckInformation(Cursor decksCursor, View deckView) {
		TextView deckTextView = (TextView) deckView;
		String deckTitle = getDeckTitle(decksCursor);

		deckTextView.setText(deckTitle);
	}

	private String getDeckTitle(Cursor decksCursor) {
		return decksCursor.getString(decksCursor.getColumnIndex(GambitContract.Decks.TITLE));
	}

	@Override
	public void bindView(View deckView, Context context, Cursor decksCursor) {
		setUpDeckInformation(decksCursor, deckView);
	}
}
