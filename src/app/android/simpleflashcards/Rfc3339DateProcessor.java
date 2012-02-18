package app.android.simpleflashcards;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class Rfc3339DateProcessor
{
	private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN);

	public static Date parse(String dateString) {
		try {
			dateString = withUpdatedUtcZoneRepresentation(dateString);
			return dateFormat.parse(dateString);
		}
		catch (ParseException e) {
			throw new RuntimeException("Invalid date format");
		}
	}

	private static String withUpdatedUtcZoneRepresentation(String dateString) {
		if (dateString.endsWith("Z")) {
			dateString = removeTrailingCharacter(dateString) + "+0000";
		}
		return dateString;
	}

	private static String removeTrailingCharacter(String dateString) {
		return dateString.substring(0, dateString.length() - 1);
	}

	public static String convertToString(Date date, TimeZone timeZone) {
		dateFormat.setTimeZone(timeZone);
		return dateFormat.format(date);
	}
}
