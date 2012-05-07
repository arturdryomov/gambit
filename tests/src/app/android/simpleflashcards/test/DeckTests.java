package app.android.simpleflashcards.test;


import java.util.List;

import android.test.suitebuilder.annotation.Suppress;
import app.android.gambit.local.Card;
import app.android.gambit.local.Deck;


public class DeckTests extends DatabaseTestCase
{
	private static final String DECK_TITLE = "Title";
	private Deck deck;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		deck = decks.addNewDeck(DECK_TITLE);
	}

	public void testGetId() {
		long id = deck.getId();
		assertTrue(id >= 0);
	}

	public void testGetTitle() {
		assertEquals(DECK_TITLE, deck.getTitle());
	}

	public void testSetTitle() {
		String newTitle = "New title";

		deck.setTitle(newTitle);

		assertEquals(newTitle, deck.getTitle());
	}

	public void testGetCardsCount() {
		deck.addNewCard("Front text", "Back text");

		assertEquals(1, deck.getCardsCount());
	}

	public void testGetCardsList() {
		fillDeckWithCards(deck);

		List<Card> cardsList = deck.getCardsList();

		assertNotNull(cardsList);
		assertFalse(cardsList.isEmpty());
	}

	public void testAddCard() {
		String frontSideText = "Front side text";
		String backSideText = "Back side text";

		Card newCard = deck.addNewCard(frontSideText, backSideText);

		assertEquals(1, deck.getCardsCount());
		assertEquals(frontSideText, newCard.getFrontSideText());
		assertEquals(backSideText, newCard.getBackSideText());
	}

	public void testDeleteCard() {
		Card card = deck.addNewCard("Front text", "Back text");

		deck.deleteCard(card);

		assertEquals(0, deck.getCardsCount());
	}

	public void testResetCardsOrder() {
		fillDeckWithCards(deck);

		// If no exception thrown, assume everything's fine
		// TODO: Invent a smarter test case
		deck.resetCardsOrder();
	}

	@Suppress
	public void testShuffleCardsOrder() {
		fillDeckWithCards(deck);

		// If no exception thrown, assume everything's fine
		// TODO: Invent a smarter test case
		deck.shuffleCards();
	}

	public void testGetCardById() {
		Card justCard = deck.addNewCard("Front text", "Back text");

		Card cardById = deck.getCardById(justCard.getId());

		assertEquals(justCard, cardById);
	}

	public void testGetSetCurrentCardIndex() {
		fillDeckWithCards(deck);
		int index = 1;

		deck.setCurrentCardIndex(index);

		assertEquals(index, deck.getCurrentCardIndex());
	}

	public void testCurrentCardIndexIsZeroAfterInsertion() {
		deck.setCurrentCardIndex(1);

		deck.addNewCard("Front side text", "Back side text");

		assertEquals(0, deck.getCurrentCardIndex());
	}

	public void testCurrentCardIndexIsInvalidOnEmptyDeck() {
		fillDeckWithCards(deck);
		deck.setCurrentCardIndex(1);

		for (Card card : deck.getCardsList()) {
			deck.deleteCard(card);
		}

		assertEquals(Deck.INVALID_CURRENT_CARD_INDEX, deck.getCurrentCardIndex());
	}
}
