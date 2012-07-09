package app.android.gambit.test;


import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;
import app.android.gambit.local.DbProvider;
import app.android.gambit.local.Deck;
import app.android.gambit.local.Decks;


@Suppress
public abstract class DatabaseTestCase extends AndroidTestCase
{
	private static final int DECKS_COUNT = 5;
	private static final int CARDS_COUNT = 5;

	protected Decks decks;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		decks = DbProvider.getInstance(getContext()).getDecks();
		decks.beginTransaction();

		emptyDatabase();
	}

	protected void emptyDatabase() {
		for (Deck deck : decks.getDecksList()) {
			decks.deleteDeck(deck);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		decks.endTransaction();

		super.tearDown();
	}

	protected void fillDatabaseWithEmptyDecks() {
		if (decks.getDecksList().size() != 0) {
			return;
		}

		for (int deckIndex = 1; deckIndex <= DECKS_COUNT; deckIndex++) {
			decks.createDeck(String.format("Deck %d", deckIndex));
		}
	}

	protected void fillDeckWithCards(Deck deck) {
		if (deck.getCardsCount() != 0) {
			return;
		}

		for (int cardIndex = 1; cardIndex <= CARDS_COUNT; cardIndex++) {
			String frontSideText = String.format("Card %d in deck %d front", cardIndex, deck.getId());
			String backSideText = String.format("Card %d in deck %d back", cardIndex, deck.getId());

			deck.createCard(frontSideText, backSideText);
		}
	}

	protected void fillDatabase() {
		for (int deckIndex = 1; deckIndex <= DECKS_COUNT; deckIndex++) {
			Deck newDeck = decks.createDeck(String.format("Deck %s", deckIndex + 1));

			for (int cardIndex = 1; cardIndex <= CARDS_COUNT; cardIndex++) {
				String frontSideText = String.format("Card %s in deck %s front", cardIndex, deckIndex);
				String backSideText = String.format("Card %s in deck %s back", cardIndex, deckIndex);

				newDeck.createCard(frontSideText, backSideText);
			}
		}
	}
}
