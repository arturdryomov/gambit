package app.android.gambit.remote;


import java.io.IOException;

import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;


class DriveHttpRequestInitializer implements HttpRequestInitializer, HttpUnsuccessfulResponseHandler, HttpExecuteInterceptor
{
	private static final String OAUTH_TOKEN_TYPE = "Bearer";

	private final String authToken;

	public DriveHttpRequestInitializer(String authToken) {
		this.authToken = authToken;
	}

	@Override
	public void initialize(HttpRequest httpRequest) throws IOException {
		httpRequest.setInterceptor(this);
		httpRequest.setUnsuccessfulResponseHandler(this);
	}

	@Override
	public void intercept(HttpRequest httpRequest) throws IOException {
		httpRequest.getHeaders().setAuthorization(buildAuthorizationHeader());
	}

	private String buildAuthorizationHeader() {
		return String.format("%s %s", OAUTH_TOKEN_TYPE, authToken);
	}

	@Override
	public boolean handleResponse(HttpRequest httpRequest, HttpResponse httpResponse, boolean supportsRetry) throws IOException {
		throw buildExceptionFromHttpStatusCode(httpResponse.getStatusCode());
	}

	private RuntimeException buildExceptionFromHttpStatusCode(int httpStatusCode) {
		switch (httpStatusCode) {
			case HttpStatusCodes.STATUS_CODE_NOT_FOUND:
				return new SpreadsheetNotExistsException();

			case HttpStatusCodes.STATUS_CODE_UNAUTHORIZED:
				return new UnauthorizedException();

			default:
				return new SyncException();
		}
	}
}
