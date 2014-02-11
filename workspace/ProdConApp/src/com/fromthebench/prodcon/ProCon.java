package com.fromthebench.prodcon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

public class ProCon extends Activity {
	BlockingQueue<Integer> generatedNumbers;
	private Consumer consumer;
	private Producer producer;
	private Handler handlerProducer;
	private Handler handlerConsumer;
	private ArrayList<ProConItem> itemsPro;
	private ArrayList<ProConItem> itemsCon;
	ListView listPro;
	ListView listCon;

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.procon);
		listPro = (ListView) findViewById(R.id.producerList);
		listCon = (ListView) findViewById(R.id.consumerList);

		listPro.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listCon.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

		generatedNumbers = new LinkedBlockingQueue<Integer>(10);
		itemsPro = new ArrayList<ProConItem>();
		itemsCon = new ArrayList<ProConItem>();

		handlerProducer = new Handler() {
			@SuppressLint("HandlerLeak")
			@Override
			public void handleMessage(Message msg) {
				Bundle bundle;
				bundle = msg.getData();
				if (bundle.containsKey("internetConnection")
						&& !bundle.getBoolean("internetConnection")) {
					checkConnection();
				} else {
					if (bundle.containsKey("newNumber"))
						updateProducerList();
				}
			}
		};

		handlerConsumer = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle bundle;
				bundle = msg.getData();
				updateConsumerList(bundle.getInt("cunsumedNumber"));
			}
		};

		producer = new Producer("Producer", generatedNumbers, this);
		consumer = new Consumer("Consumer", generatedNumbers);
		producer.setHandler(handlerProducer);
		consumer.setHandler(handlerConsumer);

		startProCon();
	}

	public void startProCon() {
		producer.start();
		consumer.start();
	}

	private void updateProducerList() {
		Iterator<Integer> it = generatedNumbers.iterator();
		itemsPro.clear();
		while (it.hasNext()) {
			itemsPro.add(new ProConItem(itemsPro.size(), it.next()));
		}
		ItemProAdapter adapterPro = new ItemProAdapter(this, itemsPro);
		listPro.setAdapter(adapterPro);
	}

	private void updateConsumerList(int numberToPut) {
		itemsCon.add(new ProConItem(itemsCon.size(), numberToPut));
		ItemConsAdapter adapterCon = new ItemConsAdapter(this, itemsCon);
		listCon.setAdapter(adapterCon);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		producer.finish();
		consumer.finish();
	}

	private void checkConnection() {
		Notifications.showMessage(this,
				"Por favor, compruebe su conexión a internet.");
	}

}
