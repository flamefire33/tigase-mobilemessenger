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
package org.tigase.messenger.phone.pro.muc;

import java.util.ArrayList;

import org.tigase.messenger.phone.pro.IJaxmppService;
import org.tigase.messenger.phone.pro.IJaxmppService.Stub;
import org.tigase.messenger.phone.pro.R;
import org.tigase.messenger.phone.pro.account.AccountAuthenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class JoinMucDialog extends DialogFragment {

	public static JoinMucDialog newInstance() {
		Bundle args = new Bundle();
		return newInstance(args);
	}

	public static JoinMucDialog newInstance(Bundle args) {
		JoinMucDialog frag = new JoinMucDialog();
		frag.setArguments(args);
		return frag;
	}

	private IJaxmppService jaxmppService;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog dialog = new Dialog(getActivity());
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		dialog.setContentView(R.layout.join_room_dialog);
		dialog.setTitle(getString(R.string.join_room));

		ArrayList<String> accounts = new ArrayList<String>();
		for (Account account : AccountManager.get(getActivity()).getAccountsByType(AccountAuthenticator.ACCOUNT_TYPE)) {
			accounts.add(account.name);
		}

		final Spinner accountSelector = (Spinner) dialog.findViewById(R.id.muc_accountSelector);
		final Button joinButton = (Button) dialog.findViewById(R.id.muc_joinButton);
		final Button cancelButton = (Button) dialog.findViewById(R.id.muc_cancelButton);
		final TextView name = (TextView) dialog.findViewById(R.id.muc_name);
		final TextView roomName = (TextView) dialog.findViewById(R.id.muc_roomName);
		final TextView mucServer = (TextView) dialog.findViewById(R.id.muc_server);
		final TextView nickname = (TextView) dialog.findViewById(R.id.muc_nickname);
		final TextView password = (TextView) dialog.findViewById(R.id.muc_password);
		final CheckBox autojoin = (CheckBox) dialog.findViewById(R.id.muc_autojoin);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
				accounts.toArray(new String[] {}));
		accountSelector.setAdapter(adapter);

		Bundle data = getArguments();
		final boolean editMode = data != null && data.containsKey("editMode") && data.getBoolean("editMode");
		final String id = data != null ? data.getString("id") : null;

		if (data != null) {
			accountSelector.setSelection(adapter.getPosition(data.getString("account")));

			name.setText(data.getString("name"));
			roomName.setText(data.getString("room"));
			mucServer.setText(data.getString("server"));
			nickname.setText(data.getString("nick"));
			password.setText(data.getString("password"));
			autojoin.setChecked(data.getBoolean("autojoin"));
		}

		if (!editMode) {
			name.setVisibility(View.GONE);
			autojoin.setVisibility(View.GONE);
		} else {
			joinButton.setText("Save");
		}

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		joinButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				if (editMode) {
//
//					BareJID account = BareJID.bareJIDInstance(accountSelector.getSelectedItem().toString());
//					final Jaxmpp jaxmpp = ((MessengerApplication) getActivity().getApplicationContext()).getMultiJaxmpp().get(
//							account);
//
//					Bundle data = new Bundle();
//
//					data.putString("id", id);
//					data.putString("account", account.toString());
//					data.putString("name", name.getText().toString());
//					data.putString("room", roomName.getText().toString());
//					data.putString("server", mucServer.getText().toString());
//					data.putString("nick", nickname.getText().toString());
//					data.putString("password", password.getText().toString());
//					data.putBoolean("autojoin", autojoin.isChecked());
//
//					((BookmarksActivity) getActivity()).saveItem(data);
//
//					dialog.dismiss();
//					return;
//				}
				new Thread() {
					public void run() {
						try {
							jaxmppService.joinRoom(accountSelector.getSelectedItem().toString(), 
								roomName.getEditableText().toString()
									+ "@" + mucServer.getEditableText().toString(), 
								nickname.getEditableText().toString(),
								password.getEditableText().toString(),
								"org.tigase.messenger.phone.pro.MUC_ROOM_JOINED");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
				dialog.dismiss();				
			}
		});

		return dialog;
	}

	public void setJaxmppService(IJaxmppService jaxmppService) {
		this.jaxmppService = jaxmppService;
	}
	
//	public void setAsyncTask(AsyncTask<Room, Void, Void> r) {
//		this.task = r;
//	}
}
