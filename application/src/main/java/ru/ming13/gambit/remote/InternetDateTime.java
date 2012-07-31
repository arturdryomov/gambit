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


import java.util.Date;

import android.text.format.Time;


public class InternetDateTime
{
	private final Time time;

	public InternetDateTime() {
		time = new Time(Time.TIMEZONE_UTC);
		time.setToNow();
	}

	public InternetDateTime(String dateTimeAsString) {
		time = new Time(Time.TIMEZONE_UTC);
		time.parse3339(dateTimeAsString);
	}

	public InternetDateTime(Date dateTimeAsUtcDate) {
		time = new Time(Time.TIMEZONE_UTC);
		time.set(dateTimeAsUtcDate.getTime());
	}

	@Override
	public String toString() {
		return time.format3339(false);
	}

	public Date toDate() {
		return new Date(time.toMillis(false));
	}

	public boolean isAfter(InternetDateTime dateTime) {
		return time.after(dateTime.time);
	}

	public boolean isBefore(InternetDateTime dateTime) {
		return time.before(dateTime.time);
	}
}
