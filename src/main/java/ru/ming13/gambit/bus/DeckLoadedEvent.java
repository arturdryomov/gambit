package ru.ming13.gambit.bus;

import ru.ming13.gambit.model.Deck;

public class DeckLoadedEvent implements BusEvent
{
	private final Deck deck;

	public DeckLoadedEvent(Deck deck) {

		this.deck = deck;
	}

	public Deck getDeck() {
		return deck;
	}
}
