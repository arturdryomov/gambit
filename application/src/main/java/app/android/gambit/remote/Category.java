package app.android.gambit.remote;


import android.text.TextUtils;
import com.google.api.client.util.Key;


public class Category
{
	@Key("@scheme")
	private String scheme;

	@Key("@term")
	private String term;

	@Key("@label")
	private String label;

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
		return TextUtils.isEmpty(scheme) && TextUtils.isEmpty(term) && TextUtils.isEmpty(label);
	}
}
