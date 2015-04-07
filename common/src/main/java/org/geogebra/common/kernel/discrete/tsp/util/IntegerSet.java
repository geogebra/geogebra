package org.geogebra.common.kernel.discrete.tsp.util;

/**
 * Integeråž‹ã�®ã‚»ãƒƒãƒˆ
 * @author ma38su
 */
public class IntegerSet {
	private int[] bitset;
	private int[] list;
	private int size;
	/**
	 * è¦�ç´ ã�®æ•°ã‚’è¿”ã�™ãƒ¡ã‚½ãƒƒãƒ‰
	 * @return è¦�ç´ ã�®æ•°
	 */
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

	/**
	 * è¦�ç´ ã‚’å�«ã‚€ã�‹ã�©ã�†ã�‹ç¢ºèª�ã�™ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param value è¦�ç´ 
	 * @return è¦�ç´ ã‚’å�«ã‚“ã�§ã�„ã‚Œã�°trueã‚’è¿”ã�™ã€�falseã‚’è¿”ã�™ã€‚
	 */
	public boolean contains(int value) {
		return this.bitset[value] > 0;
	}

	/**
	 * è¦�ç´ ã‚’åŠ ã�ˆã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param value åŠ ã�ˆã‚‹è¦�ç´ 
	 * @return è¦�ç´ ã�Œå�«ã�¾ã‚Œã�¦ã�„ã�ªã�‘ã‚Œã�°trueã€�å�«ã�¾ã‚Œã�¦ã�„ã�ªã�‘ã‚Œã�°falseã‚’è¿”ã�™ã€‚
	 */
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
	
	/**
	 * è¦�ç´ ã‚’å‰Šé™¤ã�™ã‚‹ãƒ¡ã‚½ãƒƒãƒ‰
	 * @param value å‰Šé™¤ã�™ã‚‹è¦�ç´ 
	 * @return è¦�ç´ ã�Œå�«ã�¾ã‚Œã�¦ã�„ã‚Œã�°trueã€�å�«ã�¾ã‚Œã�¦ã�„ã�ªã�‘ã‚Œã�°falseã‚’è¿”ã�™ã€‚
	 */
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
