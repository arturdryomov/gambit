package ru.ming13.gambit.cursor;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.venmo.cursor.IterableCursorWrapper;

import ru.ming13.gambit.model.Deck;
import ru.ming13.gambit.provider.GambitContract;

public class DecksCursor extends IterableCursorWrapper<Deck>
{
	public DecksCursor(@NonNull Cursor cursor) {
		super(cursor);
	}

	@Override
	public Deck peek() {
		long deckId = getLong(GambitContract.Decks._ID, CursorDefaults.LONG);
		String deckTitle = getString(GambitContract.Decks.TITLE, CursorDefaults.STRING);
		int deckCurrentCardPosition = getInteger(GambitContract.Decks.CURRENT_CARD_INDEX, CursorDefaults.INT);

		return new Deck(deckId, deckTitle, deckCurrentCardPosition);
	}
}
