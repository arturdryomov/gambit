package app.android.gambit.local;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import app.android.gambit.R;
import app.android.gambit.ui.Preferences;


public class ExampleDeckWriter
{
	public static final int[] ANDROID_VERSIONS_RESOURCES = {
		R.string.android_version_froyo,
		R.string.android_version_gingerbread,
		R.string.android_version_honeycomb,
		R.string.android_version_ice_cream_sandwich,
		R.string.android_version_jelly_bean
	};

	private static final String[] SUPPORTED_LANGUAGE_CODES = {"de", "es", "fr", "it"};

	private final Context context;
	private final Decks decks;

	public ExampleDeckWriter(Context context, Decks decks) {
		this.context = context;
		this.decks = decks;
	}

	public boolean shouldWriteDeck() {
		if (Preferences.getBoolean(context, Preferences.PREFERENCE_EXAMPLE_DECK_CREATED)) {
			return false;
		}

		return decks.getDecksList().isEmpty();
	}

	public void writeDeck() {
		decks.beginTransaction();

		try {
			writeDeck(decks);
			Preferences.set(context, Preferences.PREFERENCE_EXAMPLE_DECK_CREATED, true);

			decks.setTransactionSuccessful();
		}
		finally {
			decks.endTransaction();
		}
	}

	private void writeDeck(Decks decks) {
		Deck deck = decks.createDeck(context.getString(R.string.example_deck_title));

		List<String> frontSideTexts = buildFrontSideTexts();
		List<String> backSideTexts = buildBackSideTexts();

		for (int i = 0; i < frontSideTexts.size(); i++) {
			deck.createCard(frontSideTexts.get(i), backSideTexts.get(i));
		}
	}

	private List<String> buildFrontSideTexts() {
		return buildTexts(Locale.ENGLISH);
	}

	private List<String> buildTexts(Locale locale) {
		Locale originalLocale = getCurrentLocale();

		Resources resources = buildResources(locale);

		try {
			return buildTexts(resources);
		}
		finally {
			restoreLocale(originalLocale);
		}
	}

	private Locale getCurrentLocale() {
		return Locale.getDefault();
	}

	private Resources buildResources(Locale locale) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		AssetManager assetManager = context.getResources().getAssets();
		Configuration configuration = new Configuration(context.getResources().getConfiguration());
		configuration.locale = locale;

		return new Resources(assetManager, displayMetrics, configuration);
	}

	private List<String> buildTexts(Resources resources) {
		List<String> texts = new ArrayList<String>();

		for (int androidVersionResource : ANDROID_VERSIONS_RESOURCES) {
			texts.add(resources.getString(androidVersionResource));
		}

		return texts;
	}

	private void restoreLocale(Locale locale) {
		// Recreate Resources with original locale to avoid weird things
		buildResources(locale);
	}

	private List<String> buildBackSideTexts() {
		return buildTexts(selectLocaleForBackText());
	}

	private Locale selectLocaleForBackText() {
		if (isCurrentLocaleSupported()) {
			return getCurrentLocale();
		}
		else {
			return getRandomSupportedLocale();
		}
	}

	private boolean isCurrentLocaleSupported() {
		String currentLanguageCode = getCurrentLocale().getLanguage();
		return Arrays.asList(SUPPORTED_LANGUAGE_CODES).contains(currentLanguageCode);
	}

	private Locale getRandomSupportedLocale() {
		Random random = new Random();
		int languageIndex = random.nextInt(SUPPORTED_LANGUAGE_CODES.length);
		return new Locale(SUPPORTED_LANGUAGE_CODES[languageIndex]);
	}
}
