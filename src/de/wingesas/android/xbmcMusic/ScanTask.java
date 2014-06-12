package de.wingesas.android.xbmcMusic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import de.wingesas.android.xbmcMusic.data.ArtistItem;
import de.wingesas.android.xbmcMusic.data.FileSystemItem;
import de.wingesas.android.xbmcMusic.data.SongItem;
import de.wingesas.android.xbmcMusic.json.methods.AudioLibraryGetArtists;
import de.wingesas.android.xbmcMusic.json.methods.AudioLibraryGetSongs;
import de.wingesas.android.xbmcMusic.json.methods.FilesGetDirectory;

import android.os.AsyncTask;
import android.util.Log;

public class ScanTask extends AsyncTask<Void, Void, Void> {

	@Override
	protected Void doInBackground(Void... params) {

		ExecutorService pool = Executors.newFixedThreadPool(2);
		CompletionService<List<FileSystemItem>> completionService = new ExecutorCompletionService<List<FileSystemItem>>(
				pool);

		List<Callable<List<FileSystemItem>>> callables = new ArrayList<Callable<List<FileSystemItem>>>();

		long startTime = System.currentTimeMillis();

		List<FileSystemItem> items;
		try {
			items = new FilesGetDirectory(null).execute();

			for (FileSystemItem item : items) {

				final String parent = item.getFile();

				Callable<List<FileSystemItem>> callable = new Callable<List<FileSystemItem>>() {
					@Override
					public List<FileSystemItem> call() throws Exception {

						try {
							return new FilesGetDirectory(parent).execute();
						} catch (Exception e) {
							return null;
						}
					}
				};
				callables.add(callable);
				completionService.submit(callable);
			}

			long k = items.size();

			for (int i = 0; i < callables.size(); ++i) {
				final List<FileSystemItem> result = completionService.take().get();

				if (result != null) {
					k += result.size();
					if (k % 100 == 0) {
						Log.d(getClass().getSimpleName().toString(), "k: " + String.valueOf(k));
						Log.d(getClass().getSimpleName().toString(), "callables.size " + String.valueOf(callables.size()));
						//Log.d(getClass().getSimpleName().toString(), String.valueOf((System.currentTimeMillis() - startTime)));
					}

					for (FileSystemItem item : result) {
						if (item.isDirectory()) {
							final String parent = item.getFile();
							// Log.d(getClass().getSimpleName().toString(),
							// parent);

							Callable<List<FileSystemItem>> callable = new Callable<List<FileSystemItem>>() {
								@Override
								public List<FileSystemItem> call() throws Exception {
									return new FilesGetDirectory(parent).execute();
								}
							};
							callables.add(callable);
							completionService.submit(callable);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pool.shutdown();
		long endTime = System.currentTimeMillis();
		System.out.println((endTime - startTime) + " milliseconds");

		// http://stackoverflow.com/questions/4912228/when-should-i-use-a-completionservice-over-an-executorservice
		// http://stackoverflow.com/questions/4958330/java-executorservice-awaittermination-of-all-recursively-created-tasks
		// System.out.println(String.valueOf(x));

		return null;
	}

}
