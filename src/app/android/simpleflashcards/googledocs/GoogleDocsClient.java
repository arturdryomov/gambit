package app.android.simpleflashcards.googledocs;


import java.io.IOException;

import app.android.simpleflashcards.googledocs.models.DocumentEntry;
import app.android.simpleflashcards.googledocs.models.DocumentFeed;
import app.android.simpleflashcards.spreadsheets.FailedRequestException;
import app.android.simpleflashcards.spreadsheets.SpreadsheetException;
import app.android.simpleflashcards.spreadsheets.UnauthorizedException;

import com.google.api.client.googleapis.GoogleHeaders;
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


public class GoogleDocsClient
{
	private static final int UNAUTHORIZED_STATUS_CODE = 401;
	private static final XmlNamespaceDictionary DICTIONARY;

	static {
		DICTIONARY = new XmlNamespaceDictionary().set("", "http://www.w3.org/2005/Atom")
			.set("app", "http://www.w3.org/2007/app")
			.set("batch", "http://schemas.google.com/gdata/batch")
			.set("docs", "http://schemas.google.com/docs/2007")
			.set("gAcl", "http://schemas.google.com/acl/2007")
			.set("gd", "http://schemas.google.com/g/2005")
			.set("gs", "http://schemas.google.com/spreadsheets/2006")
			.set("gsx", "http://schemas.google.com/spreadsheets/2006/extended")
			.set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
			.set("xml", "http://www.w3.org/XML/1998/namespace");
	}

	private final String authToken;
	private HttpRequestFactory requestFactory;

	public GoogleDocsClient(String authToken) {
		this.authToken = authToken;
		setUpRequestFactory();
	}

	private void setUpRequestFactory() {
		HttpTransport transport = new NetHttpTransport();
		requestFactory = transport.createRequestFactory(new RequestInitializer());
	}

	private class RequestInitializer implements HttpRequestInitializer
	{
		private static final String GDATA_VERSION = "3.0";

		@Override
		public void initialize(HttpRequest request) throws IOException {
			request.setHeaders(buildHeaders());
			request.addParser(new AtomParser(DICTIONARY));
		}

		private GoogleHeaders buildHeaders() {
			GoogleHeaders headers = new GoogleHeaders();

			headers.gdataVersion = GDATA_VERSION;
			headers.setGoogleLogin(authToken);

			return headers;
		}
	}

	public DocumentFeed getDocumentFeed() {
		HttpRequest request = buildGetRequest(GoogleDocsUrl.documentsFeedUrl());
		return processGetRequest(request, DocumentFeed.class);
	}

	public DocumentFeed getDocumentFeed(DocumentEntry.Type type) {
		HttpRequest request = buildGetRequest(GoogleDocsUrl.documentsFeedUrl(type));
		return processGetRequest(request, DocumentFeed.class);
	}

	public void uploadEmptyDocument(DocumentEntry.Type type, String title) {
		DocumentEntry emptyDocument = DocumentEntry.createForUploading(type, title);
		AtomContent content = AtomContent.forEntry(DICTIONARY, emptyDocument);

		// Using post url is considered deprecated. However, if using resumable create
		// media url, a document cannot be just created like it is documented: the server
		// asks for some additional info as if it is intended to upload and convert a file.
		HttpRequest request = buildPostRequest(getPostUrl(), content);

		processPostRequest(request);
	}

	private GoogleDocsUrl getPostUrl() {
		return getDocumentFeed().getPostUrl();
	}

	private HttpRequest buildGetRequest(GoogleDocsUrl url) {
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

	private HttpRequest buildPostRequest(GoogleDocsUrl url, HttpContent content) {
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
		if (statusCode == UNAUTHORIZED_STATUS_CODE) {
			return new UnauthorizedException();
		}
		else {
			return new FailedRequestException();
		}
	}
}
