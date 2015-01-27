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

package ru.ming13.gambit.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import ru.ming13.gambit.R;

public final class DatabaseDefaults
{
	private static final int[] ANDROID_VERSION_RESOURCES = {
		R.string.android_version_cupcake,
		R.string.android_version_donut,
		R.string.android_version_eclair,
		R.string.android_version_froyo,
		R.string.android_version_gingerbread,
		R.string.android_version_honeycomb,
		R.string.android_version_ice_cream_sandwich,
		R.string.android_version_jelly_bean,
		R.string.android_version_kitkat
	};

	private static final String[] SUPPORTED_LANGUAGE_CODES = {
		"de",
		"ru"
	};

	private final Context context;
	private final SQLiteDatabase database;

	private final Locale localeForFrontText;
	private final Locale localeForBackText;

	public static DatabaseDefaults at(@NonNull Context context, @NonNull SQLiteDatabase database) {
		return new DatabaseDefaults(context, database);
	}

	private DatabaseDefaults(Context context, SQLiteDatabase database) {
		this.context = context.getApplicationContext();
		this.database = database;

		localeForFrontText = getLocaleForFrontText();
		localeForBackText = getLocaleForBackText();
	}

	private Locale getLocaleForFrontText() {
		return Locale.ENGLISH;
	}

	private Locale getLocaleForBackText() {
		if (isCurrentLocaleSupported()) {
			return getCurrentLocale();
		} else {
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
		int localePosition = new Random().nextInt(SUPPORTED_LANGUAGE_CODES.length);

		return new Locale(SUPPORTED_LANGUAGE_CODES[localePosition]);
	}

	public void writeDeck() {
		try {
			database.beginTransaction();

			createCards(createDeck());

			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	private void createCards(long deckId) {
		List<String> frontSideTexts = getCardTexts(localeForFrontText);
		List<String> backSideTexts = getCardTexts(localeForBackText);

		for (int cardPosition = 0; cardPosition < frontSideTexts.size(); cardPosition++) {
			createCard(deckId, frontSideTexts.get(cardPosition), backSideTexts.get(cardPosition));
		}
	}

	private List<String> getCardTexts(Locale locale) {
		Locale originalLocale = getCurrentLocale();

		try {
			return getCardTexts(getResources(locale));
		} finally {
			restoreLocale(originalLocale);
		}
	}

	private List<String> getCardTexts(Resources resources) {
		List<String> texts = new ArrayList<>();

		for (int androidVersionResource : ANDROID_VERSION_RESOURCES) {
			texts.add(resources.getString(androidVersionResource));
		}

		return texts;
	}

	private Resources getResources(Locale locale) {
		AssetManager assetManager = context.getResources().getAssets();
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		Configuration configuration = new Configuration(context.getResources().getConfiguration());
		configuration.locale = locale;

		return new Resources(assetManager, displayMetrics, configuration);
	}

	private void restoreLocale(Locale locale) {
		// Recreate Resources of original locale to avoid weird things

		getResources(locale);
	}

	private void createCard(long deckId, String frontSideText, String backSideText) {
		ContentValues cardValues = new ContentValues();
		cardValues.put(DatabaseSchema.CardsColumns.DECK_ID, deckId);
		cardValues.put(DatabaseSchema.CardsColumns.FRONT_SIDE_TEXT, frontSideText);
		cardValues.put(DatabaseSchema.CardsColumns.BACK_SIDE_TEXT, backSideText);
		cardValues.put(DatabaseSchema.CardsColumns.ORDER_INDEX, DatabaseSchema.CardsColumnsDefaultValues.ORDER_INDEX);

		database.insert(DatabaseSchema.Tables.CARDS, null, cardValues);
	}

	private long createDeck() {
		ContentValues deckValues = new ContentValues();
		deckValues.put(DatabaseSchema.DecksColumns.TITLE, getDeckTitle());
		deckValues.put(DatabaseSchema.DecksColumns.CURRENT_CARD_INDEX, DatabaseSchema.DecksColumnsDefaultValues.CURRENT_CARD_INDEX);

		return database.insert(DatabaseSchema.Tables.DECKS, null, deckValues);
	}

	private String getDeckTitle() {
		if (isCurrentLocaleSupported()) {
			return getCurrentDeckTitle();
		} else {
			return getSupportedDeckTitle(getDeckTitleLanguage(localeForBackText));
		}
	}

	private String getCurrentDeckTitle() {
		return context.getString(R.string.default_deck_title);
	}

	private String getSupportedDeckTitle(String deckTitleLanguage) {
		return context.getString(R.string.default_deck_title_mask, getDeckTitle(), deckTitleLanguage);
	}

	private String getDeckTitleLanguage(Locale locale) {
		Locale originalLocale = getCurrentLocale();

		try {
			return getResources(locale).getString(R.string.default_deck_title_language);
		} finally {
			restoreLocale(originalLocale);
		}
	}
}
