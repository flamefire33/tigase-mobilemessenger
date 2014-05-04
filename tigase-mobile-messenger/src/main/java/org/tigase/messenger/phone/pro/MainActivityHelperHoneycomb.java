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
package org.tigase.messenger.phone.pro;

import java.util.List;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(11)
public class MainActivityHelperHoneycomb extends MainActivityHelper {

	private class Holder {
		TextView description;
		ImageView status;
		TextView title;
	}

	protected static final String TAG = "MainActivityHelperHoneycomb";

	protected MainActivityHelperHoneycomb(MainActivity activity) {
		super(activity);
	}

	@Override
	public void invalidateOptionsMenu() {
		activity.invalidateOptionsMenu();
	}

	@Override
	public void setShowAsAction(MenuItem item, int actionEnum) {
		item.setShowAsAction(actionEnum);
	}

	@Override
	public void updateActionBar(Fragment frag) {
//		activity.drawerLayout.post(new Runnable() {
//
//			@Override
//			public void run() {
				// TODO Auto-generated method stub
//				int currentPage = activity.getCurrentPage();

				boolean isMain = activity.isMainView();
				
				Log.v(TAG, "updating ActionBar - isMain: " + isMain);
				
				ActionBar actionBar = activity.getActionBar();
				actionBar.setDisplayHomeAsUpEnabled(true);//isMain);
				if (isMain) {
					activity.drawerLayout.setDrawerListener(activity.drawerToggle);
				}
				else {
					activity.drawerLayout.setDrawerListener(null);
				}
				
////				if (currentPage != 1 && !isXLarge()) {
//					activity.drawerLayout.setDrawerListener(null);
//					activity.drawerToggle.setDrawerIndicatorEnabled(false);
//				} else {
//					activity.drawerLayout.setDrawerListener(activity.drawerToggle);
					activity.drawerToggle.setDrawerIndicatorEnabled(isMain);
//				}
				actionBar.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.actionbar_background));

				if (frag instanceof CustomHeader) {
					CustomHeader cheader = (CustomHeader) frag;
					View cview = actionBar.getCustomView();
					actionBar.setDisplayShowCustomEnabled(true);
					int id = cheader.getHeaderViewId();
					if (cview == null || cview.getId() != id) {
						actionBar.setCustomView(id);
						cview = actionBar.getCustomView();
					}
					cheader.updateHeaderView(cview);
				}
				else {
					actionBar.setDisplayShowCustomEnabled(false);
				}
				// actionBar.setHomeButtonEnabled(true);
				// actionBar.setDisplayHomeAsUpEnabled(currentPage != 1 &&
				// !isXLarge());
				
				

				// Setting subtitle to show who we chat with
//				ChatWrapper c = activity.getChatByPageIndex(currentPage);
//				if (c != null) {
//					// actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE,
//					// ActionBar.DISPLAY_SHOW_TITLE);
//					actionBar.setSubtitle(null);
//					actionBar.setDisplayShowCustomEnabled(true);
//					View view = actionBar.getCustomView();
//					if (view == null) {
//						actionBar.setCustomView(R.layout.actionbar_status);
//						view = actionBar.getCustomView();
//					}
//					String subtitle = null;
//					int icon = 0;
//					if (c.getChat() != null) {
//						BareJID jid = c.getChat().getJid().getBareJid();
//						RosterItem ri = c.getChat().getSessionObject().getRoster().get(jid);
//						subtitle = "Chat with " + (ri != null ? ri.getName() : jid.toString());
//
//						icon = R.drawable.user_offline;
//						CPresence p = RosterDisplayTools.getShowOf(c.getChat().getSessionObject(),
//								c.getChat().getJid().getBareJid());
//						c.getChat().getSessionObject().getPresence().getPresence(c.getChat().getJid());
//						switch (p) {
//						case chat:
//							icon = R.drawable.user_free_for_chat;
//							break;
//						case online:
//							icon = R.drawable.user_available;
//							break;
//						case away:
//							icon = R.drawable.user_away;
//							break;
//						case xa:
//							icon = R.drawable.user_extended_away;
//							break;
//						case dnd:
//							icon = R.drawable.user_busy;
//							break;
//						default:
//							break;
//						}
//					} else if (c.getRoom() != null) {
//						subtitle = "Room " + c.getRoom().getRoomJid().toString();
//						icon = R.drawable.user_offline;
//
//						if (c.getRoom().getState() == State.joined) {
//							icon = R.drawable.user_available;
//						}
//					}
//					// actionBar.setSubtitle(subtitle);
//					if (view != null) {
//						Holder holder = (Holder) view.getTag();
//						if (holder == null) {
//							holder = new Holder();
//							holder.title = (TextView) view.findViewById(R.id.title);
//							holder.description = (TextView) view.findViewById(R.id.description);
//							holder.status = (ImageView) view.findViewById(R.id.status);
//							view.setTag(holder);
//						}
//						holder.title.setText(actionBar.getTitle());
//						holder.description.setText(subtitle);
//						holder.status.setImageResource(icon);
//					}
//				} else {
//					actionBar.setDisplayShowCustomEnabled(false);
//					if (currentPage == 0) {
//						actionBar.setSubtitle("Accounts");
//					} else {
//						actionBar.setSubtitle(null);
//					}
//				}
//			}
//
//		});
	}

	@Override
	public void updateActionBar(int itemHashCode) {
//		List<ChatWrapper> chats = activity.getChatList();
//		for (int i = 0; i < chats.size(); i++) {
//			ChatWrapper chat = chats.get(i);
//			if (chat.hashCode() == itemHashCode) {
//				updateActionBar();
//				return;
//			}
//		}
	}
}
