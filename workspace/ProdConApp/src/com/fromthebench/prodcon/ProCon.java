package com.fromthebench.prodcon;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ProCon extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.procon);

		ListView listPro = (ListView) findViewById(R.id.producerList);
		ListView listCon = (ListView) findViewById(R.id.consumerList);

		ArrayList<ProConItem> itemsPro = obtenerItemsPro();
		ArrayList<ProConItem> itemsCon = obtenerItemsCon();

		ItemProAdapter adapterPro = new ItemProAdapter(this, itemsPro);
		ItemConsAdapter adapterCon = new ItemConsAdapter(this, itemsCon);

		listPro.setAdapter(adapterPro);
		listCon.setAdapter(adapterCon);

//		initProCon();
	}

	private void initProCon() {
		int listSize = 10;
		BackgroundThread back = new BackgroundThread();

		back.setDaemon(false);
		back.start();

		while (listSize > 0) {
			back.executeTask();
			listSize--;
		}
	}

	private ArrayList<ProConItem> obtenerItemsPro() {
		ArrayList<ProConItem> items = new ArrayList<ProConItem>();

		// TODO: añadir items desde servidor
		items.add(new ProConItem(0, 1));
		items.add(new ProConItem(1, 3));
		items.add(new ProConItem(2, 5));
		items.add(new ProConItem(3, 6));

		return items;
	}

	private ArrayList<ProConItem> obtenerItemsCon() {
		ArrayList<ProConItem> items = new ArrayList<ProConItem>();

		// TODO: añadir items desde servidor
		items.add(new ProConItem(0, 7));
		items.add(new ProConItem(1, 1));
		items.add(new ProConItem(2, 4));
		items.add(new ProConItem(3, 8));

		return items;
	}

}
