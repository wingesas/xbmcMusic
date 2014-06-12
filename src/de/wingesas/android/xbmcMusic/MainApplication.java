package de.wingesas.android.xbmcMusic;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.apache.http.HttpException;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.KeyEvent;
import de.wingesas.android.xbmcMusic.json.VolumeManager;
import de.wingesas.android.xbmcMusic.preference.PreferenceActivity;

public class MainApplication extends Application {

	private static Context context = null;

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
	}

	public boolean handleVolumeKeyEvent(String keyAction, int keyCode, Context context) {

		if (keyAction.equals("KeyDown")) {
			VolumeManager.Action action = (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) ? VolumeManager.Action.VOLUME_DOWN
					: VolumeManager.Action.VOLUME_UP;
			new VolumeManager(context).execute(new VolumeManager.Action[] { action });

			return true;
		} else if (keyAction.equals("KeyUp")) {
			new VolumeManager(context).execute(new VolumeManager.Action[] { VolumeManager.Action.GET_VOLUME });
			return true;
		} else
			return false;
	}

	public static void onErrorHandler(Context context, Exception e) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setPositiveButton(android.R.string.ok, null);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String host = pref.getString(PreferenceActivity.HOST_ADDRESS, PreferenceActivity.HOST_ADDRESS_DEFAULT);
		String port = pref.getString(PreferenceActivity.HOST_PORT, PreferenceActivity.HOST_PORT_DEFAULT);

		if (e instanceof IOException) {
			builder.setTitle(R.string.timeout_error);
			builder.setMessage(String.format(context.getResources().getString(R.string.connect_failure), host, port));
			builder.show();
		} else if (e instanceof HttpException && e.getMessage() != null && e.getMessage().startsWith("401")) {
			builder.setTitle(R.string.authorization_error);
			builder.setMessage(R.string.user_password_not_valid);
			builder.show();
		} else if (e instanceof JSONException) {
			builder.setTitle(R.string.json_exception_title);
			builder.setMessage(R.string.json_exception_message);
			builder.show();
		} else {
			builder.setTitle(e.getClass().toString());
			builder.setMessage(e.getLocalizedMessage());
			builder.show();
		}
	}

	public void updateAuthenticatorX() {
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		Authenticator.setDefault(new Authenticator() {
			private int i = 0;
			private int max = 5;

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				String userName = pref.getString(PreferenceActivity.HOST_USER_NAME,
						PreferenceActivity.HOST_USER_NAME_DEFAULT);
				String password = pref.getString(PreferenceActivity.HOST_PASSWORD,
						PreferenceActivity.HOST_PASSWORD_DEFAULT);

				if (i < max) {
					i++;
					System.out.println("hallo");
					return new PasswordAuthentication(userName, password.toCharArray());
				}
				return null;
			}
		});
	}

	public static String getHostAddress() {
		return getSharedPreference(PreferenceActivity.HOST_ADDRESS, PreferenceActivity.HOST_ADDRESS_DEFAULT);
	}

	public static String getHostPort() {
		return getSharedPreference(PreferenceActivity.HOST_PORT, PreferenceActivity.HOST_PORT_DEFAULT);
	}

	public static String getAuthorization() {
		String userName = getSharedPreference(PreferenceActivity.HOST_USER_NAME,
				PreferenceActivity.HOST_USER_NAME_DEFAULT);
		String password = getSharedPreference(PreferenceActivity.HOST_PASSWORD,
				PreferenceActivity.HOST_PASSWORD_DEFAULT);

		return "Basic " + Base64.encodeToString((userName + ":" + password).getBytes(), Base64.NO_WRAP);
	}

	private static String getSharedPreference(String key, String defaultValue) {

		if (context != null) {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			return sharedPref.getString(key, defaultValue);
		} else
			return null;
	}
}
