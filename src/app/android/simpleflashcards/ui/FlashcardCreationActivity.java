package app.android.simpleflashcards.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import app.android.simpleflashcards.R;


public class FlashcardCreationActivity extends Activity
{
	private Context activityContext = this;
	
	private String frontSideText;
	private String backSideText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcard_creation);
		
		initializeBodyControls();
	}
	
	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(confirmListener);
	}
	
	private OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			readUserData();
			
			if (isUserDataCorrect()) {
				// TODO: Call flashcard creation task
				
				finish();
			}
		}
	};
	
	private void readUserData() {
		EditText frontSideEdit = (EditText) findViewById(R.id.frondSideEdit);
		EditText backSideEdit = (EditText) findViewById(R.id.backSideEdit);
		
		frontSideText = frontSideEdit.getText().toString().trim();
		backSideText = backSideEdit.getText().toString().trim();
	}
	
	private boolean isUserDataCorrect() {
		if (!isFrontSideTextCorrect()) {
			return false;
		}
		
		if (!isBackSideTextCorrect()) {
			return false;
		}
		
		return true;
	}
	
	private boolean isFrontSideTextCorrect() {
		if (frontSideText.isEmpty()) {
			UserAlerter.alert(activityContext, getString(R.string.enterFrontText));
			
			return false;
		}
		
		return true;
	}
	
	private boolean isBackSideTextCorrect() {
		if (backSideText.isEmpty()) {
			UserAlerter.alert(activityContext, getString(R.string.enterBackText));
			
			return false;
		}
		
		return true;
	}
}
