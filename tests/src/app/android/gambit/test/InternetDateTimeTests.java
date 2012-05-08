package app.android.gambit.test;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.test.AndroidTestCase;
import app.android.gambit.InternetDateTime;


public class InternetDateTimeTests extends AndroidTestCase
{
	public void testFromStringPositiveOffset() {
		InternetDateTime dateTime = new InternetDateTime("2000-10-21T21:56:12.123+01:10");

		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.setTime(dateTime.toDate());

		assertEquals(2000, calendar.get(Calendar.YEAR));
		assertEquals(10, calendar.get(Calendar.MONTH) + 1); // January is 0 for GregorianCalendar
		assertEquals(21, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(6, calendar.get(Calendar.MINUTE));
		assertEquals(12, calendar.get(Calendar.SECOND));
	}

	public void testFromStringNegativeOffset() {
		InternetDateTime dateTime = new InternetDateTime("2000-10-21T21:56:12.123-01:10");

		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.setTime(dateTime.toDate());

		assertEquals(2000, calendar.get(Calendar.YEAR));
		assertEquals(10, calendar.get(Calendar.MONTH) + 1); // January is 0 for GregorianCalendar
		assertEquals(21, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(20, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(46, calendar.get(Calendar.MINUTE));
		assertEquals(12, calendar.get(Calendar.SECOND));
	}

	public void testFromStringZeroOffset() {
		InternetDateTime dateTime = new InternetDateTime("2000-10-21T21:56:12.123Z");

		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.setTime(dateTime.toDate());

		assertEquals(2000, calendar.get(Calendar.YEAR));
		assertEquals(10, calendar.get(Calendar.MONTH) + 1); // January is 0 for GregorianCalendar
		assertEquals(21, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(21, calendar.get(Calendar.HOUR_OF_DAY));
		assertEquals(56, calendar.get(Calendar.MINUTE));
		assertEquals(12, calendar.get(Calendar.SECOND));
	}

	public void testToStringPositiveOffset() {
		InternetDateTime dateTime = new InternetDateTime("2000-10-21T21:56:12.123+01:10");
		String converted = dateTime.toString();

		assertEquals("2000-10-21T23:06:12.123Z", converted);
	}

	public void testToNegativeZeroOffset() {
		InternetDateTime dateTime = new InternetDateTime("2000-10-21T21:56:12.123-01:10");
		String converted = dateTime.toString();

		assertEquals("2000-10-21T20:46:12.123Z", converted);
	}

	public void testToStringZeroOffset() {
		InternetDateTime dateTime = new InternetDateTime("2000-10-21T21:56:12.123Z");
		String converted = dateTime.toString();

		assertEquals("2000-10-21T21:56:12.123Z", converted);
	}
}
