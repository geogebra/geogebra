package org.geogebra.common.kernel.discrete.tsp.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * çµŒè·¯æŽ¢ç´¢ã�®ã�Ÿã‚�ã�®ãƒ’ãƒ¼ãƒ—ã�§ã�™ã€‚
 * ã‚­ãƒ¼ã�«å¯¾ã�—ã�¦å€¤ã‚’æŒ�ã�Ÿã�›ã€�å€¤ã�®æ¯”è¼ƒã�«ã‚ˆã�£ã�¦ã€�ãƒ’ãƒ¼ãƒ—ï¼ˆå„ªå…ˆåº¦ä»˜ã‚­ãƒ¥ãƒ¼ï¼‰ã‚’æ§‹ç¯‰ã�—ã�¾ã�™ã€‚
 * Comparatorã‚’ã‚³ãƒ³ã‚¹ãƒˆãƒ©ã‚¯ã‚¿ã�«ä¸Žã�ˆã�ªã�‘ã‚Œã�°ã€�è¦�ç´ ã�¯æœ€å°�ã�®å€¤ã‚’æ ¹ã�«ã�—ã�¦ãƒ’ãƒ¼ãƒ—ã‚’æ§‹æˆ�ã�—ã�¾ã�™ã€‚
 * 
 * ã‚­ãƒ¼ã�«å¯¾ã�™ã‚‹å€¤ã‚’æ›´æ–°ã�™ã‚‹å ´å�ˆã�«ã�¯ã€�ä»¥å‰�ã�®å€¤ã‚ˆã‚Šã‚‚æ ¹ã�«è¿‘ã�„ï¼ˆå°�ã�•ã�„ï¼‰ã�¨è©•ä¾¡ã�•ã‚Œã‚‹å ´å�ˆã�®ã�¿
 * æ›´æ–°ã�•ã‚Œã�¾ã�™ã€‚
 *
 * @author ma38su
 * @param <E> 
 *
 */
public class Heap<E> {

	/**
	 * æ¨™æº–ã�®åˆ�æœŸå®¹é‡�
	 */
	private static final int DEFAULT_CAPACITY = 10;

	/**
	 * ã‚½ãƒ¼ãƒˆã�•ã‚Œã‚‹ã‚ªãƒ–ã‚¸ã‚§ã‚¯ãƒˆ
	 */
	private E[] entries;

	/**
	 * ãƒ’ãƒ¼ãƒ—ã�®ã‚µã‚¤ã‚º
	 */
	private int size;

	/**
	 * ã‚­ãƒ¼ã�®ç®¡ç�†ã�®ã�Ÿã‚�ã�®Map
	 */
	private final Map<E, Integer> table;
	
	/**
	 * é †åº�ä»˜ã�‘
	 */
	private final Comparator<E> comparator;

	/**
	 * ã‚³ãƒ³ã‚¹ãƒˆãƒ©ã‚¯ã‚¿
	 *
	 */
	public Heap() {
		this(null);
	}

	/**
	 * ã‚³ãƒ³ã‚¹ãƒˆãƒ©ã‚¯ã‚¿
	 * @param initialCapacity åˆ�æœŸå®¹é‡�
	 */
	public Heap(int initialCapacity) {
		this(initialCapacity, null);
	}
	/**
	 * ã‚³ãƒ³ã‚¹ãƒˆãƒ©ã‚¯ã‚¿
	 * @param comparator
	 */
	public Heap(Comparator<E> comparator) {
		this(Heap.DEFAULT_CAPACITY, comparator);
	}

	/**
	 * ã‚³ãƒ³ã‚¹ãƒˆãƒ©ã‚¯ã‚¿
	 * @param initialCapacity åˆ�æœŸå®¹é‡�
	 * @param comparator
	 */
	@SuppressWarnings("unchecked")
	public Heap(int initialCapacity, Comparator<E> comparator) {
		if (initialCapacity < 1) {
			throw new IllegalArgumentException();
		}
		this.size = 0;
		this.entries = (E[]) new Object[initialCapacity + 1];
		this.table = new HashMap<E, Integer>();
		this.comparator = comparator;
	}

	/**
	 * keyã�Œå­˜åœ¨ã�—ã�¦ã�„ã‚Œã�°valueæ›´æ–°ã€�keyã�Œå­˜åœ¨ã�—ã�¦ã�ªã�‘ã‚Œã�°å ´å�ˆã�¯æŒ¿å…¥ã�™ã‚‹
	 * @param key æŒ¿å…¥ã�™ã‚‹ key
	 * @param value æŒ¿å…¥ã�™ã‚‹ value
	 * @return æ›´æ–°ã�¾ã�Ÿã�¯æŒ¿å…¥ã�Œã�Šã�“ã�ªã�ˆã‚Œã�°true
	 */
	@SuppressWarnings("unchecked")
	public boolean add(E key) {
		E entry = key;
		Integer pointer = this.table.get(key);
		if (pointer != null) {
			int index = pointer.intValue();
			if (this.comparator == null) {
				if(((Comparable<E>)this.entries[index]).compareTo(entry) > 0) {
					this.entries[index] = entry;
					this.fixUp(index);
				} else {
					return false;
				}
			} else {
				if (this.comparator.compare(this.entries[index], entry) > 0) {
					this.entries[index] = entry;
					this.fixUp(index);
				} else {
					return false;
				}
			}
		} else {
			this.grow(++this.size);
			this.table.put(key, this.size);
			this.entries[this.size] = entry;
			this.fixUp(this.size);
		}
		return true;
	}

	/**
	 * å…¥ã‚Œæ›¿ã�ˆã‚‹
	 * @param index1
	 * @param index2
	 */
	private void swap(int index1, int index2) {
		final E tmp = this.entries[index1];
		this.entries[index1] = this.entries[index2];
		this.entries[index2] = tmp;
		this.table.put(this.entries[index1], index1);
		this.table.put(this.entries[index2], index2);
	}

	/**
	 * ãƒ’ãƒ¼ãƒ—ã�®å…ˆé ­ï¼ˆæ ¹ï¼‰ã�®è¦�ç´ ã‚’å‰Šé™¤ã�—ã�¦å�–ã‚Šå‡ºã�™
	 * @return ãƒ’ãƒ¼ãƒ—ã�®å…ˆé ­ã�®è¦�ç´ 
	 */
	public E poll() {
		if (this.size == 0) {
			return null;
		}

		final E entry = this.entries[1];
		this.table.remove(entry);
		if (this.size > 1) {
			this.entries[1] = this.entries[this.size];
			this.table.put(this.entries[1], 1);
		}
		this.entries[this.size] = null;
		if (--this.size > 1) {
			this.fixDown(1);
		}
		return entry;
	}

	/**
	 * å‰Šé™¤ã�›ã�šã�«ãƒ’ãƒ¼ãƒ—ã�®å…ˆé ­ï¼ˆæ ¹ï¼‰ã�®è¦�ç´ ã‚’å�–ã‚Šå‡ºã�™
	 * @return ãƒ’ãƒ¼ãƒ—ã�®å…ˆé ­ã�®è¦�ç´ 
	 */
	public E peek() {
		return this.entries[1];
	}

	/**
	 * @param key ç¢ºèª�ã�™ã‚‹ key
	 * @return keyã�Œå�«ã�¾ã‚Œã�¦ã�„ã‚Œã�° true
	 */
	public boolean containsKey(Object key) {
		return this.table.containsKey(key);
	}

	public void clear() {
		this.table.clear();
		for (int i = 0; i <= this.size; i++) {
			this.entries[i] = null;
		}
		this.size = 0;
	}

	/**
	 * å­�ã�¨ã�®çŠ¶æ…‹ã�®æ¯”è¼ƒ
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	private void fixDown(int index) {
		int son;
		if (this.comparator == null) {
			while ((son = index << 1) <= this.size) {
				if (son < this.size && ((Comparable<E>) this.entries[son]).compareTo(this.entries[son+1]) > 0) {
					son++;
				}
				if (((Comparable<E>) this.entries[index]).compareTo(this.entries[son]) <= 0) {
					break;
				}
				this.swap(index, son);
				index = son;
			}
		} else {
			while ((son = index << 1) <= this.size) {
				if (son < this.size && this.comparator.compare(this.entries[son], this.entries[son+1]) > 0) {
					son++;
				}
				if (this.comparator.compare(this.entries[index], this.entries[son]) <= 0) {
					break;
				}
				this.swap(index, son);
				index = son;
			}
		}
	}

	/**
	 * è¦ªã�¨ã�®çŠ¶æ…‹ã‚’ç¢ºèª�
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	private void fixUp(int index) {
		int parent;
		if (this.comparator == null) {
			while ((parent = index >> 1) > 0) {
				if (((Comparable<E>) this.entries[index]).compareTo(this.entries[parent]) >= 0) {
					break;
				}
				this.swap(index, parent);
				index = parent;
			}
		} else {
			while ((parent = index >> 1) > 0) {
				if (this.comparator.compare(this.entries[index], this.entries[parent]) >= 0) {
					break;
				}
				this.swap(index, parent);
				index = parent;
			}
		}
	}

	/**
	 * ãƒ’ãƒ¼ãƒ—ã�Œç©ºã�§ã�ªã�„ã�‹ç¢ºã�‹ã‚�ã‚‹ã€‚
	 * @return ãƒ’ãƒ¼ãƒ—ã�«è¦�ç´ ã�Œã�ªã�‘ã‚Œã�°true
	 */
	public boolean isEmpty() {
		return this.size == 0;
	}

	/**
	 * é…�åˆ—ã�®ã‚µã‚¤ã‚ºã‚’æ‹¡å¼µã�™ã‚‹
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	private void grow(int index) {
		int newLength = this.entries.length;
		if (index < newLength) {
			return;
		}
		if (index == Integer.MAX_VALUE) {
			throw new Error("Out of memory");
		}
		while (newLength <= index) {
			if (newLength >= Integer.MAX_VALUE / 2) {
				newLength = Integer.MAX_VALUE;
			} else {
				newLength <<= 2;
			}
		}
		final E[] newEntrys = (E[]) new Object[newLength];
		System.arraycopy(this.entries, 0, newEntrys, 0, this.entries.length);

		this.entries = newEntrys;
	}

	@Override
	public String toString() {
		if (this.size == 0) {
			return "";
		}
		final StringBuilder sb = new StringBuilder(this.entries[1].toString());
		for(int i = 2; i <= this.size; i++) {
			sb.append("," + this.entries[i].toString());
		}
		return sb.toString();
	}

	/**
	 * ãƒ’ãƒ¼ãƒ—ã�®ã‚µã‚¤ã‚ºã‚’è¿”ã�—ã�¾ã�™ã€‚
	 * @return ãƒ’ãƒ¼ãƒ—ã�®ã‚µã‚¤ã‚º
	 */
	public int size() {
		return this.size;
	}
}
