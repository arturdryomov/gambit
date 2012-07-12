package app.android.gambit.remote;


import java.util.Date;

import android.text.format.Time;


public class InternetDateTime
{
	private final Time time;

	public InternetDateTime() {
		time = new Time(Time.TIMEZONE_UTC);
		time.setToNow();
	}

	public InternetDateTime(String dateTimeAsString) {
		time = new Time(Time.TIMEZONE_UTC);
		time.parse3339(dateTimeAsString);
	}

	public InternetDateTime(Date dateTimeAsUtcDate) {
		time = new Time(Time.TIMEZONE_UTC);
		time.set(dateTimeAsUtcDate.getTime());
	}

	@Override
	public String toString() {
		return time.format3339(false);
	}

	public Date toDate() {
		return new Date(time.toMillis(false));
	}

	public boolean isAfter(InternetDateTime dateTime) {
		return time.after(dateTime.time);
	}

	public boolean isBefore(InternetDateTime dateTime) {
		return time.before(dateTime.time);
	}
}
