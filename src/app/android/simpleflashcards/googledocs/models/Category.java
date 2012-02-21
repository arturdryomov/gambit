package app.android.simpleflashcards.googledocs.models;


import java.util.List;

import com.google.api.client.util.Key;


public class Category
{
	@Key("@scheme")
	private String scheme;

	@Key("@term")
	private String term;

	@Key("@label")
	private String label;

	public String getScheme() {
		return scheme;
	}

	public String getTerm() {
		return term;
	}

	public String getLabel() {
		return label;
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
