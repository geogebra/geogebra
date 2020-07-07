package org.geogebra.common.kernel.arithmetic;

/**
 * Symbolic vector, coordinastes are expression values
 *
 */
public interface MyVecNDNode extends ExpressionValue, ReplaceChildrenByValues {

	/**
	 * @return whether this vector is for CAS
	 */
	public boolean isCASVector();

	/**
	 * Set this vector to CAS
	 */
	public void setupCASVector();

	/**
	 * @return 2 or 3
	 */
	public int getDimension();

	/**
	 * 
	 * @return x component
	 */
	public ExpressionValue getX();

	/**
	 * 
	 * @return y component
	 */
	public ExpressionValue getY();

	/**
	 * 
	 * @return z component (or null if getDimension() = 2)
	 */
	public ExpressionValue getZ();

	/**
	 * Sets the printing mode to vector printing
	 */
	void setVectorPrintingMode();
}
