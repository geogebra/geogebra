/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.euclidian3D.openGL;

import java.util.ArrayList;
import java.util.Arrays;

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
		int k = values.length;
		if (length + k <= size) {
			for (int i = 0; i < k; i++) {
				set(length + i, values[i]);
			}
		} else {
			this.addAll(Arrays.asList(values));
			size += k;
		}
		length += k;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj); // see EQ_DOESNT_OVERRIDE_EQUALS in SpotBugs
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
