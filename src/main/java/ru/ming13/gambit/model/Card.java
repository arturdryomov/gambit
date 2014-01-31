package ru.ming13.gambit.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable
{
	private final String frontSideText;
	private final String backSideText;

	public Card(String frontSideText, String backSideText) {
		this.frontSideText = frontSideText;
		this.backSideText = backSideText;
	}

	public String getFrontSideText() {
		return frontSideText;
	}

	public String getBackSideText() {
		return backSideText;
	}

	public Card(Parcel parcel) {
		this.frontSideText = parcel.readString();
		this.backSideText = parcel.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(frontSideText);
		parcel.writeString(backSideText);
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
}
