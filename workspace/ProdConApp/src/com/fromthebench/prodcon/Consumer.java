package com.fromthebench.prodcon;

import java.util.Random;
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
		Integer consumedNumber;
		while (run) {
			try {
				Random r = new Random();
				long time;
				bundle = new Bundle();
				msg = new Message();
				consumedNumber = generatedNumbers.poll();
				if (consumedNumber != null) {
					bundle.putInt("cunsumedNumber", consumedNumber);
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
				time = r.nextInt(MAX_NUM - MIN_NUM) + MIN_NUM;
				sleep(time);
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
