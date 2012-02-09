package app.android.simpleflashcards.googledocs.models;


import java.util.List;

import com.google.api.client.util.Key;


public class Category
{
	@Key("@scheme")
	private String scheme = new String();

	@Key("@term")
	private String term = new String();

	@Key("@label")
	private String label = new String();

	public String getScheme() {
		return scheme;
	}

	public String getTerm() {
		return term;
	}

	public String getLabel() {
		return label;
	}

	public boolean isEmpty() {
		return scheme.isEmpty() && term.isEmpty() && label.isEmpty();
	}

	public static Category findFirstWithScheme(List<Category> categories, String scheme) {
		for (Category category : categories) {
			if (scheme.equals(category.getScheme())) {
				return category;
			}
		}

		return new Category();
	}

	public static Category createForUploading(String scheme, String term) {
		Category category = new Category();

		category.scheme = scheme;
		category.term = term;

		return category;
	}
}
