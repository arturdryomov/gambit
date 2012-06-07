package app.android.gambit.test;


import java.util.List;

import app.android.gambit.local.Deck;


public class DecksTests extends DatabaseTestCase
{
	public void testGetDecksList() {
		fillDatabaseWithEmptyDecks();

		List<Deck> decksList = decks.getDecksList();

		assertNotNull(decksList);
	}

	public void testGetDecksCount() {
		decks.createDeck("New deck");

		assertEquals(1, decks.getDecksList().size());
	}

	public void testAddDeck() {
		String deckTitle = "New deck";

		Deck newDeck = decks.createDeck(deckTitle);

		assertEquals(1, decks.getDecksList().size());
		assertEquals(deckTitle, newDeck.getTitle());
	}

	public void testDeleteDeck() {
		Deck deck = decks.createDeck("New deck");

		decks.deleteDeck(deck);

		assertEquals(0, decks.getDecksList().size());
	}
}
