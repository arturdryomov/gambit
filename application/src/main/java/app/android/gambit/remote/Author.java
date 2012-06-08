package app.android.gambit.remote;


import com.google.api.client.util.Key;


public class Author
{
	@Key
	private String name;

	@Key
	private String email;

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
}
