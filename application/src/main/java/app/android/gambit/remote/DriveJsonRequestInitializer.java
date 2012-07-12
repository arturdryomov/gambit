package app.android.gambit.remote;


import java.io.IOException;

import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.services.drive.DriveRequest;


class DriveJsonRequestInitializer implements JsonHttpRequestInitializer
{
	private final String apiKey;

	public DriveJsonRequestInitializer(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public void initialize(JsonHttpRequest jsonHttpRequest) throws IOException {
		DriveRequest driveRequest = (DriveRequest) jsonHttpRequest;

		driveRequest.setKey(apiKey);
	}
}
