package app.android.simpleflashcards.test;


import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.app.Activity;
import android.test.InstrumentationTestCase;
import app.android.gambit.remote.RemoteCard;
import app.android.gambit.remote.RemoteDeck;
import app.android.gambit.remote.RemoteDecks;
import app.android.gambit.remote.SpreadsheetsClient;
import app.android.gambit.ui.AccountSelector;
import app.android.gambit.ui.Authorizer;
import app.android.gambit.ui.DeckCreationActivity;


/**
 * This test needs manual user actions to select account and confirm credentials use.
 * This is ugly and some better approach would be nicely appreciated.
 */

public class RemoteDecksTests extends InstrumentationTestCase
{
	private static String spreadsheetKey;
	private static String token;
	private static Activity hostActivity;

	private RemoteDecks remoteDecks;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ensureAuthorized();
		setUpSpreadsheetKey();
		remoteDecks = new RemoteDecks(token, spreadsheetKey);
	}

	private void ensureAuthorized() {
		if (hostActivity == null) {
			hostActivity = launchActivity("app.android.gambit", DeckCreationActivity.class, null);
		}

		if (token == null) {
			Account account = AccountSelector.select(hostActivity);
			Authorizer authorizer = new Authorizer(hostActivity);
			token = authorizer.getToken(Authorizer.ServiceType.SPREADSHEETS, account);
		}
	}

	private void setUpSpreadsheetKey() {
		ensureAuthorized();

		SpreadsheetsClient client = new SpreadsheetsClient(RemoteDecksTests.token);
		RemoteDecksTests.spreadsheetKey = client.getSpreadsheetFeed().getEntries().get(0).getKey();
	}

	@Override
	protected void tearDown() throws Exception {
		hostActivity.finish();
		super.tearDown();
	}

	public void testSettingDecks() {
		List<RemoteDeck> deckList = buildRemoteDecksList();

		remoteDecks.setDecks(deckList);
		List<RemoteDeck> receivedDeckList = remoteDecks.getDecks();

		assertEquals(deckList, receivedDeckList);
	}

	private List<RemoteDeck> buildRemoteDecksList() {
		final int DECK_COUNT = 2;
		final int CARD_COUNT = 4;

		List<RemoteDeck> decks = new ArrayList<RemoteDeck>();

		for (int deckIndex = 0; deckIndex < DECK_COUNT; deckIndex++) {
			List<RemoteCard> cards = new ArrayList<RemoteCard>();

			for (int cardIndex = 0; cardIndex < CARD_COUNT; cardIndex++) {
				RemoteCard card = new RemoteCard();
				card.setFrontSideText(String.format("Front text for card # %d in deck # %d", cardIndex,
					deckIndex));
				card.setBackSideText(String.format("Back text for card # %d in deck # %d", cardIndex,
					deckIndex));
				cards.add(card);
			}

			RemoteDeck deck = new RemoteDeck();
			deck.setTitle(String.format("Deck # %d", deckIndex));
			deck.setCards(cards);
			decks.add(deck);
		}

		return decks;
	}
}
