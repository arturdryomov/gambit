package app.android.gambit.remote;


import com.google.api.client.googleapis.GoogleUrl;


public class SpreadsheetUrl extends GoogleUrl
{
	private static final String ROOT_PATH = "https://spreadsheets.google.com/feeds";
	private static final String SPREADSHEETS_FEED_PART = "/spreadsheets/private/full";

	public SpreadsheetUrl(String url) {
		super(url);
	}

	public static GoogleUrl spreadsheetFeedUrl() {
		StringBuilder urlBuilder = new StringBuilder();

		urlBuilder.append(ROOT_PATH);
		urlBuilder.append(SPREADSHEETS_FEED_PART);

		return new GoogleUrl(urlBuilder.toString());
	}
}
