package org.geogebra.common.kernel.discrete.tsp.util;


public class IntegerSet {
	private int[] bitset;
	private int[] list;
	private int size;

	public int size() {
		return this.size;
	}
	
	private int[] index;
	/**
	 * @param initialCapacity
	 */
	public IntegerSet(int initialCapacity) {
		this.bitset = new int[initialCapacity];
		this.list = new int[initialCapacity];
		this.size = 0;
		this.index = new int[initialCapacity];
		for (int i = 0; i < this.index.length; i++) {
			this.index[i] = i;
		}
	}

	public boolean contains(int value) {
		return this.bitset[value] > 0;
	}

	public boolean add(int value) {
		if (this.bitset[value] == 0) {
			this.list[this.size++] = value;
			this.bitset[value] = this.size;
			return true;
		}
		return false;
	}
	
	public int get(int index) {
		if (index >= this.size) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return this.list[index];
	}
	
	public boolean remove(int value) {
		if (this.bitset[value] > 0) {
			int index = this.bitset[value] - 1;
			swap(index, --this.size);
			this.bitset[this.list[index]] = this.bitset[value];
			this.bitset[value] = 0;
			return true;
		}
		return false;
	}

	/**
	 * è¦�ç´ ã‚’äº¤æ�›ã�™ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param n1 äº¤æ�›ã�™ã‚‹è¦�ç´ ã�®ã‚¤ãƒ³ãƒ‡ãƒƒã‚¯ã‚¹
	 * @param n2 äº¤æ�›ã�™ã‚‹è¦�ç´ ã�®ã‚¤ãƒ³ãƒ‡ãƒƒã‚¯ã‚¹
	 */
	private void swap(int n1, int n2) {
		int tmp = this.list[n1];
		this.list[n1] = this.list[n2];
		this.list[n2] = tmp;
		
	}
}
