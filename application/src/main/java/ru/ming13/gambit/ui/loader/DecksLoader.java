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

package ru.ming13.gambit.ui.loader;


import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ming13.gambit.local.DbProvider;
import ru.ming13.gambit.local.Deck;
import ru.ming13.gambit.ui.loader.result.LoaderResult;
import ru.ming13.gambit.ui.loader.result.LoaderStatus;


public class DecksLoader extends AsyncTaskLoader<LoaderResult<List<Deck>>>
{
	public static DecksLoader newInstance(Context context) {
		return new DecksLoader(context);
	}

	private DecksLoader(Context context) {
		super(context);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public LoaderResult<List<Deck>> loadInBackground() {
		List<Deck> decks = DbProvider.getInstance().getDecks().getDecksList();

		return buildSuccessResult(decks);
	}

	private LoaderResult<List<Deck>> buildSuccessResult(List<Deck> decks) {
		return new LoaderResult<List<Deck>>(LoaderStatus.SUCCESS, decks, new String());
	}
}
