package app.android.gambit.remote;


import com.google.api.client.util.Key;


public class Link
{
	@Key
	private String href;

	@Key
	private String rel;

	public String getHref() {
		return href;
	}

	public String getRel() {
		return rel;
	}
}
