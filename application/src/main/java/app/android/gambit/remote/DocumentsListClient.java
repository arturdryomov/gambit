package app.android.gambit.remote;


import app.android.gambit.remote.DocumentEntry.Type;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.xml.atom.AtomContent;


public class DocumentsListClient extends GoogleDocsClient
{
	public DocumentsListClient(String authToken) {
		super(authToken);
	}

	public DocumentFeed getDocumentFeed() {
		HttpRequest request = buildGetRequest(DocumentsListUrl.documentsFeedUrl());
		return processGetRequest(request, DocumentFeed.class);
	}

	public DocumentFeed getDocumentFeed(DocumentEntry.Type type) {
		HttpRequest request = buildGetRequest(DocumentsListUrl.documentsFeedUrl(type));
		return processGetRequest(request, DocumentFeed.class);
	}

	public DocumentEntry getSpreadsheetByKey(String key) {
		DocumentFeed documents = getDocumentFeed(Type.SPREADSHEET);

		for (DocumentEntry document : documents.getEntries()) {
			if (document.getSpreadsheetKey().equals(key)) {
				return document;
			}
		}

		throw new EntryNotFoundException();
	}

	public void uploadEmptyDocument(DocumentEntry.Type type, String title) {
		DocumentEntry emptyDocument = new DocumentEntry(type, title);
		AtomContent content = AtomContent.forEntry(getXmlNamespaceDictionary(), emptyDocument);

		// Using post url is considered deprecated. However, if using resumable create
		// media url, a document cannot be just created like it is documented: the server
		// asks for some additional info as if it is intended to upload and convert a file.
		HttpRequest request = buildPostRequest(getPostUrl(), content);

		processPostRequest(request);
	}

	private DocumentsListUrl getPostUrl() {
		return getDocumentFeed().getPostUrl();
	}

	public void updateDocument(DocumentEntry entry) {
		AtomContent content = AtomContent.forEntry(getXmlNamespaceDictionary(), entry);
		HttpRequest request = buildPutRequest(entry.getEditUrl(), content);

		processPutRequest(request);
	}
}
