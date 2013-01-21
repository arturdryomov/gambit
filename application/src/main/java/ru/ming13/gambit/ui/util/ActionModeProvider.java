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

package ru.ming13.gambit.ui.util;


import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class ActionModeProvider
{
	public interface ContextMenuHandler
	{
		boolean handleContextMenu(MenuItem menuItem, long listItemId);
	}

	private final ListView listView;
	private final ActionModeListener actionModeListener;

	public static boolean isActionModeAvailable() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static void setUpActionMode(ListView listView, ContextMenuHandler contextMenuHandler, int contextMenuResourceId) {
		new ActionModeProvider(listView, contextMenuHandler, contextMenuResourceId).setUpActionMode();
	}

	private ActionModeProvider(ListView listView, ContextMenuHandler contextMenuHandler, int contextMenuResourceId) {
		this.listView = listView;

		this.actionModeListener = new ActionModeListener(listView, contextMenuHandler,
			contextMenuResourceId);
	}

	private void setUpActionMode() {
		listView.setOnItemLongClickListener(actionModeListener);
	}

	private static final class ActionModeListener implements AdapterView.OnItemLongClickListener
	{
		private final ListView listView;

		private final ContextMenuHandler contextMenuHandler;
		private final int contextMenuResourceId;

		public ActionModeListener(ListView listView, ContextMenuHandler contextMenuHandler, int contextMenuResourceId) {
			this.listView = listView;

			this.contextMenuHandler = contextMenuHandler;
			this.contextMenuResourceId = contextMenuResourceId;
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
			ActionModeCallback actionModeCallback = new ActionModeCallback(contextMenuHandler, id,
				contextMenuResourceId);
			listView.startActionMode(actionModeCallback);

			return true;
		}
	}

	private static final class ActionModeCallback implements ActionMode.Callback
	{
		private final ContextMenuHandler contextMenuHandler;
		private final int contextMenuResourceId;

		private final long listItemId;

		public ActionModeCallback(ContextMenuHandler contextMenuHandler, long listItemId, int contextMenuResourceId) {
			this.contextMenuHandler = contextMenuHandler;
			this.contextMenuResourceId = contextMenuResourceId;

			this.listItemId = listItemId;
		}

		@Override
		public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
			actionMode.getMenuInflater().inflate(contextMenuResourceId, menu);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
			if (contextMenuHandler.handleContextMenu(menuItem, listItemId)) {
				actionMode.finish();

				return true;
			}

			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode actionMode) {
		}
	}
}
