package app.android.simpleflashcards.googledocs.models;


import com.google.api.client.util.Key;


public class Content
{
	@Key("@src")
	private String source;

	public String getSource() {
		return source;
	}
}
