/*
 * Tigase Mobile Messenger for Android
 * Copyright (C) 2011-2013 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
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

import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Client side presence
 */
public class CPresence implements Parcelable {
	
	public static final int OFFLINE = 0;
	public static final int ERROR = 1;
	public static final int DND = 5;
	public static final int XA = 10;
	public static final int AWAY = 15;
	public static final int ONLINE = 20;
	public static final int CHAT = 25;
	
	private final String resource;
	private final Integer priority;
	private final int status;
	private final String description;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(resource);
		dest.writeInt(priority == null ? -1000 : priority);
		dest.writeInt(status);
		dest.writeString(description);
	}

	public static final Parcelable.Creator<CPresence> CREATOR = new Parcelable.Creator<CPresence>() {
		public CPresence createFromParcel(Parcel in) {
			return new CPresence(in);
		}

		public CPresence[] newArray(int size) {
			return new CPresence[size];
		}
	};
	
	private CPresence(Parcel in) {
		resource = in.readString();
		int priority = in.readInt();
		if (priority < -1000) 
			this.priority = 0;
		else
			this.priority = priority;
		status = in.readInt();
		description = in.readString();
	}
	
	public CPresence(String resource, Integer priority, int status, String description) {
		this.resource = resource;
		this.priority = priority;
		this.status = status;
		this.description = description;
	}
	
	public CPresence(Presence p) throws XMLException {
		this.resource = p.getFrom().getResource();
		this.priority = p.getPriority();
		this.status = getStatusFromPresence(p);
		this.description = p.getStatus();		
	}
	
	public String getResource() {
		return resource;
	}

	public Integer getPriority() {
		return priority;
	}

	public int getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

	public boolean isAvailable() {
		return status > 0;
	}

	public static int getStatusFromPresence(Presence p) throws XMLException {
		int status = p.getType() == null ? Presence.Show.online.ordinal() : 0;
		if (status > 0) {
			status = p.getShow().ordinal();
		}
		if (p.getType() == StanzaType.error) {
			status = 1;
		}
		return status * 5;		
	}
	
}
