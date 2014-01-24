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

public class CardsAdapter extends CursorAdapter
{
	private final LayoutInflater layoutInflater;

	public CardsAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cardsCursor, ViewGroup viewGroup) {
		View cardView = buildCardView(viewGroup);

		setUpCardInformation(context, cardsCursor, cardView);

		return cardView;
	}

	private View buildCardView(ViewGroup viewGroup) {
		return layoutInflater.inflate(R.layout.list_item, viewGroup, false);
	}

	private void setUpCardInformation(Context context, Cursor cardsCursor, View cardView) {
		TextView cardTextView = (TextView) cardView;

		String cardFrontSideText = getCardFrontSideText(cardsCursor);
		String cardBackSideText = getCardBackSideText(cardsCursor);

		cardTextView.setText(context.getString(R.string.mask_card_list_item, cardFrontSideText, cardBackSideText));
	}

	private String getCardFrontSideText(Cursor cardsCursor) {
		return cardsCursor.getString(cardsCursor.getColumnIndex(GambitContract.Cards.FRONT_SIDE_TEXT));
	}

	private String getCardBackSideText(Cursor cardsCursor) {
		return cardsCursor.getString(cardsCursor.getColumnIndex(GambitContract.Cards.BACK_SIDE_TEXT));
	}

	@Override
	public void bindView(View cardView, Context context, Cursor cardsCursor) {
		setUpCardInformation(context, cardsCursor, cardView);
	}
}
