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

package app.android.gambit.ui;


import android.app.Activity;
import android.os.Bundle;


final class IntentProcessor
{
	private IntentProcessor() {
	}

	public static Object getMessage(Activity activity) {
		Bundle messageData = activity.getIntent().getExtras();

		Object message = messageData.getParcelable(IntentFactory.MESSAGE_ID);

		if (message == null) {
			throw new IntentCorruptedException();
		}

		return message;
	}
}
