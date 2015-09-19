package org.geogebra.common.kernel.arithmetic;

/**
 * Symbolic vector, coordinastes are expression values
 *
 */
public interface MyVecNDNode {

	/**
	 * @return whether this vector is for CAS
	 */
	public boolean isCASVector();

	/**
	 * @return 2 or 3
	 */
	public int getDimension();

}
