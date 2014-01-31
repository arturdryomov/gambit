package ru.ming13.gambit.bus;

public class DeckLoadedEvent implements BusEvent
{
	private final String deckTitle;

	public DeckLoadedEvent(String deckTitle) {
		this.deckTitle = deckTitle;
	}

	public String getDeckTitle() {
		return deckTitle;
	}
}
