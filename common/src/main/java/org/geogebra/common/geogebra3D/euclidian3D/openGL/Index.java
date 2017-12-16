package org.geogebra.common.geogebra3D.euclidian3D.openGL;

/**
 * Index for TreeSet that uses two integers
 *
 */
class Index implements Comparable<Index> {
	private int v1, v2;

	/**
	 * simple constructor
	 */
	public Index() {
		// nothing done
	}

	/**
	 * create a copy
	 * 
	 * @param index
	 *            index
	 */
	public Index(Index index) {
		this.v1 = index.v1;
		this.v2 = index.v2;
	}

	/**
	 * set index parameters
	 * 
	 * @param v1
	 *            first parameter
	 * @param v2
	 *            second parameter
	 */
	public void set(int v1, int v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	@Override
	public int compareTo(Index o) {
		if (v1 < o.v1) {
			return -1;
		}
		if (v1 > o.v1) {
			return 1;
		}
		if (v2 < o.v2) {
			return -1;
		}
		if (v2 > o.v2) {
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
}