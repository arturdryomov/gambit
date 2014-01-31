package ru.ming13.gambit.bus;

public class DeckAssembledEvent implements BusEvent
{
	private final String deckTitle;

	public DeckAssembledEvent(String deckTitle) {
		this.deckTitle = deckTitle;
	}

	public String getDeckTitle() {
		return deckTitle;
	}
}
