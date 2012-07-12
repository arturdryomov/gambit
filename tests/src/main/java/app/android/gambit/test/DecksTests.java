package app.android.gambit.test;


import java.util.List;

import app.android.gambit.local.Deck;


public class DecksTests extends DatabaseTestCase
{
	private static final String DECK_TITLE = "deck";

	public void testGetDecksList() {
		fillDatabaseWithEmptyDecks();

		List<Deck> decksList = decks.getDecksList();

		assertNotNull(decksList);
	}

	public void testGetDecksCount() {
		decks.createDeck(DECK_TITLE);

		assertEquals(1, decks.getDecksList().size());
	}

	public void testAddDeck() {
		Deck deck = decks.createDeck(DECK_TITLE);

		assertEquals(1, decks.getDecksList().size());
		assertEquals(DECK_TITLE, deck.getTitle());
	}

	public void testDeleteDeck() {
		Deck deck = decks.createDeck(DECK_TITLE);

		decks.deleteDeck(deck);

		assertEquals(0, decks.getDecksList().size());
	}

	public void testClearDecks() {
		fillDatabaseWithEmptyDecks();

		decks.clear();

		assertEquals(0, decks.getDecksList().size());
	}
}
