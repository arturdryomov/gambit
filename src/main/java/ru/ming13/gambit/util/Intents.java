package ru.ming13.gambit.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import ru.ming13.gambit.R;
import ru.ming13.gambit.activity.CardCreationActivity;
import ru.ming13.gambit.activity.CardsListActivity;
import ru.ming13.gambit.activity.DeckCreationActivity;
import ru.ming13.gambit.activity.DeckEditingActivity;
import ru.ming13.gambit.activity.LicensesActivity;

public final class Intents
{
	private Intents() {
	}

	public static final class Extras
	{
		private Extras() {
		}

		public static final String URI = "uri";
	}

	private static final class UriMasks
	{
		private UriMasks() {
		}

		public static final String EMAIL = "mailto:%s?subject=%s";

		public static final String GOOGLE_PLAY_APP = "market://details?id=%s";
		public static final String GOOGLE_PLAY_WEB = "https://play.google.com/store/apps/details?id=%s";
	}

	public static final class Builder
	{
		private final Context context;

		public static Builder with(Context context) {
			return new Builder(context);
		}

		private Builder(Context context) {
			this.context = context;
		}

		public Intent buildDeckCreationIntent() {
			return new Intent(context, DeckCreationActivity.class);
		}

		public Intent buildDeckRenamingIntent(Uri deckUri) {
			Intent intent = new Intent(context, DeckEditingActivity.class);
			intent.putExtra(Extras.URI, deckUri);

			return intent;
		}

		public Intent buildCardCreationIntent(Uri cardsUri) {
			Intent intent = new Intent(context, CardCreationActivity.class);
			intent.putExtra(Extras.URI, cardsUri);

			return intent;
		}

		public Intent buildCardsListIntent(Uri deckUri) {
			Intent intent = new Intent(context, CardsListActivity.class);
			intent.putExtra(Extras.URI, deckUri);

			return intent;
		}

		public Intent buildLicensesIntent() {
			return new Intent(context, LicensesActivity.class);
		}

		public Intent buildFeedbackIntent() {
			String feedbackAddress = context.getString(R.string.email_feedback_address);
			String feedbackSubject = context.getString(R.string.email_feedback_subject);

			String feedbackUri = String.format(UriMasks.EMAIL, feedbackAddress, feedbackSubject);

			return new Intent(Intent.ACTION_SENDTO, Uri.parse(feedbackUri));
		}

		public Intent buildGooglePlayAppIntent() {
			String packageName = context.getPackageName();

			String googlePlayUri = String.format(UriMasks.GOOGLE_PLAY_APP, packageName);

			return new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUri));
		}

		public Intent buildGooglePlayWebIntent() {
			String packageName = context.getPackageName();

			String googlePlayUri = String.format(UriMasks.GOOGLE_PLAY_WEB, packageName);

			return new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUri));
		}
	}
}
