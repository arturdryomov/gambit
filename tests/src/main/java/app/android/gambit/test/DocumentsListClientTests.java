package app.android.gambit.test;


import android.accounts.Account;
import android.app.Activity;
import android.test.InstrumentationTestCase;
import app.android.gambit.remote.DocumentEntry;
import app.android.gambit.remote.DocumentEntry.Type;
import app.android.gambit.remote.DocumentFeed;
import app.android.gambit.remote.DocumentsListClient;
import app.android.gambit.ui.AccountSelector;
import app.android.gambit.ui.Authorizer;
import app.android.gambit.ui.DeckCreationActivity;


/**
 * This test needs manual user actions to select account and confirm credentials use.
 * This is ugly and some better approach would be nicely appreciated.
 */

public class DocumentsListClientTests extends InstrumentationTestCase
{
	private static String token;
	private static Activity hostActivity;

	private DocumentsListClient client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ensureAuthorized();
		client = new DocumentsListClient(token);
	}

	private void ensureAuthorized() {
		if (hostActivity == null) {
			hostActivity = launchActivity("app.android.gambit", DeckCreationActivity.class, null);
		}

		if (token == null) {
			Account account = AccountSelector.select(hostActivity);
			Authorizer authorizer = new Authorizer(hostActivity);
			token = authorizer.getToken(Authorizer.ServiceType.DOCUMENTS_LIST, account);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		hostActivity.finish();
		super.tearDown();
	}

	public void testGetDocumentFeed() {
		DocumentFeed feed = client.getDocumentFeed();

		assertNotNull(feed.getEntries());
	}

	public void testGetTypedDocumentFeed() {
		DocumentFeed feed;

		feed = client.getDocumentFeed(Type.DOCUMENT);
		assertNotNull(feed.getEntries());

		feed = client.getDocumentFeed(Type.SPREADSHEET);
		assertNotNull(feed.getEntries());

		feed = client.getDocumentFeed(Type.PRESENTATION);
		assertNotNull(feed.getEntries());

		feed = client.getDocumentFeed(Type.DRAWING);
		assertNotNull(feed.getEntries());
	}

	public void testUploadEmptyDocument() {
		// No exceptions are assumed to be a pass criteria
		client.uploadEmptyDocument(Type.DOCUMENT, "Cool document");
		client.uploadEmptyDocument(Type.SPREADSHEET, "Cool spreadsheet");
	}

	public void testUpdateDocument() {
		client.uploadEmptyDocument(Type.SPREADSHEET, "Cool spreadsheet");

		DocumentEntry entry = client.getDocumentFeed().getEntries().get(0);
		entry.setTitle("Cool new title");

		// No exceptions are assumed to be a pass criteria
		client.updateDocument(entry);
	}
}
