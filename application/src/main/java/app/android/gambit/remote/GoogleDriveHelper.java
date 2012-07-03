package app.android.gambit.remote;


import java.io.IOException;

import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveRequest;


class GoogleDriveHelper
{
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
}
