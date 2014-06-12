package de.wingesas.android.xbmcMusic.fragments;

import de.wingesas.android.xbmcMusic.MainApplication;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

@TargetApi(11)
public class PlaylistActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(0, ActionBar.DISPLAY_USE_LOGO);
		actionBar.setDisplayHomeAsUpEnabled(true);

		setProgressBarIndeterminateVisibility(false);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PlaylistFragment()).commit();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			MainApplication app = (MainApplication) getApplication();
			return app.handleVolumeKeyEvent("KeyDown", keyCode, this);
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			MainApplication app = (MainApplication) getApplication();
			return app.handleVolumeKeyEvent("KeyUp", keyCode, this);
		} else
			return super.onKeyUp(keyCode, event);
	}
}
