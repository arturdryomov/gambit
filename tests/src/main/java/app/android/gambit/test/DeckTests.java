package app.android.gambit.test;


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

		deck = decks.createDeck(DECK_TITLE);
	}

	public void testGetId() {
		long deckId = deck.getId();

		assertTrue(deckId >= 0);
	}

	public void testGetTitle() {
		assertEquals(DECK_TITLE, deck.getTitle());
	}

	public void testSetTitle() {
		String newDeckTitle = "New title";

		deck.setTitle(newDeckTitle);

		assertEquals(newDeckTitle, deck.getTitle());
	}

	public void testGetCardsCount() {
		deck.createCard("Front text", "Back text");

		assertEquals(1, deck.getCardsCount());
	}

	public void testGetCardsList() {
		fillDeckWithCards(deck);

		List<Card> cardsList = deck.getCardsList();

		assertNotNull(cardsList);
		assertFalse(cardsList.isEmpty());
	}

	public void testAddCard() {
		String cardFrontSideText = "Front side text";
		String cardBackSideText = "Back side text";

		Card card = deck.createCard(cardFrontSideText, cardBackSideText);

		assertEquals(1, deck.getCardsCount());
		assertEquals(cardFrontSideText, card.getFrontSideText());
		assertEquals(cardBackSideText, card.getBackSideText());
	}

	public void testDeleteCard() {
		Card card = deck.createCard("Front text", "Back text");

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

	public void testGetSetCurrentCardIndex() {
		fillDeckWithCards(deck);
		final int currentCardIndex = 1;

		deck.setCurrentCardIndex(currentCardIndex);

		assertEquals(currentCardIndex, deck.getCurrentCardIndex());
	}

	public void testCurrentCardIndexIsZeroAfterInsertion() {
		deck.setCurrentCardIndex(1);

		deck.createCard("Front side text", "Back side text");

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
