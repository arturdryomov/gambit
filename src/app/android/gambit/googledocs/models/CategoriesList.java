package app.android.gambit.googledocs.models;


import java.util.ArrayList;
import java.util.Collection;


public class CategoriesList extends ArrayList<Category>
{
	public CategoriesList() {
	}

	public CategoriesList(int initialCapacity) {
		super(initialCapacity);
	}

	public CategoriesList(Collection<Category> collection) {
		super(collection);
	}

	public Category findFirstWithScheme(String scheme) {
		for (Category category : this) {
			if (scheme.equals(category.getScheme())) {
				return category;
			}
		}

		return new Category();
	}
}
