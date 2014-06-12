package de.wingesas.android.xbmcMusic.json;

import de.wingesas.android.xbmcMusic.NotifyManager;
import android.os.AsyncTask;
import android.util.Log;

public class JsonTask<T> extends AsyncTask<JsonMethod<T>, Integer, T> {

	private TaskResultListener<T> taskResultListener;

	public interface TaskResultListener<T> {
		public void onTaskComplete(T result);
	}

	private NotifyManager notifyManager;

	private Exception taskException;

	public JsonTask(NotifyManager notifyManager) {
		this.notifyManager = notifyManager;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (notifyManager != null)
			notifyManager.onAsyncTaskStarted();
	}

	@Override
	protected T doInBackground(JsonMethod<T>... params) {

		JsonMethod<T> jsonMethod = params[0];

		T result = null;
		try {
			result = jsonMethod.execute();
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "doInBackground", e);
			this.taskException = e;
		}
		return result;
	}

	@Override
	protected void onPostExecute(T result) {

		if (taskException != null && notifyManager != null)
			notifyManager.onError(this.taskException);
		else
			super.onPostExecute(result);

		if (taskResultListener != null)
			taskResultListener.onTaskComplete(result);

		if (notifyManager != null)
			notifyManager.onAsyncTaskFinished();
	}

	public TaskResultListener<T> getTaskResultListener() {
		return taskResultListener;
	}

	public void setTaskResultListener(TaskResultListener<T> taskResultListener) {
		this.taskResultListener = taskResultListener;
	}
}
