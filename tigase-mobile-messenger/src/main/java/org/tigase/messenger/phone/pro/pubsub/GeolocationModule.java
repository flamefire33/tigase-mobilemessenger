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
package org.tigase.messenger.phone.pro.pubsub;

import java.util.Date;
import java.util.List;

import org.tigase.messenger.phone.pro.db.DatabaseHelper;
import org.tigase.messenger.phone.pro.db.GeolocationTableMetaData;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.JaxmppCore;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.XmppModule;
import tigase.jaxmpp.core.client.criteria.Criteria;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.pubsub.PubSubModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GeolocationModule implements XmppModule {

	public static final String XMLNS = "http://jabber.org/protocol/geoloc";

	public static final String[] FEATURES = { XMLNS + "+notify" };

	private static final String TAG = "GeolocationModule";

	private static final String WHERE_BY_JID = GeolocationTableMetaData.FIELD_JID + " = ? ";

	private DatabaseHelper dbHelper;

	private PubSubModule.NotificationReceivedHandler handler;
	
	public GeolocationModule(DatabaseHelper helper) {
		dbHelper = helper;
	}

	public void deinit(JaxmppCore jaxmpp) {
		if (handler == null)
			return;
		jaxmpp.getEventBus().remove(handler);
		handler = null;
	}

	@Override
	public Criteria getCriteria() {
		return null;
	}

	@Override
	public String[] getFeatures() {
		return FEATURES;
	}

	public ContentValues getLocationForJid(BareJID jid) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues location = null;
		final Cursor c = db.query(GeolocationTableMetaData.TABLE_NAME, new String[] { GeolocationTableMetaData.FIELD_LON,
				GeolocationTableMetaData.FIELD_LAT, GeolocationTableMetaData.FIELD_ALT, GeolocationTableMetaData.FIELD_COUNTRY,
				GeolocationTableMetaData.FIELD_LOCALITY, GeolocationTableMetaData.FIELD_STREET }, WHERE_BY_JID,
				new String[] { jid.toString() }, null, null, null);
		try {
			while (c.moveToNext()) {
				location = new ContentValues();

				int col = c.getColumnIndex(GeolocationTableMetaData.FIELD_LON);
				if (!c.isNull(col)) {
					location.put(GeolocationTableMetaData.FIELD_LON, c.getDouble(col));
				}
				col = c.getColumnIndex(GeolocationTableMetaData.FIELD_LAT);
				if (!c.isNull(col)) {
					location.put(GeolocationTableMetaData.FIELD_LAT, c.getDouble(col));
				}
				col = c.getColumnIndex(GeolocationTableMetaData.FIELD_ALT);
				if (!c.isNull(col)) {
					location.put(GeolocationTableMetaData.FIELD_ALT, c.getDouble(col));
				}
				col = c.getColumnIndex(GeolocationTableMetaData.FIELD_COUNTRY);
				location.put(GeolocationTableMetaData.FIELD_COUNTRY, c.getString(col));
				col = c.getColumnIndex(GeolocationTableMetaData.FIELD_LOCALITY);
				location.put(GeolocationTableMetaData.FIELD_LOCALITY, c.getString(col));
				col = c.getColumnIndex(GeolocationTableMetaData.FIELD_STREET);
				location.put(GeolocationTableMetaData.FIELD_STREET, c.getString(col));
			}
		} finally {
			c.close();
		}
		return location;
	}

	public void init(final JaxmppCore jaxmpp) {
		if (handler != null)
			return;

		handler = new PubSubModule.NotificationReceivedHandler() {
			@Override
			public void onNotificationReceived(SessionObject sessionObject,
					Message message, JID pubSubJID, String node,
					String itemId, Element geoloc, Date delayTime,
					String itemType) {
				try {
					if (!XMLNS.equals(node))
						return;
	
					Log.v(TAG, "got payload = " + geoloc);
					if (geoloc == null || !"geoloc".equals(geoloc.getName()))
						return;
	
					List<Element> children = geoloc.getChildren();
					if (children == null || children.isEmpty()
							|| (message != null && message.getType() == StanzaType.error)) {
						removeLocalityForJid(pubSubJID.getBareJid());
					} else {
						ContentValues locality = new ContentValues();
						locality.put(GeolocationTableMetaData.FIELD_LON, (Double) null);
						locality.put(GeolocationTableMetaData.FIELD_LAT, (Double) null);
						locality.put(GeolocationTableMetaData.FIELD_ALT, (Double) null);
						locality.put(GeolocationTableMetaData.FIELD_COUNTRY, (String) null);
						locality.put(GeolocationTableMetaData.FIELD_LOCALITY, (String) null);
						locality.put(GeolocationTableMetaData.FIELD_STREET, (String) null);
						for (Element el : children) {
							if ("lon".equals(el.getName())) {
								String val = el.getValue();
								if (val != null && val.length() > 0) {
									locality.put(GeolocationTableMetaData.FIELD_LON, Double.parseDouble(val));
								}
							} else if ("lat".equals(el.getName())) {
								String val = el.getValue();
								if (val != null && val.length() > 0) {
									locality.put(GeolocationTableMetaData.FIELD_LAT, Double.parseDouble(val));
								}
							} else if ("alt".equals(el.getName())) {
								String val = el.getValue();
								if (val != null && val.length() > 0) {
									locality.put(GeolocationTableMetaData.FIELD_ALT, Double.parseDouble(val));
								}
							} else if ("country".equals(el.getName())) {
								String val = el.getValue();
								if (val != null && val.length() > 0) {
									locality.put(GeolocationTableMetaData.FIELD_COUNTRY, val);
								}
							} else if ("locality".equals(el.getName())) {
								String val = el.getValue();
								if (val != null && val.length() > 0) {
									locality.put(GeolocationTableMetaData.FIELD_LOCALITY, val);
								}
							} else if ("street".equals(el.getName())) {
								String val = el.getValue();
								if (val != null && val.length() > 0) {
									locality.put(GeolocationTableMetaData.FIELD_STREET, val);
								}
							}
						}
						setLocalityForJid(pubSubJID.getBareJid(), locality);
					}
				} catch (JaxmppException ex) {
					Log.e(TAG, "Exception processing geolocation notification", ex);
				}
			}
		};
		jaxmpp.getEventBus().addHandler(PubSubModule.NotificationReceivedHandler.NotificationReceivedEvent.class, handler);
	}

	@Override
	public void process(Element element) throws XMPPException, XMLException, JaxmppException {
	}

	public void removeLocalityForJid(BareJID jid) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete(GeolocationTableMetaData.TABLE_NAME, WHERE_BY_JID, new String[] { jid.toString() });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void setLocalityForJid(BareJID jid, ContentValues locality) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		locality.put(GeolocationTableMetaData.FIELD_JID, jid.toString());
		try {
			int updated = db.update(GeolocationTableMetaData.TABLE_NAME, locality, WHERE_BY_JID,
					new String[] { jid.toString() });
			if (updated == 0) {
				db.insert(GeolocationTableMetaData.TABLE_NAME, null, locality);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
}
