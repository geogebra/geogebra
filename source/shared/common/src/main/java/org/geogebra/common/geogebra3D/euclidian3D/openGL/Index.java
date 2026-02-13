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

/**
 * Index for TreeSet that uses two integers
 *
 */
class Index implements Comparable<Index> {
	int v1;
	int v2;

	/**
	 * simple constructor
	 */
	Index() {
		// nothing done
	}

	/**
	 * create a copy
	 * 
	 * @param index
	 *            index
	 */
	Index(Index index) {
		this(index.v1, index.v2);
	}

	/**
	 * constructor
	 * 
	 * @param v1
	 *            first value
	 * @param v2
	 *            second value
	 */
	Index(int v1, int v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	/**
	 * set index parameters
	 * 
	 * @param v1
	 *            first parameter
	 * @param v2
	 *            second parameter
	 */
	void set(int v1, int v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	/**
	 * set index parameters
	 * 
	 * @param bufferSegment
	 *            buffer segment
	 */
	void setAvailableLengths(BufferSegment bufferSegment) {
		set(bufferSegment.getElementsAvailableLength(),
				bufferSegment.getIndicesAvailableLength());
	}

	/**
	 * set index parameters
	 * 
	 * @param bufferSegment
	 *            buffer segment
	 */
	void setLengths(BufferSegment bufferSegment) {
		set(bufferSegment.getElementsLength(),
				bufferSegment.getIndicesLength());
	}

	@Override
	public int compareTo(Index o) {
		// we compare second value first because for BufferSegment,
		// indices size is more likely to be much greater than elements size
		// (especially for graphs)
		if (v2 < o.v2) {
			return -1;
		}
		if (v2 > o.v2) {
			return 1;
		}
		if (v1 < o.v1) {
			return -1;
		}
		if (v1 > o.v1) {
			return 1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Index)) {
			return false;
		}
		Index index = (Index) o;
		return v1 == index.v1 && v2 == index.v2;
	}

	@Override
	public int hashCode() {
		return 0; // we don't use it in hash table etc.
	}

	@Override
	public String toString() {
		return v1 + ", " + v2;
	}

	/**
	 * @param index
	 *            index
	 * @return true if second value is greater or equal index second value
	 */
	boolean hasFirstValueGreaterThan(Index index) {
		return v1 > index.v1;
	}
}