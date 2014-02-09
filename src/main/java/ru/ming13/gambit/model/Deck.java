package ru.ming13.gambit.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Deck implements Parcelable
{
	private final String title;

	public Deck(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
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
		this.title = parcel.readString();
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(title);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
