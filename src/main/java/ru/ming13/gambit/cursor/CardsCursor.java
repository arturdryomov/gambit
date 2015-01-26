package ru.ming13.gambit.cursor;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.venmo.cursor.IterableCursorWrapper;

import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.provider.GambitContract;

public class CardsCursor extends IterableCursorWrapper<Card>
{
	public CardsCursor(@NonNull Cursor cursor) {
		super(cursor);
	}

	@Override
	public Card peek() {
		long cardId = getLong(GambitContract.Cards._ID, CursorDefaults.LONG);
		String cardFrontSideText = getString(GambitContract.Cards.FRONT_SIDE_TEXT, CursorDefaults.STRING);
		String cardBackSideText = getString(GambitContract.Cards.BACK_SIDE_TEXT, CursorDefaults.STRING);

		return new Card(cardId, cardFrontSideText, cardBackSideText);
	}
}
