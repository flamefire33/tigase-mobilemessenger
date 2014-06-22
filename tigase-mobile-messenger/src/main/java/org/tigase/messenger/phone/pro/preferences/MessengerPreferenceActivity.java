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
package org.tigase.messenger.phone.pro.preferences;

import org.tigase.messenger.phone.pro.Constants;
import org.tigase.messenger.phone.pro.Preferences;
import org.tigase.messenger.phone.pro.R;
import org.tigase.messenger.phone.pro.service.ActivityFeature;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;

public class MessengerPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private static final boolean DEBUG = false;

	private static final int MISSING_SETTING = 0;

	private static final int PICK_ACCOUNT = 1;

	private static final String TAG = "tigase";

	private void initSummary(Preference p) {
		if (p.hasKey() && "activity_related".equals(p.getKey())) {
			p.setEnabled(ActivityFeature.isAvailable(this));
		}
		if (p instanceof PreferenceScreen) {
			PreferenceScreen pCat = (PreferenceScreen) p;
			for (int i = 0; i < pCat.getPreferenceCount(); i++) {
				initSummary(pCat.getPreference(i));
			}
		} else if (p instanceof PreferenceCategory) {
			PreferenceCategory pCat = (PreferenceCategory) p;
			for (int i = 0; i < pCat.getPreferenceCount(); i++) {
				initSummary(pCat.getPreference(i));
			}
		} else {
			updateSummary(p.getKey());
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_ACCOUNT) {
			if (data == null || data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME) == null) {
				// this.finish();
				return;
			}

			String accName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			Log.v(TAG, "selected account = " + accName);
			Intent intent = new Intent(this, AccountPreferenceActivity.class);
			intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accName);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@TargetApi(14)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate() " + savedInstanceState);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_preferences);
		super.getPreferenceManager().setSharedPreferencesMode(Context.MODE_MULTI_PROCESS);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			PreferenceScreen accounts = (PreferenceScreen) this.findPreference("accounts_manager");
			accounts.setOnPreferenceClickListener(new OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent intentChooser = AccountManager.newChooseAccountIntent(null, null,
							new String[] { Constants.ACCOUNT_TYPE }, true, null, null, null, null);
					startActivityForResult(intentChooser, PICK_ACCOUNT);
					return true;
				}

			});
		}
		initSummary(getPreferenceScreen());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case MISSING_SETTING: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Please set login data").setCancelable(true).setIcon(android.R.drawable.ic_dialog_alert);
			return builder.create();
		}
		default:
			return null;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "New INtent!!! " + intent);
		super.onNewIntent(intent);
		if (intent.getBooleanExtra("missingLogin", false)) {
			showDialog(MISSING_SETTING);
		}
	}

	@Override
	protected void onPause() {
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		updateSummary(key);
	}

	private void updateSummary(String key) {
		Preference p = findPreference(key);
		if (p instanceof EditTextPreference) {
			final EditTextPreference pref = (EditTextPreference) p;
			if ("reconnect_time".equals(key)) {
				pref.setSummary(getResources().getString(R.string.pref_reconnect_time_summary, pref.getText()));
				this.onContentChanged();
			} else if ("default_priority".equals(key)) {
				pref.setSummary(getResources().getString(R.string.pref_default_priority_summary, pref.getText()));
				this.onContentChanged();
			} else if ("away_priority".equals(key)) {
				pref.setSummary(getResources().getString(R.string.pref_auto_away_priority_summary, pref.getText()));
				this.onContentChanged();
			} else if ("keepalive_time".equals(key)) {
				pref.setSummary(getResources().getString(R.string.pref_keepalive_time_summary, pref.getText()));
				this.onContentChanged();
			} else if (Preferences.ACTIVITY_IN_VEHICLE_DESCR.equals(key)) {
				pref.setSummary(pref.getText());
			}
		}
		if (p instanceof ListPreference) {
			final ListPreference pref = (ListPreference) p;
			if (Preferences.ACTIVITY_IN_VEHICLE_STATUS.equals(key)) {
				pref.setSummary(pref.getEntry());
			}
		}
	}

}
