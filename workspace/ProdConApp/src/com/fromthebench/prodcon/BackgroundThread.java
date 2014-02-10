package com.fromthebench.prodcon;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

public class BackgroundThread extends Thread {
	private static LinkedBlockingQueue<Consumer> taskQueue = null;

	public BackgroundThread() {
		taskQueue = new LinkedBlockingQueue<Consumer>();
	}

	public void executeTask() {
		Consumer task = new Consumer();
		taskQueue.add(task);
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public void run() {
		while (true) {
			try {
				Consumer consumer = taskQueue.take();
				consumer.consume();
				sleep(randInt(650, 1800));
			} catch (InterruptedException e) {
				Log.e("log_tag",
						"Error ejecutando BackgroundThread." + e.toString());
			}
		}
	}
}

class Consumer {
	void consume() {

	}
}
