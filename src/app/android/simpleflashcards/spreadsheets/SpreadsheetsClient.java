package app.android.simpleflashcards.spreadsheets;


import java.io.IOException;

import app.android.simpleflashcards.spreadsheets.models.CellFeed;
import app.android.simpleflashcards.spreadsheets.models.SpreadsheetEntry;
import app.android.simpleflashcards.spreadsheets.models.SpreadsheetFeed;
import app.android.simpleflashcards.spreadsheets.models.WorksheetEntry;
import app.android.simpleflashcards.spreadsheets.models.WorksheetFeed;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.xml.XmlNamespaceDictionary;


public class SpreadsheetsClient
{
	private String authToken;
	HttpRequestFactory requestFactory;

	public SpreadsheetsClient(String authToken) {
		this.authToken = authToken;
		setUpRequestFactory();
	}

	private void setUpRequestFactory() {
		HttpTransport transport = new NetHttpTransport();
		requestFactory = transport.createRequestFactory(new RequestInitializer());
	}

	private class RequestInitializer implements HttpRequestInitializer
	{
		final XmlNamespaceDictionary DICTIONARY = new XmlNamespaceDictionary()
			.set("", "http://www.w3.org/2005/Atom").set("app", "http://www.w3.org/2007/app")
			.set("batch", "http://schemas.google.com/gdata/batch")
			.set("docs", "http://schemas.google.com/docs/2007")
			.set("gAcl", "http://schemas.google.com/acl/2007")
			.set("gd", "http://schemas.google.com/g/2005")
			.set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
			.set("xml", "http://www.w3.org/XML/1998/namespace");

		@Override
		public void initialize(HttpRequest request) throws IOException {
			request.setHeaders(buildHeaders());
			request.addParser(new AtomParser(DICTIONARY));
		}

		private GoogleHeaders buildHeaders() {
			GoogleHeaders headers = new GoogleHeaders();

			headers.setApplicationName("Simple-Flashcards/0.0.1");
			headers.gdataVersion = "3.0";
			headers.setGoogleLogin(authToken);

			return headers;
		}
	}

	public SpreadsheetFeed getSpreadsheetFeed() {
		HttpRequest request = buildSpreadsheetFeedRequest();
		return processRequest(request, SpreadsheetFeed.class);
	}

	private HttpRequest buildSpreadsheetFeedRequest() {
		return buildGetRequest(SpreadsheetUrl.spreadsheetFeedUrl());
	}

	public SpreadsheetEntry getSpreadsheetEntry(String id) {
		HttpRequest request = buildSpreadsheedRequest(id);
		return processRequest(request, SpreadsheetEntry.class);
	}

	private HttpRequest buildSpreadsheedRequest(String id) {
		return buildGetRequest(new GoogleUrl(id));
	}

	public WorksheetFeed getWorksheetFeed(SpreadsheetEntry spreadsheet) {
		HttpRequest request = buildWorksheetFeedRequest(spreadsheet);
		return processRequest(request, WorksheetFeed.class);
	}

	private HttpRequest buildWorksheetFeedRequest(SpreadsheetEntry spreadsheet) {
		return buildGetRequest(spreadsheet.getWorksheetFeedUrl());
	}

	public CellFeed getCellFeed(WorksheetEntry worksheet) {
		HttpRequest request = buildCellFeedRequest(worksheet);
		return processRequest(request, CellFeed.class);
	}

	private HttpRequest buildCellFeedRequest(WorksheetEntry worksheet) {
		return buildGetRequest(worksheet.getCellFeedUrl());
	}

	private HttpRequest buildGetRequest(GoogleUrl url) {
		try {
			return requestFactory.buildGetRequest(url);
		}
		catch (IOException e) {
			throw new SpreadsheetException(e);
		}
	}

	private <T> T processRequest(HttpRequest request, Class<T> dataClass) {
		try {
			return request.execute().parseAs(dataClass);
		}
		catch (IOException e) {
			throw new SpreadsheetException("Unable to fetch data", e);
		}
	}
}
