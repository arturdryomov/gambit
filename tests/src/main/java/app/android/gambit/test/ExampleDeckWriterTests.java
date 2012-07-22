package app.android.gambit.test;


import static app.android.gambit.R.string;

import app.android.gambit.local.Deck;
import app.android.gambit.local.ExampleDeckWriter;


public class ExampleDeckWriterTests extends DatabaseTestCase
{
	private ExampleDeckWriter exampleDeckWriter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		exampleDeckWriter = new ExampleDeckWriter(getContext(), decks);
	}

	public void testBuildDeck() {
		exampleDeckWriter.writeDeck();

		assertEquals(1, decks.getDecksList().size());

		Deck deck = decks.getDecksList().get(0);
		assertTrue(deck.getTitle().startsWith(getContext().getString(string.example_deck_title)));

		int cardsListSize = deck.getCardsList().size();
		assertEquals(ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES.length, cardsListSize);

		for (int cardIndex = 0; cardIndex < cardsListSize; cardIndex++) {
			assertValidCard(deck, cardIndex);
		}
	}

	private void assertValidCard(Deck deck, int cardIndex) {
		String expectedFrontSideText = getContext().getString(
			ExampleDeckWriter.ANDROID_VERSIONS_RESOURCES[cardIndex]);
		String actualFrontSideText = deck.getCardsList().get(cardIndex).getFrontSideText();

		assertEquals(expectedFrontSideText, actualFrontSideText);
	}
}
