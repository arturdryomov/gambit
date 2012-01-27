package app.android.simpleflashcards;


import android.app.Activity;
import android.os.Bundle;


public class HelloAndroidActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
}
