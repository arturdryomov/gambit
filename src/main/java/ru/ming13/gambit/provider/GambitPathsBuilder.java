package ru.ming13.gambit.provider;

final class GambitPathsBuilder
{
	private static final class Segments
	{
		private Segments() {
		}

		public static final String DECKS = "decks";
		public static final String CARDS = "cards";
	}

	public String buildDecksPath() {
		return Segments.DECKS;
	}

	public String buildDeckPath(String deckNumber) {
		return String.format("%s/%s", Segments.DECKS, deckNumber);
	}

	public String buildCardsPath(String deckNumber) {
		return String.format("%s/%s/%s", Segments.DECKS, deckNumber, Segments.CARDS);
	}

	public String buildCardPath(String deckNumber, String cardNumber) {
		return String.format("%s/%s/%s/%s", Segments.DECKS, deckNumber, Segments.CARDS, cardNumber);
	}
}
