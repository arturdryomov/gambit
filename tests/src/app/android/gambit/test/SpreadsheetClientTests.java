package app.android.gambit.test;


import android.accounts.Account;
import android.app.Activity;
import android.test.InstrumentationTestCase;
import app.android.gambit.remote.CellFeed;
import app.android.gambit.remote.SpreadsheetFeed;
import app.android.gambit.remote.SpreadsheetsClient;
import app.android.gambit.remote.WorksheetEntry;
import app.android.gambit.remote.WorksheetFeed;
import app.android.gambit.ui.AccountSelector;
import app.android.gambit.ui.Authorizer;
import app.android.gambit.ui.DeckCreationActivity;


/**
 * This test needs manual user actions to select account and confirm credentials use.
 * This is ugly and some better approach would be nicely appreciated.
 */

public class SpreadsheetClientTests extends InstrumentationTestCase
{
	private static String token;
	private static Activity hostActivity;

	private SpreadsheetsClient client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ensureAuthorized();
		client = new SpreadsheetsClient(SpreadsheetClientTests.token);
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

	@Override
	protected void tearDown() throws Exception {
		hostActivity.finish();
		super.tearDown();
	}

	public void testGetSpreadsheetFeed() {
		SpreadsheetFeed feed = client.getSpreadsheetFeed();

		assertNotNull(feed.getEntries());
	}

	public void testGetWorksheetFeed() {
		SpreadsheetFeed spreadsheets = client.getSpreadsheetFeed();
		WorksheetFeed worksheets = client.getWorksheetFeed(spreadsheets.getEntries().get(0));

		assertNotNull(worksheets.getEntries());
	}

	public void testGetCellFeed() {
		SpreadsheetFeed spreadsheets = client.getSpreadsheetFeed();
		WorksheetFeed worksheets = client.getWorksheetFeed(spreadsheets.getEntries().get(0));
		CellFeed cells = client.getCellFeed(worksheets.getEntries().get(0));

		assertNotNull(cells.getEntries());
	}

	public void testAddWorksheet() {
		SpreadsheetFeed spreadsheets = client.getSpreadsheetFeed();
		WorksheetFeed worksheets = client.getWorksheetFeed(spreadsheets.getEntries().get(0));
		int oldWorksheetsCount = worksheets.getEntries().size();

		client.insertWorksheet(spreadsheets.getEntries().get(0),
			String.format("Worksheet %d", oldWorksheetsCount), 100, 40);

		worksheets = client.getWorksheetFeed(spreadsheets.getEntries().get(0));
		int newWorksheetsCount = worksheets.getEntries().size();

		assertEquals(1, newWorksheetsCount - oldWorksheetsCount);
	}

	public void testDeleteWorksheet() {
		SpreadsheetFeed spreadsheets = client.getSpreadsheetFeed();
		WorksheetFeed worksheets = client.getWorksheetFeed(spreadsheets.getEntries().get(0));
		int oldWorksheetsCount = worksheets.getEntries().size();

		client.deleteWorksheet(worksheets.getEntries().get(oldWorksheetsCount - 1));

		worksheets = client.getWorksheetFeed(spreadsheets.getEntries().get(0));
		int newWorksheetsCount = worksheets.getEntries().size();

		assertEquals(1, oldWorksheetsCount - newWorksheetsCount);
	}

	public void testUpdateWorksheet() {
		SpreadsheetFeed spreadsheets = client.getSpreadsheetFeed();
		WorksheetFeed worksheets = client.getWorksheetFeed(spreadsheets.getEntries().get(0));
		WorksheetEntry worksheet = worksheets.getEntries().get(0);

		updateString(worksheet);
		client.updateWorksheet(worksheet);

		worksheets = client.getWorksheetFeed(spreadsheets.getEntries().get(0));

		assertEquals(worksheet.getTitle(), worksheets.getEntries().get(0).getTitle());
	}

	private void updateString(WorksheetEntry worksheet) {
		final String UPDATE_MARK = "<updated> ";

		if (worksheet.getTitle().startsWith(UPDATE_MARK)) {
			worksheet.setTitle(worksheet.getTitle().substring(UPDATE_MARK.length()));
		}
		else {
			worksheet.setTitle(UPDATE_MARK + worksheet.getTitle());
		}
	}

	public void testClearWorksheet() {
		SpreadsheetFeed spreadsheets = client.getSpreadsheetFeed();
		WorksheetFeed worksheets = client.getWorksheetFeed(spreadsheets.getEntries().get(0));
		WorksheetEntry worksheet = worksheets.getEntries().get(0);

		// Insert some data
		client.updateCell(worksheet, 1, 1, "Some data");

		// Clear
		client.clearWorksheet(worksheet);

		// Ensure no data
		CellFeed cells = client.getCellFeed(worksheet);

		assertTrue(cells.getEntries().isEmpty());
	}
}
