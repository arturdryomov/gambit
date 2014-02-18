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

public class Card implements Parcelable
{
	private final long id;
	private final String frontSideText;
	private final String backSideText;

	public Card(String frontSideText, String backSideText) {
		this(Long.MIN_VALUE, frontSideText, backSideText);
	}

	public Card(long id, String frontSideText, String backSideText) {
		this.id = id;
		this.frontSideText = frontSideText;
		this.backSideText = backSideText;
	}

	public long getId() {
		return id;
	}

	public String getFrontSideText() {
		return frontSideText;
	}

	public String getBackSideText() {
		return backSideText;
	}

	public static final Creator<Card> CREATOR = new Creator<Card>() {
		@Override
		public Card createFromParcel(Parcel parcel) {
			return new Card(parcel);
		}

		@Override
		public Card[] newArray(int size) {
			return new Card[size];
		}
	};

	private Card(Parcel parcel) {
		this.id = parcel.readLong();
		this.frontSideText = parcel.readString();
		this.backSideText = parcel.readString();
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(frontSideText);
		parcel.writeString(backSideText);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
