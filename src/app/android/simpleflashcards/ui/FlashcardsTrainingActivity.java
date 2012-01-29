package app.android.simpleflashcards.ui;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import app.android.simpleflashcards.R;


public class FlashcardsTrainingActivity extends Activity
{
	private final Context activityContext = this;

	private ArrayList<String> flashcardsData;

	public FlashcardsTrainingActivity() {
		super();

		flashcardsData = new ArrayList<String>();

		flashcardsData.add("First card");
		flashcardsData.add("Second card");
		flashcardsData.add("Third card");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flashcards_training);

		FlashcardsAdapter flashcardsAdapter = new FlashcardsAdapter();
		ViewPager flashcardsPager = (ViewPager) findViewById(R.id.flashcardsPager);
		flashcardsPager.setAdapter(flashcardsAdapter);
	}

	private class FlashcardsAdapter extends PagerAdapter
	{
		@Override
		public int getCount() {
			return flashcardsData.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			TextView textView = new TextView(activityContext);
			textView.setText(flashcardsData.get(position));
			textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
			textView.setTextSize(30);

			((ViewPager) container).addView(textView, 0);

			return textView;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((TextView) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (TextView) object;
		}
	}
}
