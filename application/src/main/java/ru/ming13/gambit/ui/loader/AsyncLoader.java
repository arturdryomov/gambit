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

package ru.ming13.gambit.ui.loader;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import org.apache.commons.lang.StringUtils;
import ru.ming13.gambit.ui.loader.result.LoaderResult;
import ru.ming13.gambit.ui.loader.result.LoaderStatus;


abstract class AsyncLoader<Data> extends AsyncTaskLoader<LoaderResult<Data>>
{
	protected AsyncLoader(Context context) {
		super(context);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	protected LoaderResult<Data> buildSuccessResult(Data data) {
		return new LoaderResult<Data>(LoaderStatus.SUCCESS, data, StringUtils.EMPTY);
	}

	protected LoaderResult<Data> buildErrorResult(Data data, String errorMessage) {
		return new LoaderResult<Data>(LoaderStatus.ERROR, data, errorMessage);
	}
}
