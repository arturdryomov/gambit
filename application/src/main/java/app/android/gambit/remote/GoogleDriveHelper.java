package app.android.gambit.remote;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.android.gambit.InternetDateTime;
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveRequest;
import com.google.api.services.drive.model.File;


public class GoogleDriveHelper
{
	private static final String MIME_XLS = "application/vnd.ms-excel";
	private static final String MIME_GOOGLE_SPREADSHEET = "application/vnd.google-apps.spreadsheet";
	private static final String OAUTH_TOKEN_PARAM = "oauth_token";

	private static final String FIELD_ID = "id";
	private static final String FIELD_EXPORT_LINKS = "exportLinks";
	private static final String FIELD_MODIFIED_DATE = "modifiedDate";

	private static final String FIELD_LIST_PREFIX = "items";

	private Drive driveService;

	private String authToken;

	public GoogleDriveHelper(String authToken, String apiKey) {
		this.authToken = authToken;

		driveService = buildDriveService(authToken, apiKey);
	}

	private Drive buildDriveService(String authToken, String apiKey) {
		HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		JsonHttpRequestInitializer requestInitializer = new DriveRequestInitializer(authToken, apiKey);

		Drive.Builder driveServiceBuilder = new Drive.Builder(httpTransport, jsonFactory, null);
		driveServiceBuilder.setJsonHttpRequestInitializer(requestInitializer);

		return driveServiceBuilder.build();
	}

	private static class DriveRequestInitializer implements JsonHttpRequestInitializer
	{
		private final String authToken;
		private final String apiKey;

		public DriveRequestInitializer(String authToken, String apiKey) {
			this.authToken = authToken;
			this.apiKey = apiKey;
		}

		@Override
		public void initialize(JsonHttpRequest jsonHttpRequest) throws IOException {
			DriveRequest driveRequest = (DriveRequest) jsonHttpRequest;

			driveRequest.setOauthToken(authToken);
			driveRequest.setKey(apiKey);
		}
	}

	public String createSpreadsheetFromXlsData(String spreadsheetName, byte[] data) {
		File file = buildSpreadsheetFile(spreadsheetName);
		return createSpreadsheetFromXlsData(file, data);
	}

	private File buildSpreadsheetFile(String spreadsheetName) {
		File file = new File();

		file.setTitle(spreadsheetName);
		file.setMimeType(MIME_XLS);

		return file;
	}

	private String createSpreadsheetFromXlsData(File file, byte[] data) {
		try {
			AbstractInputStreamContent content = contentFromXlsData(data);
			Drive.Files.Insert insertRequest = driveService.files().insert(file, content);

			insertRequest.setConvert(Boolean.TRUE);
			insertRequest.setFields(buildFields(FIELD_ID));

			return insertRequest.execute().getId();
		}
		catch (IOException e) {
			throw new SyncException();
		}
	}

	private AbstractInputStreamContent contentFromXlsData(byte[] data) {
		return new ByteArrayContent(MIME_XLS, data);
	}

	private String buildFields(String... fields) {
		StringBuilder builder = new StringBuilder();

		for (String field : fields) {
			builder.append(field);
			builder.append(",");
		}

		// Remove last extra comma
		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}

	public void uploadXlsData(String spreadsheetKey, byte[] data) {
		try {
			AbstractInputStreamContent content = contentFromXlsData(data);
			Drive.Files.Update updateRequest = driveService.files().update(spreadsheetKey, null, content);

			updateRequest.setConvert(Boolean.TRUE);

			// We actually need nothing, but there can't be empty response
			updateRequest.setFields(buildFields(FIELD_ID));

			updateRequest.execute();
		}
		catch (IOException e) {
			throw new SyncException();
		}
	}

	public InputStream downloadXlsData(String spreadsheetKey) {
		File file = getFileByKey(spreadsheetKey, buildFields(FIELD_EXPORT_LINKS));
		GenericUrl xlsExportUrl = getXlsExportUrl(file);
		return downloadFileContent(xlsExportUrl);
	}

	private File getFileByKey(String spreadsheetKey, String fields) {
		try {
			Drive.Files.Get getRequest = driveService.files().get(spreadsheetKey);
			getRequest.setFields(fields);
			return driveService.files().get(spreadsheetKey).execute();
		}
		catch (HttpResponseException e) {
			throw exceptionFromStatusCode(e.getStatusCode());
		}
		catch (IOException e) {
			throw new SyncException();
		}
	}

	private RuntimeException exceptionFromStatusCode(int statusCode) {
		if (statusCode == HttpStatusCodes.STATUS_CODE_NOT_FOUND) {
			return new FileNotExistsException();
		}
		else {
			return new SyncException();
		}
	}

	private GenericUrl getXlsExportUrl(File file) {
		if (!file.getExportLinks().keySet().contains(MIME_XLS)) {
			throw new SyncException("No XLS export for file provided");
		}

		return new GenericUrl(file.getExportLinks().get(MIME_XLS));
	}

	private InputStream downloadFileContent(GenericUrl contentsUrl) {
		try {
			contentsUrl.set(OAUTH_TOKEN_PARAM, authToken);
			HttpRequest request = driveService.getRequestFactory().buildGetRequest(contentsUrl);
			return request.execute().getContent();
		}
		catch (IOException e) {
			throw new SyncException();
		}
	}

	public String getNewestSpreadsheetKeyByName(String spreadsheetName) {
		List<File> spreadsheetsByName = getSpreadsheetsByName(spreadsheetName);

		if (spreadsheetsByName.isEmpty()) {
			throw new FileNotExistsException();
		}

		Collections.sort(spreadsheetsByName,
			Collections.reverseOrder(new FileByModifiedDateComparator()));

		return spreadsheetsByName.get(0).getId();
	}

	private List<File> getSpreadsheetsByName(String spreadsheetName) {
		try {
			Drive.Files.List listRequest = driveService.files().list();

			listRequest.setQ(buildFileSelectionQuery(spreadsheetName));
			listRequest.setFields(fieldsForList(buildFields(FIELD_ID, FIELD_MODIFIED_DATE)));

			return listRequest.execute().getItems();
		}
		catch (IOException e) {
			throw new SyncException();
		}
	}

	private String buildFileSelectionQuery(String spreadsheetName) {
		StringBuilder queryBuilder = new StringBuilder();

		// TODO: Owner field should also be specified to track only user's files
		// This will require passing Account or something to GoogleDriveHelper
		queryBuilder.append("trashed=false");
		queryBuilder.append(" and ");
		queryBuilder.append(String.format("mimeType='%s'", MIME_GOOGLE_SPREADSHEET));
		queryBuilder.append(" and ");
		queryBuilder.append(String.format("title='%s'", escapeSingleQuote(spreadsheetName)));

		return queryBuilder.toString();
	}

	private String escapeSingleQuote(String string) {
		return string.replace("'", "\\'");
	}

	private String fieldsForList(String fields) {
		return String.format("%s(%s)", FIELD_LIST_PREFIX, fields);
	}

	private static class FileByModifiedDateComparator implements Comparator<File>
	{
		@Override
		public int compare(File first, File second) {
			InternetDateTime firstTime = new InternetDateTime(first.getModifiedDate().toStringRfc3339());
			InternetDateTime secondTime = new InternetDateTime(
				second.getModifiedDate().toStringRfc3339());

			if (firstTime.isBefore(secondTime)) {
				return -1;
			}
			else if (firstTime.isAfter(secondTime)) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}

	public InternetDateTime getSpreadsheetUpdateTime(String spreadsheetKey) {
		File file = getFileByKey(spreadsheetKey, buildFields(FIELD_MODIFIED_DATE));
		return new InternetDateTime(file.getModifiedDate().toStringRfc3339());
	}
}
