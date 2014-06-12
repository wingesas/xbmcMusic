package de.wingesas.android.xbmcMusic;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import de.wingesas.android.xbmcMusic.fragments.PlaylistActivity;
import de.wingesas.android.xbmcMusic.json.JsonTask;
import de.wingesas.android.xbmcMusic.json.JsonTask.TaskResultListener;
import de.wingesas.android.xbmcMusic.json.methods.JSONRPCPing;
import de.wingesas.android.xbmcMusic.json.methods.SystemShutdown;
import de.wingesas.android.xbmcMusic.preference.PreferenceActivity;

@TargetApi(11)
public class HomeActivity extends Activity implements NotifyManager {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

		setContentView(R.layout.home_grid);
		final GridView gridview = (GridView) findViewById(R.id.grid_home_activity);

		String[] values = getResources().getStringArray(R.array.navigation_list_actions);
		final String[] keys = getResources().getStringArray(R.array.navigation_list_actions_keys);

		TypedArray typedArray = getResources().obtainTypedArray(R.array.navigation_list_actions_images);
		int[] images = new int[typedArray.length()];
		for (int i = 0; i < typedArray.length(); i++)
			images[i] = typedArray.getResourceId(i, 0);
		typedArray.recycle();

		gridview.setAdapter(new GridAdapter(this, values, images));
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

				gridview.setEnabled(false);

				JsonTask<Boolean> pingTask = new JsonTask<Boolean>(HomeActivity.this);
				pingTask.setTaskResultListener(new TaskResultListener<Boolean>() {

					@Override
					public void onTaskComplete(Boolean result) {
						gridview.setEnabled(true);

						if (result != null && result == true) {
							if (keys[position].equals("PLAYLIST")) {
								Intent intent = new Intent(HomeActivity.this, PlaylistActivity.class);
								startActivity(intent);
							} else {
								Intent intent = new Intent();
								intent.setClass(HomeActivity.this, MainActivity.class);
								intent.putExtra("KEY", keys[position]);
								startActivity(intent);
							}
						}
					}
				});

				pingTask.execute(new JSONRPCPing[] { new JSONRPCPing() });
			}
		});
		setProgressBarIndeterminateVisibility(false);

		if (savedInstanceState == null) {

			final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			int counter = pref.getInt(PreferenceActivity.LAUNCH_COUNTER, 1);

			if (counter >= 10) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.please_rate).setTitle(R.string.like_the_app)
						.setCancelable(false)
						.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								try {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri
											.parse("market://details?id=de.wingesas.android.xbmcMusic")));
								} catch (android.content.ActivityNotFoundException anfe) {
									startActivity(new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("http://play.google.com/store/apps/details?id=de.wingesas.android.xbmcMusic")));
								}

							}
						}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				builder.create().show();

				pref.edit().putInt(PreferenceActivity.LAUNCH_COUNTER, -1).commit();
			} else if (counter > -1)
				pref.edit().putInt(PreferenceActivity.LAUNCH_COUNTER, ++counter).commit();
		}
		
		//new ScanTask().execute(null, null);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return true;
		case R.id.menu_shutdown:
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
			String host = sharedPref
					.getString(PreferenceActivity.HOST_ADDRESS, PreferenceActivity.HOST_ADDRESS_DEFAULT);

			if (host != null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setIcon(R.drawable.ic_action_warning);
				builder.setTitle(R.string.shutdown_host);
				builder.setMessage(String.format(getResources().getString(R.string.shutdown_host_ask), host));
				builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new JsonTask<Boolean>(HomeActivity.this).execute(new SystemShutdown[] { new SystemShutdown() });
					}
				});
				builder.setNegativeButton(android.R.string.cancel, null);
				builder.show();
			}

			return true;
		case R.id.menu_item_preferences:
			Intent intent = new Intent(this, PreferenceActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onAsyncTaskStarted() {
		setProgressBarIndeterminateVisibility(true);
	}

	@Override
	public void onAsyncTaskFinished() {
		setProgressBarIndeterminateVisibility(false);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void onError(Exception e) {
		setProgressBarIndeterminateVisibility(false);
		MainApplication.onErrorHandler(this, e);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Editor editor =
		// PreferenceManager.getDefaultSharedPreferences(this).edit();
		// editor.putInt(SELECTED_NAVIGATION_ITEM,
		// getActionBar().getSelectedNavigationIndex());
		// editor.commit();
	}

	public static class GridAdapter extends BaseAdapter {

		private Context context;
		private final String[] values;
		private final int[] images;

		public GridAdapter(Context context, String[] values, int[] images) {
			this.context = context;
			this.values = values;
			this.images = images;
		}

		@Override
		public int getCount() {
			return values.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (convertView == null) {

				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.home_grid_content, parent, false);

				ImageView imageView = (ImageView) view.findViewById(R.id.image_home_activity_grid);
				imageView.setImageResource(images[position]);

				TextView textView = (TextView) view.findViewById(R.id.text_home_activity_grid);
				textView.setText(values[position]);

			} else {
				view = convertView;
			}
			return view;
		}
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub

	}
}
