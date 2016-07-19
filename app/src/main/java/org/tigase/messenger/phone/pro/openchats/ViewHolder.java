/*
 * ViewHolder.java
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

package org.tigase.messenger.phone.pro.openchats;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import org.tigase.messenger.phone.pro.R;

public class ViewHolder extends RecyclerView.ViewHolder {

	TextView mContactName;

	TextView mLastMessage;

	ImageView mContactAvatar;

	ImageView mStatus;

	ImageView mDeliveryStatus;

	public ViewHolder(View itemView) {
		super(itemView);

		this.mContactName = (TextView) itemView.findViewById(R.id.contact_display_name);
		this.mLastMessage = (TextView) itemView.findViewById(R.id.last_message);
		this.mContactAvatar = (ImageView) itemView.findViewById(R.id.contact_avatar);
		this.mStatus = (ImageView) itemView.findViewById(R.id.contact_presence);
		this.mDeliveryStatus = (ImageView) itemView.findViewById(R.id.chat_delivery_status);
	}

	public void setContextMenu(final int menuId, final PopupMenu.OnMenuItemClickListener menuClick) {
		itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				PopupMenu popup = new PopupMenu(itemView.getContext(), itemView);
				popup.inflate(menuId);
				popup.setOnMenuItemClickListener(menuClick);
				popup.show();
				return true;
			}
		});

	}

}
