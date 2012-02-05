package app.android.simpleflashcards.spreadsheets;


import java.io.IOException;

import app.android.simpleflashcards.spreadsheets.models.CellFeed;
import app.android.simpleflashcards.spreadsheets.models.SpreadsheetEntry;
import app.android.simpleflashcards.spreadsheets.models.SpreadsheetFeed;
import app.android.simpleflashcards.spreadsheets.models.WorksheetEntry;
import app.android.simpleflashcards.spreadsheets.models.WorksheetFeed;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.xml.XmlNamespaceDictionary;


// This class methods will throw UnauthorizedException if auth token passed is invalid,
// and FailedRequestException if internet connection is unavailable.

public class SpreadsheetsClient
{
	private static final String APPLICATION_NAME = "Simple-Flashcards/0.0.1";
	private static final XmlNamespaceDictionary DICTIONARY = new XmlNamespaceDictionary()
		.set("", "http://www.w3.org/2005/Atom").set("app", "http://www.w3.org/2007/app")
		.set("batch", "http://schemas.google.com/gdata/batch")
		.set("docs", "http://schemas.google.com/docs/2007")
		.set("gAcl", "http://schemas.google.com/acl/2007")
		.set("gd", "http://schemas.google.com/g/2005")
		.set("gs", "http://schemas.google.com/spreadsheets/2006")
		.set("gsx", "http://schemas.google.com/spreadsheets/2006/extended")
		.set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
		.set("xml", "http://www.w3.org/XML/1998/namespace");

	private String authToken;
	private HttpRequestFactory requestFactory;

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
		@Override
		public void initialize(HttpRequest request) throws IOException {
			request.setHeaders(buildHeaders());
			request.addParser(new AtomParser(DICTIONARY));
		}

		private GoogleHeaders buildHeaders() {
			GoogleHeaders headers = new GoogleHeaders();

			headers.setApplicationName(APPLICATION_NAME);
			headers.gdataVersion = "3.0";
			headers.setGoogleLogin(authToken);

			return headers;
		}
	}

	public SpreadsheetFeed getSpreadsheetFeed() {
		HttpRequest request = buildGetRequest(SpreadsheetUrl.spreadsheetFeedUrl());
		return processGetRequest(request, SpreadsheetFeed.class);
	}

	public SpreadsheetEntry getSpreadsheetEntry(String id) {
		HttpRequest request = buildGetRequest(new SpreadsheetUrl(id));
		return processGetRequest(request, SpreadsheetEntry.class);
	}

	public WorksheetFeed getWorksheetFeed(SpreadsheetEntry spreadsheet) {
		HttpRequest request = buildGetRequest(spreadsheet.getWorksheetFeedUrl());
		return processGetRequest(request, WorksheetFeed.class);
	}

	public CellFeed getCellFeed(WorksheetEntry worksheet) {
		HttpRequest request = buildGetRequest(worksheet.getCellFeedUrl());
		return processGetRequest(request, CellFeed.class);
	}

	private HttpRequest buildGetRequest(GoogleUrl url) {
		try {
			return requestFactory.buildGetRequest(url);
		}
		catch (IOException e) {
			throw new SpreadsheetException(e);
		}
	}

	private <T> T processGetRequest(HttpRequest request, Class<T> dataClass) {
		try {
			return processRequest(request).parseAs(dataClass);
		}
		catch (IOException e) {
			throw new FailedRequestException(e);
		}
	}

	public void insertWorksheet(SpreadsheetEntry spreadsheet, String title, int rowCount,
		int columnCount) {

		WorksheetEntry entry = new WorksheetEntry();
		entry.title = title;
		entry.rowCount = rowCount;
		entry.columnCount = columnCount;

		AtomContent content = AtomContent.forEntry(DICTIONARY, entry);

		HttpRequest request = buildPostRequest(spreadsheet.getWorksheetFeedUrl(), content);
		processPostRequest(request);
	}

	private HttpRequest buildPostRequest(SpreadsheetUrl url, HttpContent content) {
		try {
			return requestFactory.buildPostRequest(url, content);
		}
		catch (IOException e) {
			throw new SpreadsheetException(e);
		}
	}

	private void processPostRequest(HttpRequest request) {
		processRequest(request);
	}

	private HttpResponse processRequest(HttpRequest request) {
		try {
			return request.execute();
		}
		catch (HttpResponseException e) {
			throw exceptionFromUnsuccessfulStatusCode(e.getResponse().getStatusCode());
		}
		catch (IOException e) {
			throw new FailedRequestException(e);
		}
	}

	private FailedRequestException exceptionFromUnsuccessfulStatusCode(int statusCode) {
		if (statusCode == 401) {
			return new UnauthorizedException();
		}
		else {
			return new FailedRequestException();
		}
	}
}
