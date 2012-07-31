/*
 * Copyright 2012 Artur Dryomov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.ming13.gambit.remote;


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
