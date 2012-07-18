package app.android.gambit.test;


import static app.android.gambit.R.string;

import app.android.gambit.local.Deck;
import app.android.gambit.local.ExampleDeckBuilder;


public class ExampleDeckBuilderTests extends DatabaseTestCase
{
	private ExampleDeckBuilder exampleDeckBuilder;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		exampleDeckBuilder = new ExampleDeckBuilder(getContext());
	}

	public void testBuildDeck() {
		exampleDeckBuilder.buildDeck();

		assertEquals(1, decks.getDecksList().size());

		Deck deck = decks.getDecksList().get(0);
		assertEquals(deck.getTitle(), getContext().getString(string.example_deck_title));

		int cardsListSize = deck.getCardsList().size();
		assertEquals(ExampleDeckBuilder.ANDROID_VERSIONS_RESOURCES.length, cardsListSize);

		for (int cardIndex = 0; cardIndex < cardsListSize; cardIndex++) {
			assertValidCard(deck, cardIndex);
		}
	}

	private void assertValidCard(Deck deck, int cardIndex) {
		String expectedFrontSideText = getContext().getString(
			ExampleDeckBuilder.ANDROID_VERSIONS_RESOURCES[cardIndex]);
		String actualFrontSideText = deck.getCardsList().get(cardIndex).getFrontSideText();

		assertEquals(expectedFrontSideText, actualFrontSideText);
	}
}
