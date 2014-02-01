package ru.ming13.gambit.bus;

import ru.ming13.gambit.model.Deck;

public class DeckAssembledEvent implements BusEvent
{
	private final Deck deck;

	public DeckAssembledEvent(Deck deck) {
		this.deck = deck;
	}

	public Deck getDeck() {
		return deck;
	}
}
