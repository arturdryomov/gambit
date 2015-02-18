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

package ru.ming13.gambit.util;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.f2prateek.bundler.Bundler;

import ru.ming13.gambit.fragment.CardCreationFragment;
import ru.ming13.gambit.fragment.CardEditingFragment;
import ru.ming13.gambit.fragment.CardsListFragment;
import ru.ming13.gambit.fragment.CardsPagerFragment;
import ru.ming13.gambit.fragment.DeckCreationFragment;
import ru.ming13.gambit.fragment.DeckEditingFragment;
import ru.ming13.gambit.fragment.DecksListFragment;
import ru.ming13.gambit.fragment.GoogleServicesErrorDialog;
import ru.ming13.gambit.fragment.MessageFragment;
import ru.ming13.gambit.model.Card;
import ru.ming13.gambit.model.Deck;

public final class Fragments
{
	private Fragments() {
	}

	public static final class Arguments
	{
		private Arguments() {
		}

		public static final String DECK = "deck";
		public static final String CARD = "card";

		public static final String MESSAGE = "message";

		public static final String ERROR_CODE = "error_code";
		public static final String REQUEST_CODE = "request_code";
	}

	public static final class Builder
	{
		private Builder() {
		}

		public static Fragment buildDecksListFragment() {
			return new DecksListFragment();
		}

		public static Fragment buildDeckCreationFragment() {
			return new DeckCreationFragment();
		}

		public static Fragment buildDeckEditingFragment(@NonNull Deck deck) {
			Fragment fragment = new DeckEditingFragment();

			fragment.setArguments(Bundler.create()
				.put(Arguments.DECK, deck)
				.get());

			return fragment;
		}

		public static Fragment buildCardsListFragment(@NonNull Deck deck) {
			Fragment fragment = new CardsListFragment();

			fragment.setArguments(Bundler.create()
				.put(Arguments.DECK, deck)
				.get());

			return fragment;
		}

		public static Fragment buildCardsPagerFragment(@NonNull Deck deck) {
			Fragment fragment = new CardsPagerFragment();

			fragment.setArguments(Bundler.create()
				.put(Arguments.DECK, deck)
				.get());

			return fragment;
		}

		public static Fragment buildCardCreationFragment(@NonNull Deck deck) {
			Fragment fragment = new CardCreationFragment();

			fragment.setArguments(Bundler.create()
				.put(Arguments.DECK, deck)
				.get());

			return fragment;
		}

		public static Fragment buildCardEditingFragment(@NonNull Deck deck, @NonNull Card card) {
			Fragment fragment = new CardEditingFragment();

			fragment.setArguments(Bundler.create()
				.put(Arguments.DECK, deck)
				.put(Arguments.CARD, card)
				.get());

			return fragment;
		}

		public static Fragment buildMessageFragment(@NonNull String message) {
			Fragment fragment = new MessageFragment();

			fragment.setArguments(Bundler.create()
				.put(Arguments.MESSAGE, message)
				.get());

			return fragment;
		}

		public static DialogFragment buildGoogleServicesErrorDialog(int errorCode, int requestCode) {
			DialogFragment fragment = new GoogleServicesErrorDialog();

			fragment.setArguments(Bundler.create()
				.put(Arguments.ERROR_CODE, errorCode)
				.put(Arguments.REQUEST_CODE, requestCode)
				.get());

			return fragment;
		}
	}

	public static final class Operator
	{
		private final FragmentManager fragmentManager;

		public static Operator at(@NonNull Activity activity) {
			return new Operator(activity);
		}

		private Operator(Activity activity) {
			this.fragmentManager = activity.getFragmentManager();
		}

		private boolean isSet(@IdRes int fragmentContainerId) {
			return fragmentManager.findFragmentById(fragmentContainerId) != null;
		}

		public void reset(@IdRes int fragmentContainerId, @NonNull Fragment fragment) {
			fragmentManager
				.beginTransaction()
				.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
				.replace(fragmentContainerId, fragment)
				.commit();
		}

		public void set(@IdRes int fragmentContainerId, @NonNull Fragment fragment) {
			if (isSet(fragmentContainerId)) {
				return;
			}

			fragmentManager
				.beginTransaction()
				.add(fragmentContainerId, fragment)
				.commit();
		}
	}
}
