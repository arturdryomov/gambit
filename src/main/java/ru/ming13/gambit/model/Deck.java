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

package ru.ming13.gambit.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Deck implements Parcelable
{
	private final long id;
	private final String title;
	private final int currentCardPosition;

	public Deck(String title) {
		this(Long.MIN_VALUE, title, Integer.MIN_VALUE);
	}

	public Deck(long id, String title) {
		this(id, title, Integer.MIN_VALUE);
	}

	public Deck(long id, String title, int currentCardPosition) {
		this.id = id;
		this.title = title;
		this.currentCardPosition = currentCardPosition;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getCurrentCardPosition() {
		return currentCardPosition;
	}

	public static final Creator<Deck> CREATOR = new Creator<Deck>()
	{
		@Override
		public Deck createFromParcel(Parcel parcel) {
			return new Deck(parcel);
		}

		@Override
		public Deck[] newArray(int size) {
			return new Deck[size];
		}
	};

	private Deck(Parcel parcel) {
		this.id = parcel.readLong();
		this.title = parcel.readString();
		this.currentCardPosition = parcel.readInt();
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(title);
		parcel.writeInt(currentCardPosition);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
