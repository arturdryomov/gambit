package app.android.gambit.remote;



import com.google.api.client.googleapis.GoogleUrl;


public class DocumentsListUrl extends GoogleUrl
{
	private static final String ROOT_PATH = "https://docs.google.com/feeds";
	private static final String DOCUMENTS_FEED_PART = "/default/private/full";
	private static final String DIVIDER_PART = "/-";
	private static final String TYPE_PART_DRAWING = "/drawing";
	private static final String TYPE_PART_PRESENTATION = "/presentation";
	private static final String TYPE_PART_SPREADSHEET = "/spreadsheet";
	private static final String TYPE_PART_DOCUMENT = "/document";
	private static final String OWNER_PART = "/mine";

	public DocumentsListUrl(String url) {
		super(url);
	}

	public static DocumentsListUrl documentsFeedUrl() {
		StringBuilder urlBuilder = new StringBuilder();

		urlBuilder.append(ROOT_PATH);
		urlBuilder.append(DOCUMENTS_FEED_PART);
		urlBuilder.append(DIVIDER_PART);
		urlBuilder.append(OWNER_PART);

		return new DocumentsListUrl(urlBuilder.toString());
	}

	public static DocumentsListUrl documentsFeedUrl(DocumentEntry.Type type) {
		StringBuilder urlBuilder = new StringBuilder();

		urlBuilder.append(ROOT_PATH);
		urlBuilder.append(DOCUMENTS_FEED_PART);
		urlBuilder.append(DIVIDER_PART);
		urlBuilder.append(buildDocumentTypePart(type));
		urlBuilder.append(OWNER_PART);

		return new DocumentsListUrl(urlBuilder.toString());
	}

	private static String buildDocumentTypePart(DocumentEntry.Type type) {
		switch (type) {
			case DOCUMENT:
				return TYPE_PART_DOCUMENT;

			case SPREADSHEET:
				return TYPE_PART_SPREADSHEET;

			case PRESENTATION:
				return TYPE_PART_PRESENTATION;

			case DRAWING:
				return TYPE_PART_DRAWING;

			default:
				throw new SyncException("Unknown Google Docs type");
		}
	}
}
