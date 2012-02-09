package app.android.simpleflashcards.googledocs.models;


import java.util.ArrayList;
import java.util.List;

import app.android.simpleflashcards.googledocs.GoogleDocsUrl;

import com.google.api.client.util.Key;


public class DocumentFeed
{
	private static final String RESUMABLE_CREATE_MEDIA_SCHEME = "http://schemas.google.com/g/2005#resumable-create-media";
	private static final String POST_SCHEME = "http://schemas.google.com/g/2005#post";

	@Key("link")
	private List<Link> links = new ArrayList<Link>();

	@Key("entry")
	private List<DocumentEntry> entries = new ArrayList<DocumentEntry>();

	public List<DocumentEntry> getEntries() {
		return entries;
	}

	public GoogleDocsUrl getResumableCreateMediaUrl() {
		String resumableCreateMediaHref = Link.findFirstWithRel(links, RESUMABLE_CREATE_MEDIA_SCHEME)
			.getHref();
		return new GoogleDocsUrl(resumableCreateMediaHref);
	}

	public GoogleDocsUrl getPostUrl() {
		String postHref = Link.findFirstWithRel(links, POST_SCHEME).getHref();
		return new GoogleDocsUrl(postHref);
	}
}
