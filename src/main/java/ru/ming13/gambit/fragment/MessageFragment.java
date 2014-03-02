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

package ru.ming13.gambit.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.ming13.gambit.R;
import ru.ming13.gambit.util.Fragments;

public class MessageFragment extends Fragment
{
	public static MessageFragment newInstance(String message) {
		MessageFragment fragment = new MessageFragment();

		fragment.setArguments(buildArguments(message));

		return fragment;
	}

	private static Bundle buildArguments(String message) {
		Bundle arguments = new Bundle();

		arguments.putString(Fragments.Arguments.MESSAGE, message);

		return arguments;
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_message, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpMessage();
	}

	private void setUpMessage() {
		TextView messageView = (TextView) getView().findViewById(R.id.text_message);
		messageView.setText(getMessage());
	}

	private String getMessage() {
		return getArguments().getString(Fragments.Arguments.MESSAGE);
	}
}