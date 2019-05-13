package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;

/**
 * ArrayList that can be reused
 *
 * @param <T>
 *            values stored type
 */
public class ReusableArrayList<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;
	private int length;
	private int size;

	/**
	 * constructor
	 */
	public ReusableArrayList() {
		length = 0;
		size = 0;
	}

	/**
	 * constructor
	 * 
	 * @param size
	 *            initial size
	 */
	public ReusableArrayList(int size) {
		super(size);
		length = 0;
		this.size = 0;
	}

	/**
	 * set length
	 * 
	 * @param length
	 *            length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * 
	 * @return current length
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * add a value to the current position
	 * 
	 * @param value
	 *            value
	 */
	public void addValue(T value) {
		if (length == size) {
			add(value);
			size++;
		} else {
			set(length, value);
		}
		length++;
	}

	/**
	 * add values to the current position
	 * 
	 * @param values
	 *            values
	 */
	public void addValues(T... values) {
		if (length == size) {
			for (T v : values) {
				add(v);
			}
			size += values.length;
		} else {
			for (int i = 0; i < values.length; i++) {
				set(length + i, values[i]);
			}
		}
		length += values.length;
	}

}
