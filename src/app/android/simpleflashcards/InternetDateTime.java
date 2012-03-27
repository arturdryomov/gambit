package app.android.simpleflashcards;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


/*
 * InternetDateTime is a light wrapper around java.util.Date that is able
 * to parse a string with date in RFC 3339 Internet Date/Time Format and
 * convert a date/time into a string of Internet Date/Time Format.
 *
 * This task cannot be simply done with SimpleDateFormat class because according to
 * RFC 3339 time offsets for different time zones are formatted as follows:
 *
 *   time-numoffset  = ("+" / "-") time-hour ":" time-minute
 *   time-offset     = "Z" / time-numoffset
 *
 *   "Z" is used to represent "no offset", same as +00:00.
 *
 *   A few examples:
 *     +01:50
 *     -02:30
 *     Z
 *
 * And SimpleDateFormat can only understand time offsets as '("+" / "-") time-hour time-minute'.
 * Respective examples are:
 *   +0150
 *   -0230
 *   +0000
 */

public class InternetDateTime
{
	private Date utcDate;

	/*
	 * This class parses RFC 3339 Internet Date/Time formatted date into integer values without
	 * any range checks.
	 * If format of the input is incorrect RuntimeException is thrown.
	 */

	private static class Parser
	{
		private String input;
		private int position;

		public int year;
		public int month;
		public int day;
		public int hour;
		public int minute;
		public int second;
		public int millisecond;
		public int timeOffsetHour;
		public int timeOffsetMinute;

		private void parse(String input) {
			this.input = input;
			position = 0;

			try {
				tryParse();
			}
			catch (Throwable e) {
				throw new RuntimeException("Invalid data format");
			}
		}

		private void tryParse() {
			parseYears();
			parseLiteralCaseInsensitive('-');
			parseMonth();
			parseLiteralCaseInsensitive('-');
			parseDay();

			parseDateTimeDelimiter();

			parseHour();
			parseLiteralCaseInsensitive(':');
			parseMinute();
			parseLiteralCaseInsensitive(':');
			parseSecond();

			parseMilliseconds();

			parseTimeOffset();
		}

		private void parseYears() {
			year = parseDigits(4);
		}

		private void parseMonth() {
			month = parseDigits(2);
		}

		private void parseDay() {
			day = parseDigits(2);
		}

		private void parseDateTimeDelimiter() {
			parseLiteralCaseInsensitive('T');
		}

		private void parseHour() {
			hour = parseDigits(2);
		}

		private void parseMinute() {
			minute = parseDigits(2);
		}

		private void parseSecond() {
			second = parseDigits(2);
		}

		private void parseMilliseconds() {
			if (current() != '.') {
				millisecond = 0;
			}
			moveNext();

			// Parse all digits up to first non-digit
			List<Character> secondFractionDigits = new ArrayList<Character>();
			while (Character.isDigit(current())) {
				secondFractionDigits.add(current());
				moveNext();
			}

			if (secondFractionDigits.isEmpty()) {
				throw new RuntimeException();
			}

			// Take only 1--3 leftmost chars as we want to represent milliseconds.
			// It would be better to perform rounding here but now we just throwing away
			// unneccesary digits.
			int leftMostCharsCount = Math.min(3, secondFractionDigits.size());
			String millisecondsString = new String();
			for (int i = 0; i < leftMostCharsCount; i++) {
				millisecondsString += secondFractionDigits.get(i);
			}

			millisecond = Integer.parseInt(millisecondsString);
		}

		private void parseTimeOffset() {
			if (isLiteralCaseInsensitive('Z')) {
				timeOffsetHour = 0;
				timeOffsetMinute = 0;
				moveNext();
			}
			else {
				int multiplyer;
				if (isLiteralCaseInsensitive('-')) {
					multiplyer = -1;
				}
				else if (isLiteralCaseInsensitive('+')) {
					multiplyer = 1;
				}
				else {
					throw new RuntimeException();
				}
				moveNext();

				timeOffsetHour = parseDigits(2) * multiplyer;
				parseLiteralCaseInsensitive(':');
				timeOffsetMinute = parseDigits(2) * multiplyer;
			}
			ensureEndOfString();
		}

		private int parseDigits(int digitsCount) {
			String digitsString = new String();

			for (int i = 0; i < digitsCount; i++) {
				digitsString += current();
				moveNext();
			}

			return Integer.parseInt(digitsString);
		}

		private void parseLiteralCaseInsensitive(char literal) {
			if (!isLiteralCaseInsensitive(literal)) {
				throw new RuntimeException();
			}
			moveNext();
		}

		private boolean isLiteralCaseInsensitive(char literal) {
			return Character.toUpperCase(current()) == Character.toUpperCase(literal);
		}

		private void ensureEndOfString() {
			if (position < input.length()) {
				throw new RuntimeException();
			}
		}

		private void moveNext() {
			position++;
		}

		private char current() {
			return input.charAt(position);
		}
	}

	// Initializes InternetDateTime instance to the current date/time
	public InternetDateTime() {
		utcDate = new Date();
	}

	public InternetDateTime(String dateTimeAsString) {
		utcDate = parseStringToUtcDate(dateTimeAsString);
	}

	private Date parseStringToUtcDate(String dateString) {
		Parser parser = new Parser();
		parser.parse(dateString);

		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

		calendar.set(Calendar.YEAR, parser.year);
		calendar.set(Calendar.MONTH, parser.month - 1); // Calendar.JANUARY == 0
		calendar.set(Calendar.DAY_OF_MONTH, parser.day);
		calendar.set(Calendar.HOUR_OF_DAY, parser.hour);
		calendar.set(Calendar.MINUTE, parser.minute);
		calendar.set(Calendar.SECOND, parser.second);
		calendar.set(Calendar.MILLISECOND, parser.millisecond);

		calendar.add(Calendar.HOUR_OF_DAY, parser.timeOffsetHour);
		calendar.add(Calendar.MINUTE, parser.timeOffsetMinute);

		return calendar.getTime();
	}

	public InternetDateTime(Date dateTimeAsUtcDate) {
		utcDate = dateTimeAsUtcDate;
	}

	// Returns string of RFC 3339 Internet Date/Time format in UTC (thus, offset is 0, i.e. Z)
	@Override
	public String toString() {
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.setTime(utcDate);

		StringBuilder builder = new StringBuilder();

		// The format is"yyyy-MM-dd'T'HH:mm:ss.SSSZ";
		builder.append(String.format("%04d-", calendar.get(Calendar.YEAR)));
		builder.append(String.format("%02d-", calendar.get(Calendar.MONTH) + 1)); // Calendar.JANUARY == 0
		builder.append(String.format("%02dT", calendar.get(Calendar.DAY_OF_MONTH)));
		builder.append(String.format("%02d:", calendar.get(Calendar.HOUR_OF_DAY)));
		builder.append(String.format("%02d:", calendar.get(Calendar.MINUTE)));
		builder.append(String.format("%02d.", calendar.get(Calendar.SECOND)));
		builder.append(String.format("%03dZ", calendar.get(Calendar.MILLISECOND)));

		return builder.toString();
	}

	// Returns Date in UTC
	public Date toDate() {
		return utcDate;
	}

	public boolean isAfter(InternetDateTime dateTime) {
		return utcDate.after(dateTime.utcDate);
	}

	public boolean isBefore(InternetDateTime dateTime) {
		return utcDate.before(dateTime.utcDate);
	}
}
