package de.wingesas.android.xbmcMusic.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import de.wingesas.android.xbmcMusic.MainApplication;
import de.wingesas.android.xbmcMusic.R;

@TargetApi(11)
public class HostPreference extends DialogPreference {

	private EditText editHostAddress;
	private EditText editHostPort;
	private EditText editHostUsername;
	private EditText editHostPassword;

	public HostPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPersistent(false);
		setDialogLayoutResource(R.layout.host_preference);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		editHostAddress = (EditText) view.findViewById(R.id.edit_host_address);
		editHostPort = (EditText) view.findViewById(R.id.edit_host_port);
		editHostUsername = (EditText) view.findViewById(R.id.edit_host_username);
		editHostPassword = (EditText) view.findViewById(R.id.edit_host_password);

		SharedPreferences sharedPref = getSharedPreferences();
		editHostAddress.setText(sharedPref.getString(PreferenceActivity.HOST_ADDRESS,
				PreferenceActivity.HOST_ADDRESS_DEFAULT));
		editHostPort.setText(sharedPref.getString(PreferenceActivity.HOST_PORT, PreferenceActivity.HOST_PORT_DEFAULT));
		editHostUsername.setText(sharedPref.getString(PreferenceActivity.HOST_USER_NAME,
				PreferenceActivity.HOST_USER_NAME_DEFAULT));
		editHostPassword.setText(sharedPref.getString(PreferenceActivity.HOST_PASSWORD, PreferenceActivity.HOST_PASSWORD_DEFAULT));
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			Editor editor = getEditor();
			editor.putString(PreferenceActivity.HOST_ADDRESS, editHostAddress.getText().toString());
			editor.putString(PreferenceActivity.HOST_PORT, editHostPort.getText().toString());
			editor.putString(PreferenceActivity.HOST_USER_NAME, editHostUsername.getText().toString());
			editor.putString(PreferenceActivity.HOST_PASSWORD, editHostPassword.getText().toString());
			editor.commit();

			MainApplication app = (MainApplication) getContext().getApplicationContext();
			//app.updateAuthenticator();
		}
	}
}
