package ru.ming13.gambit.bus;

import android.net.Uri;

public class DeckSavedEvent implements BusEvent
{
	private final Uri deckUri;

	public DeckSavedEvent(Uri deckUri) {
		this.deckUri = deckUri;
	}

	public Uri getDeckUri() {
		return deckUri;
	}
}
