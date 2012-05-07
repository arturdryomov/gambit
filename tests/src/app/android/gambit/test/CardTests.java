package app.android.gambit.test;


import app.android.gambit.local.Card;


public class CardTests extends DatabaseTestCase
{
	private static final String CARD_BACK_SIDE_TEXT = "Back text";
	private static final String CARD_FRONT_SIDE_TEXT = "Front text";
	private static final String DECK_TITLE = "Title";
	private Card card;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		card = decks.addNewDeck(DECK_TITLE).addNewCard(CARD_FRONT_SIDE_TEXT, CARD_BACK_SIDE_TEXT);
	}

	public void testGetFrontSideText() {
		assertEquals(CARD_FRONT_SIDE_TEXT, card.getFrontSideText());
	}

	public void testGetBackSideText() {
		assertEquals(CARD_BACK_SIDE_TEXT, card.getBackSideText());
	}

	public void testSetFrontSideText() {
		String newText = "New front side text";

		card.setFrontSideText(newText);

		assertEquals(newText, card.getFrontSideText());
	}

	public void testSetBackSideText() {
		String newText = "New back side text";

		card.setBackSideText(newText);

		assertEquals(newText, card.getBackSideText());
	}
}
