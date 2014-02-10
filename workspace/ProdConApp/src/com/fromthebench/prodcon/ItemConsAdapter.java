package com.fromthebench.prodcon;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemConsAdapter extends BaseAdapter {
	protected Activity activity;
	protected ArrayList<ProConItem> items;

	public ItemConsAdapter(Activity activity, ArrayList<ProConItem> items) {
		this.activity = activity;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		View vi = contentView;

		if (contentView == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.cons_item, null);
		}

		ProConItem item = items.get(position);

		TextView number = (TextView) vi.findViewById(R.id.consItemText);
		number.setText(String.valueOf(item.getNumber()));

		return vi;
	}

}
