package app.android.gambit.test;


import app.android.gambit.local.Card;


public class CardTests extends DatabaseTestCase
{
	private static final String DECK_TITLE = "deck";
	private static final String CARD_BACK_SIDE_TEXT = "back side text";
	private static final String CARD_FRONT_SIDE_TEXT = "front side text";

	private Card card;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		card = decks.createDeck(DECK_TITLE).createCard(CARD_FRONT_SIDE_TEXT, CARD_BACK_SIDE_TEXT);
	}

	public void testGetId() {
		long cardId = card.getId();

		assertTrue(cardId >= 0);
	}

	public void testGetFrontSideText() {
		assertEquals(CARD_FRONT_SIDE_TEXT, card.getFrontSideText());
	}

	public void testGetBackSideText() {
		assertEquals(CARD_BACK_SIDE_TEXT, card.getBackSideText());
	}

	public void testSetFrontSideText() {
		String newFrontSideText = String.format("new %s", CARD_FRONT_SIDE_TEXT);

		card.setFrontSideText(newFrontSideText);

		assertEquals(newFrontSideText, card.getFrontSideText());
	}

	public void testSetBackSideText() {
		String newBackSideText = String.format("new %s", CARD_BACK_SIDE_TEXT);

		card.setBackSideText(newBackSideText);

		assertEquals(newBackSideText, card.getBackSideText());
	}
}
