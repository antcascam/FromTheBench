package com.fromthebench.prodcon;

public class ProConItem {
	protected int number;
	protected long id;

	public ProConItem() {
		this.number = 0;
	}

	public ProConItem(long id, int number) {
		this.id = id;
		this.number = number;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
