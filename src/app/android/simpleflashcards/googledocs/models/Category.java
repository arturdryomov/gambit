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

	public Category(String scheme, String term) {
		this();

		this.scheme = scheme;
		this.term = term;
	}

	private Category() {
		scheme = new String();
		term = new String();
		label = new String();
	}
}
