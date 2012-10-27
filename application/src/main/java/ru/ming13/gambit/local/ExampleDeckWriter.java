/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.ming13.gambit.local;


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
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.model.Deck;
import ru.ming13.gambit.local.model.Decks;
import ru.ming13.gambit.ui.util.Preferences;


public class ExampleDeckWriter
{
	public static final int[] ANDROID_VERSIONS_RESOURCES = {
		R.string.android_version_froyo,
		R.string.android_version_gingerbread,
		R.string.android_version_honeycomb,
		R.string.android_version_ice_cream_sandwich,
		R.string.android_version_jelly_bean
	};

	private static final String[] SUPPORTED_LANGUAGE_CODES = {"de", "es", "fr", "it", "ru"};

	private final Context context;
	private final Decks decks;

	private final Locale localeForFrontText;
	private final Locale localeForBackText;

	public ExampleDeckWriter(Context context, Decks decks) {
		this.context = context;
		this.decks = decks;

		localeForFrontText = Locale.ENGLISH;
		localeForBackText = selectLocaleForBackText();
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

	private Locale getCurrentLocale() {
		return Locale.getDefault();
	}

	private Locale getRandomSupportedLocale() {
		Random random = new Random();
		int languageIndex = random.nextInt(SUPPORTED_LANGUAGE_CODES.length);
		return new Locale(SUPPORTED_LANGUAGE_CODES[languageIndex]);
	}

	public boolean shouldWriteDeck() {
		if (Preferences.getBoolean(context, Preferences.Keys.EXAMPLE_DECK_CREATED)) {
			return false;
		}

		return decks.getDecksList().isEmpty();
	}

	public void writeDeck() {
		decks.beginTransaction();

		try {
			tryWriteDeck();
			Preferences.set(context, Preferences.Keys.EXAMPLE_DECK_CREATED, true);

			decks.setTransactionSuccessful();
		}
		finally {
			decks.endTransaction();
		}
	}

	private void tryWriteDeck() {
		Deck deck = buildDeck();

		List<String> frontSideTexts = buildTexts(localeForFrontText);
		List<String> backSideTexts = buildTexts(localeForBackText);

		for (int i = 0; i < frontSideTexts.size(); i++) {
			deck.createCard(frontSideTexts.get(i), backSideTexts.get(i));
		}
	}

	private Deck buildDeck() {
		String deckTitle = String.format("%s (%s)", context.getString(R.string.example_deck_title),
			getExampleDeckTitleLanguage(localeForBackText));

		return decks.createDeck(deckTitle);
	}

	private String getExampleDeckTitleLanguage(Locale locale) {
		Locale originalLocale = getCurrentLocale();

		Resources resources = buildResources(locale);

		try {
			return resources.getString(R.string.example_deck_title_language);
		}
		finally {
			restoreLocale(originalLocale);
		}
	}

	private Resources buildResources(Locale locale) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		AssetManager assetManager = context.getResources().getAssets();
		Configuration configuration = new Configuration(context.getResources().getConfiguration());
		configuration.locale = locale;

		return new Resources(assetManager, displayMetrics, configuration);
	}

	private void restoreLocale(Locale locale) {
		// Recreate Resources with original locale to avoid weird things
		buildResources(locale);
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

	private List<String> buildTexts(Resources resources) {
		List<String> texts = new ArrayList<String>();

		for (int androidVersionResource : ANDROID_VERSIONS_RESOURCES) {
			texts.add(resources.getString(androidVersionResource));
		}

		return texts;
	}
}
