package org.geogebra.common.awt;

public interface GArea extends GShape {

	/**
	 * Subtract other shape from this
	 * @param shape other shape
	 */
	void subtract(GArea shape);

	/**
	 * Intersect this with other shape
	 * @param shape other shape
	 */
	void intersect(GArea shape);

	/**
	 * XOR other shape with this
	 * @param shape other shape
	 */
	void exclusiveOr(GArea shape);

	/**
	 * Add other shape to this
	 * @param shape other shape
	 */
	void add(GArea shape);

	/**
	 * @return whether the area is empty
	 */
	boolean isEmpty();

	@Override
	public GPathIterator getPathIterator(GAffineTransform t);

}
