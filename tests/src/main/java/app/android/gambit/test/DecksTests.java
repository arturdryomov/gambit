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
		decks.addNewDeck("New deck");

		assertEquals(1, decks.getDecksList().size());
	}

	public void testAddDeck() {
		String deckTitle = "New deck";

		Deck newDeck = decks.addNewDeck(deckTitle);

		assertEquals(1, decks.getDecksList().size());
		assertEquals(deckTitle, newDeck.getTitle());
	}

	public void testDeleteDeck() {
		Deck deck = decks.addNewDeck("New deck");

		decks.deleteDeck(deck);

		assertEquals(0, decks.getDecksList().size());
	}

	public void testGetDeckById() {
		Deck deck = decks.addNewDeck("New deck");

		Deck deckById = decks.getDeckById(deck.getId());

		assertEquals(deck, deckById);
	}

	public void testGetDeckByCardId() {
		Deck deck = decks.addNewDeck("New deck");
		long cardId = deck.addNewCard("Front side text", "Back side text").getId();

		Deck deckByCardId = decks.getDeckByCardId(cardId);

		assertEquals(deck, deckByCardId);
	}
}
