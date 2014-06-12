package de.wingesas.android.xbmcMusic.preference;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.Window;
import de.wingesas.android.xbmcMusic.HomeActivity;
import de.wingesas.android.xbmcMusic.MainActivity;
import de.wingesas.android.xbmcMusic.R;

@TargetApi(11)
public class PreferenceActivity extends Activity {

	public static final String PLAYBACK_MODE = "PLAYBACK_MODE";
	public static final String PLAY = "PLAY";
	public static final String QUEUE = "QUEUE";

	public static final String HOST_ADDRESS = "HOST_ADDRESS";
	public static final String HOST_ADDRESS_DEFAULT = "192.168.0.1";
	public static final String HOST_PORT = "HOST_PORT";
	public static final String HOST_PORT_DEFAULT = "8080";
	public static final String HOST_USER_NAME = "HOST_USER_NAME";
	public static final String HOST_USER_NAME_DEFAULT = "xbmc";
	public static final String HOST_PASSWORD = "HOST_PASSWORD";
	public static final String HOST_PASSWORD_DEFAULT = "";
	
	public static final String LAUNCH_COUNTER = "LAUNCH_COUNTER";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(0, ActionBar.DISPLAY_USE_LOGO);
		actionBar.setDisplayHomeAsUpEnabled(true);

		getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			Bundle extras = getIntent().getExtras();

			if (extras != null && extras.getString(MainActivity.INTENT_SOURCE).equals(MainActivity.class.getSimpleName())) {
				Intent intent = new Intent(this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
			addPreferencesFromResource(R.xml.preferences);
		}
	}
}
