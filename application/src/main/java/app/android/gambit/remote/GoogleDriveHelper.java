package app.android.gambit.remote;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.android.gambit.InternetDateTime;
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveRequest;
import com.google.api.services.drive.model.File;


public class GoogleDriveHelper
{
	public static final String MIME_XLS = "application/vnd.ms-excel";
	public static final String MIME_GOOGLE_SPREADSHEET = "application/vnd.google-apps.spreadsheet";

	private Drive driveService;

	public GoogleDriveHelper(String authToken, String apiKey) {
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

	public String createSpreadsheetFromXlsData(String spreadsheetName, InputStream data) {
		File file = buildSpreadsheetFile(spreadsheetName);
		return createSpreadsheetFromXlsData(file, data);
	}

	private File buildSpreadsheetFile(String spreadsheetName) {
		File file = new File();

		file.setTitle(spreadsheetName);
		file.setMimeType(MIME_GOOGLE_SPREADSHEET);

		return file;
	}

	private String createSpreadsheetFromXlsData(File file, InputStream data) {
		try {
			// TODO: This should be implemented with a single call to insert().
			// No additional upload should be used. But insert(file, content)
			// won't work for some reason, it throws IllegalArgumentException somewhere
			// in MediaHttpUploader.
			File insertedFile = driveService.files().insert(file).execute();
			uploadXlsData(insertedFile, data);
			return insertedFile.getId();
		}
		catch (IOException e) {
			throw new SyncException();
		}
	}

	private void uploadXlsData(File file, InputStream data) {
		try {
			AbstractInputStreamContent fileContent = new InputStreamContent(MIME_XLS, data);
			driveService.files().update(file.getId(), file, fileContent);
		}
		catch (IOException e) {
			throw new SyncException();
		}
	}

	public void uploadXlsData(String spreadsheetKey, InputStream data) {
		File file = getFileByKey(spreadsheetKey);
		uploadXlsData(file, data);
	}

	private File getFileByKey(String spreadsheetKey) {
		try {
			return driveService.files().get(spreadsheetKey).execute();
		}
		catch (IOException e) {
			throw new SyncException();
		}
	}

	public InputStream downloadXlsData(String spreadsheetKey) {
		File file = getFileByKey(spreadsheetKey);
		GenericUrl xlsExportUrl = getXlsExportUrl(file);
		return downloadFileContent(xlsExportUrl);
	}

	private GenericUrl getXlsExportUrl(File file) {
		if (!file.getExportLinks().keySet().contains(MIME_XLS)) {
			throw new SyncException("No XLS export for file provided");
		}

		return new GenericUrl(file.getExportLinks().get(MIME_XLS));
	}

	private InputStream downloadFileContent(GenericUrl contentsUrl) {
		try {
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
			throw new SyncException(String.format("No spreadsheets with name '%s'", spreadsheetName));
		}

		Collections.sort(spreadsheetsByName, Collections.reverseOrder(
			new FileByModifiedDateComparator()));

		return spreadsheetsByName.get(0).getId();
	}

	private List<File> getSpreadsheetsByName(String spreadsheetName) {
		try {
			Drive.Files.List listRequest = driveService.files().list();
			listRequest.setQ(buildFileSelectionQuery(spreadsheetName));
			return listRequest.execute().getItems();
		}
		catch (IOException e) {
			throw new SyncException();
		}
	}

	private String buildFileSelectionQuery(String spreadsheetName) {
		StringBuilder queryBuilder = new StringBuilder();

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
		File file = getFileByKey(spreadsheetKey);
		return new InternetDateTime(file.getModifiedDate().toStringRfc3339());
	}
}
