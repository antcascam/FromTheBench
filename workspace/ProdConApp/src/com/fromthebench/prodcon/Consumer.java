package com.fromthebench.prodcon;

import java.util.concurrent.BlockingQueue;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Consumer extends Thread {
	private BlockingQueue<Integer> generatedNumbers;
	private static final int MAX_NUM = 1800;
	private static final int MIN_NUM = 650;
	private Handler handler;
	private boolean run;

	public Consumer(String name) {
		super(name);
	}

	public Consumer(String name, BlockingQueue<Integer> generatedNumbers) {
		super(name);
		this.generatedNumbers = generatedNumbers;
		run = true;
	}

	public void run() {
		Bundle bundle = null;
		Message msg = null;
		int consumedNumber = 0;
		while (run) {
			try {
				sleep((long) Math.floor(Math.random() * (MIN_NUM - MAX_NUM + 1)
						+ MAX_NUM));
				consumedNumber = generatedNumbers.take();
				bundle = new Bundle();
				msg = new Message();
				bundle.putInt("cunsumedNumber", consumedNumber);
				msg.setData(bundle);
				handler.sendMessage(msg);
			} catch (InterruptedException e) {
				Log.e("log_tag",
						"Exception in generation thread: " + e.toString());
			}
		}
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void finish() {
		run = false;
	}

}
