package ru.ming13.gambit.model;

public class Card
{
	private final String frontSideText;
	private final String backSideText;

	public Card(String frontSideText, String backSideText) {
		this.frontSideText = frontSideText;
		this.backSideText = backSideText;
	}

	public String getFrontSideText() {
		return frontSideText;
	}

	public String getBackSideText() {
		return backSideText;
	}
}
