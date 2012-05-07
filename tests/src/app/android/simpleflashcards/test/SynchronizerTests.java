package app.android.simpleflashcards.test;


import java.util.List;

import android.accounts.Account;
import android.app.Activity;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.Suppress;
import app.android.gambit.local.Card;
import app.android.gambit.local.DbProvider;
import app.android.gambit.local.Deck;
import app.android.gambit.local.Decks;
import app.android.gambit.remote.Synchronizer;
import app.android.gambit.ui.AccountSelector;
import app.android.gambit.ui.Authorizer;
import app.android.gambit.ui.DeckCreationActivity;


/**
 * This test needs manual user actions to select account and confirm credentials use.
 * This is ugly and some better approach would be nicely appreciated.
 */

public class SynchronizerTests extends InstrumentationTestCase
{
	private static String spreadsheetsToken;
	private static String googleDocsToken;
	private static Activity hostActivity;

	private Synchronizer synchronizer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ensureAuthorized();
		DbProvider.getInstance(hostActivity);

		synchronizer = new Synchronizer();
	}

	private void ensureAuthorized() {
		if (hostActivity == null) {
			hostActivity = launchActivity("app.android.gambit", DeckCreationActivity.class, null);
		}

		if (googleDocsToken == null) {
			Account account = AccountSelector.select(hostActivity);
			Authorizer authorizer = new Authorizer(hostActivity);
			googleDocsToken = authorizer.getToken(Authorizer.ServiceType.DOCUMENTS_LIST, account);
		}

		if (spreadsheetsToken == null) {
			Account account = AccountSelector.select(hostActivity);
			Authorizer authorizer = new Authorizer(hostActivity);
			spreadsheetsToken = authorizer.getToken(Authorizer.ServiceType.SPREADSHEETS, account);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		hostActivity.finish();
		super.tearDown();
	}

	@Suppress
	public void testCreateSpreadsheet() {
		// No exceptions are assumed as passing criteria
		synchronizer.createSpreadsheet("Test title", googleDocsToken);
	}

	public void testSynchronize() {
		Decks decks = DbProvider.getInstance().getDecks();

		decks.beginTransaction();

		initDatabase();
		String key = synchronizer.createSpreadsheet("Test title", googleDocsToken);

		decks.beginTransaction();

		// Sync from local to remote
		decks.setCurrentDateTimeAsLastUpdated();
		synchronizer.synchronize(key, spreadsheetsToken);

		decks.endTransaction();

		// Sync from remote to local
		synchronizer.synchronize(key, spreadsheetsToken);

		// Make sure new local data is correct
		List<Deck> deckList = decks.getDecksList();

		for (int deckIndex = 0; deckIndex < deckList.size(); deckIndex++) {

			assertEquals(buildDeckTitle(deckIndex), deckList.get(deckIndex).getTitle());

			List<Card> cardList = deckList.get(deckIndex).getCardsList();

			for (int cardIndex = 0; cardIndex < cardList.size(); cardIndex++) {
				assertEquals(buildCardFrontSideText(deckIndex, cardIndex), cardList.get(cardIndex)
					.getFrontSideText());
				assertEquals(buildCardBackSideText(deckIndex, cardIndex), cardList.get(cardIndex)
					.getBackSideText());
			}
		}

		decks.endTransaction();
	}

	private void initDatabase() {
		final int DECK_COUNT = 1;
		final int CARD_COUNT = 3;

		Decks decks = DbProvider.getInstance().getDecks();
		decks.clear();

		for (int deckIndex = 0; deckIndex < DECK_COUNT; deckIndex++) {
			Deck deck = decks.addNewDeck(buildDeckTitle(deckIndex));

			for (int cardIndex = 0; cardIndex < CARD_COUNT; cardIndex++) {
				deck.addNewCard(buildCardFrontSideText(deckIndex, cardIndex),
					buildCardBackSideText(deckIndex, cardIndex));
			}
		}
	}

	private String buildDeckTitle(int index) {
		return String.format("Deck %s", index);
	}

	private String buildCardFrontSideText(int deckIndex, int cardIndex) {
		return String.format("Front %s of %s", cardIndex, deckIndex);
	}

	private String buildCardBackSideText(int deckIndex, int cardIndex) {
		return String.format("Back %s of %s", cardIndex, deckIndex);
	}
}
