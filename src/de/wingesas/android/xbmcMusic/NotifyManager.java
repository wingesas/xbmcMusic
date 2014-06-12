package de.wingesas.android.xbmcMusic;

import android.app.Activity;

public interface NotifyManager {

	public Activity getActivity();

	public void onAsyncTaskStarted();

	public void onAsyncTaskFinished();

	public void onError(Exception e);

	public void onMessage(String message);
}
