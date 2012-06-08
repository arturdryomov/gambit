package app.android.gambit.remote;


import com.google.api.client.util.Key;


public class Category
{
	@Key
	private String scheme;

	@Key
	private String term;

	@Key
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

	public Category() {
		scheme = new String();
		term = new String();
		label = new String();
	}

	public Category(String scheme, String term) {
		this();

		this.scheme = scheme;
		this.term = term;
	}
}
