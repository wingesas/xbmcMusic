package de.wingesas.android.xbmcMusic.json;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import de.wingesas.android.xbmcMusic.json.methods.ApplicationVolume;

public class VolumeManager extends AsyncTask<VolumeManager.Action, String, Void> {

	private static final int VOLUME_MIN = 0;
	private static final int VOLUME_MAX = 100;
	private static final int VOLUME_STEP = VOLUME_MAX / 20;

	private Context context;

	public enum Action {
		VOLUME_UP, VOLUME_DOWN, GET_VOLUME
	}

	public VolumeManager(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(Action... params) {

		try {
			Integer volume = new ApplicationVolume(ApplicationVolume.Action.GET).execute();

			Action action = params[0];
			int step = volume % VOLUME_STEP != 0 ? volume % VOLUME_STEP : VOLUME_STEP;

			switch (action) {
			case GET_VOLUME:
				publishProgress("volume is " + String.valueOf(volume));
				break;

			case VOLUME_UP:
				if (volume < VOLUME_MAX)
					new ApplicationVolume(ApplicationVolume.Action.SET, volume + step).execute();
				break;

			case VOLUME_DOWN:
				if (volume > VOLUME_MIN)
					new ApplicationVolume(ApplicationVolume.Action.SET, volume - step).execute();
				break;

			default:
				break;
			}

		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getLocalizedMessage());
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		Toast.makeText(context, values[0], Toast.LENGTH_SHORT).show();
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
	}
}
