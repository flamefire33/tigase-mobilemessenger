/*
 * MyRosterItemRecyclerViewAdapter.java
 *
 * Tigase Android Messenger
 * Copyright (C) 2011-2016 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */

package org.tigase.messenger.phone.pro.roster;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.tigase.messenger.phone.pro.R;
import org.tigase.messenger.phone.pro.selectionview.CursorMultiSelectViewAdapter;
import org.tigase.messenger.phone.pro.selectionview.MultiSelectFragment;

public class MyRosterItemRecyclerViewAdapter
		extends CursorMultiSelectViewAdapter<ViewHolder> {

	private final Context context;

	public MyRosterItemRecyclerViewAdapter(Context context, Cursor cursor, MultiSelectFragment fragment) {
		super(cursor, fragment);
		this.context = context;
	}

	@Override
	public void onBindViewHolderCursor(final ViewHolder holder, Cursor cursor) {
		holder.bind(cursor);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_rosteritem, parent, false);
		return new ViewHolder(context, view, getFragment());
	}

}
