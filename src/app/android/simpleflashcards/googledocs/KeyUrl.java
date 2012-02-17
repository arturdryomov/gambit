package app.android.simpleflashcards.googledocs;


import com.google.api.client.googleapis.GoogleUrl;

/*
 * The class is used to parse urls like 'http://blah.blah.blah/blah?key=somekey&blah'.
 * We can use GoogleUrl to build or parse arbitrary url. We can use @Key annotation
 * to add extra url parameter to parse, but there is already a 'key' field marked with
 * the annotation in GoogleUrl so nothing actually is needed to be done.
 */

public class KeyUrl extends GoogleUrl
{
	public KeyUrl(String url) {
		super(url);
	}

	public String getKey() {
		return key;
	}
}
