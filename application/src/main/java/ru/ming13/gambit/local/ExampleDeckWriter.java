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

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;
import ru.ming13.gambit.R;
import ru.ming13.gambit.local.sqlite.DbFieldNames;
import ru.ming13.gambit.local.sqlite.DbTableNames;
import ru.ming13.gambit.local.sqlite.DbValues;


public class ExampleDeckWriter
{
	private static final int[] ANDROID_VERSIONS_RESOURCES = {
		R.string.android_version_froyo,
		R.string.android_version_gingerbread,
		R.string.android_version_honeycomb,
		R.string.android_version_ice_cream_sandwich,
		R.string.android_version_jelly_bean
	};

	private static final String[] SUPPORTED_LANGUAGE_CODES = {"de", "es", "fr", "it", "ru"};

	private final Context context;
	private final SQLiteDatabase database;

	private final Locale localeForFrontText;
	private final Locale localeForBackText;

	public static void writeDeck(Context context, SQLiteDatabase database) {
		new ExampleDeckWriter(context, database).writeDeck();
	}

	private ExampleDeckWriter(Context context, SQLiteDatabase database) {
		this.context = context;
		this.database = database;

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

	private void writeDeck() {
		database.beginTransaction();

		try {
			tryWriteDeck();

			database.setTransactionSuccessful();
		}
		finally {
			database.endTransaction();
		}
	}

	private void tryWriteDeck() {
		long deckId = createDeck();
		createCards(deckId);
	}

	private long createDeck() {
		ContentValues deckValues = new ContentValues();
		deckValues.put(DbFieldNames.DECK_TITLE, buildDeckTitle());
		deckValues.put(DbFieldNames.DECK_CURRENT_CARD_INDEX, DbValues.DEFAULT_DECK_CURRENT_CARD_INDEX);

		return database.insert(DbTableNames.DECKS, null, deckValues);
	}

	private String buildDeckTitle() {
		return String.format("%s (%s)", context.getString(R.string.example_deck_title),
			getExampleDeckTitleLanguage(localeForBackText));
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

	private void createCards(long deckId) {
		List<String> frontSideTexts = buildTexts(localeForFrontText);
		List<String> backSideTexts = buildTexts(localeForBackText);

		for (int cardIndex = 0; cardIndex < frontSideTexts.size(); cardIndex++) {
			createCard(deckId, frontSideTexts.get(cardIndex), backSideTexts.get(cardIndex));
		}
	}

	private void createCard(long deckId, String frontSideText, String backSideText) {
		ContentValues cardValues = new ContentValues();
		cardValues.put(DbFieldNames.CARD_DECK_ID, deckId);
		cardValues.put(DbFieldNames.CARD_FRONT_SIDE_TEXT, frontSideText);
		cardValues.put(DbFieldNames.CARD_BACK_SIDE_TEXT, backSideText);
		cardValues.put(DbFieldNames.CARD_ORDER_INDEX, DbValues.DEFAULT_CARD_ORDER_INDEX);

		database.insert(DbTableNames.CARDS, null, cardValues);
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
