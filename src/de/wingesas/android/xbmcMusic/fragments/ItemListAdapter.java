package de.wingesas.android.xbmcMusic.fragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.wingesas.android.xbmcMusic.R;
import de.wingesas.android.xbmcMusic.data.MusicListItem;

public abstract class ItemListAdapter<T extends MusicListItem> extends ArrayAdapter<T> {

	protected final LayoutInflater inflater;
	protected String label;
	protected String subLabel;
	protected String subExtra;
	protected int itemTypeImageResource;

	protected abstract void prepareView(int position);

	public ItemListAdapter(Activity activity) {
		super(activity, android.R.layout.simple_list_item_1);
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_row, parent, false);

			holder = new ViewHolder();
			holder.imageItemType = (ImageView) convertView.findViewById(R.id.image_item_type);
			holder.textLabel = (TextView) convertView.findViewById(R.id.text_label);
			holder.textSubLabel = (TextView) convertView.findViewById(R.id.text_sub_label);
			holder.textSubExtra = (TextView) convertView.findViewById(R.id.text_sub_extra);
			holder.subContainer = convertView.findViewById(R.id.sub_container);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		prepareView(position);

		holder.textLabel.setText(label);

		if (subLabel == null && subExtra == null)
			holder.subContainer.setVisibility(View.GONE);
		else {
			holder.subContainer.setVisibility(View.VISIBLE);
			holder.textSubLabel.setText(subLabel);
			holder.textSubExtra.setText(subExtra == null ? "" : subExtra);
		}

		holder.imageItemType.setImageResource(itemTypeImageResource);

		return convertView;
	}

	static class ViewHolder {
		ImageView imageItemType;
		TextView textLabel;
		TextView textSubLabel;
		TextView textSubExtra;
		View subContainer;
	}
}
