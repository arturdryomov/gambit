package ru.ming13.gambit.bus;

import ru.ming13.gambit.model.Card;

public class CardAssembledEvent
{
	private final Card card;

	public CardAssembledEvent(Card card) {
		this.card = card;
	}

	public Card getCard() {
		return card;
	}
}
