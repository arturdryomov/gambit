package app.android.simpleflashcards.test;


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

		assertEquals(1, decks.getDecksCount());
	}

	public void testAddDeck() {
		String title = "New deck";

		Deck newDeck = decks.addNewDeck(title);

		assertEquals(1, decks.getDecksCount());
		assertEquals(title, newDeck.getTitle());
	}

	public void testDeleteDeck() {
		Deck newDeck = decks.addNewDeck("New deck");

		decks.deleteDeck(newDeck);

		assertEquals(0, decks.getDecksCount());
	}

	public void testGetDeckById() {
		Deck justDeck = decks.addNewDeck("New deck");

		Deck deckById = decks.getDeckById(justDeck.getId());

		assertEquals(justDeck, deckById);
	}

	public void testGetDeckByCardId() {
		Deck justDeck = decks.addNewDeck("New deck");
		long cardId = justDeck.addNewCard("Front side text", "Back side text").getId();

		Deck deckByCardId = decks.getDeckByCardId(cardId);

		assertEquals(justDeck, deckByCardId);
	}
}
