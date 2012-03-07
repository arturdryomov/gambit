package app.android.simpleflashcards.googledocs.models;


import com.google.api.client.util.Key;


public class Link
{
	@Key("@href")
	private String href;

	@Key("@rel")
	private String rel;

	public String getHref() {
		return href;
	}

	public String getRel() {
		return rel;
	}
}
