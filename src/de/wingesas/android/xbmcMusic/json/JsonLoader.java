package de.wingesas.android.xbmcMusic.json;

import android.content.AsyncTaskLoader;
import android.content.Context;
import de.wingesas.android.xbmcMusic.NotifyManager;

public class JsonLoader<T> extends AsyncTaskLoader<T> {

	private JsonMethod<T> jsonMethod;
	private Exception e;
	private NotifyManager notifyManager;

	public JsonLoader(Context context, JsonMethod<T> jsonMethod) {
		super(context);
		this.jsonMethod = jsonMethod;
	}

	public JsonLoader(NotifyManager notifyManager, JsonMethod<T> jsonMethod) {
		super(notifyManager.getActivity());
		this.notifyManager = notifyManager;
		this.jsonMethod = jsonMethod;
	}

	@Override
	public final T loadInBackground() {

		T result = null;
		try {
			result = jsonMethod.execute();
		} catch (Exception e) {
			this.e = e;
		}
		return result;
	}

	@Override
	public void deliverResult(T data) {
		if (e != null && notifyManager!=null)
			notifyManager.onError(e);
		else
			super.deliverResult(data);
	}
}
