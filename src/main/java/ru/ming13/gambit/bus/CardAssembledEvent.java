package ru.ming13.gambit.bus;

public class CardAssembledEvent
{
	private final String cardFrontSideText;
	private final String cardBackSideText;

	public CardAssembledEvent(String cardFrontSideText, String cardBackSideText) {
		this.cardFrontSideText = cardFrontSideText;
		this.cardBackSideText = cardBackSideText;
	}

	public String getCardFrontSideText() {
		return cardFrontSideText;
	}

	public String getCardBackSideText() {
		return cardBackSideText;
	}
}
