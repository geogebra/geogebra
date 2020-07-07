package org.geogebra.common.kernel.matrix;

/**
 * Interface for animating values
 *
 * @param <T>
 *            type implementing the interface
 */
public interface AnimatableValue<T> {
	/**
	 * 
	 * @param other
	 *            other value
	 * @return true if value equals the other one and doesn't need animation
	 */
	public boolean equalsForAnimation(T other);

	/**
	 * 
	 * @return true if defined
	 */
	public boolean isDefined();

	/**
	 * set to other
	 * 
	 * @param other
	 *            other
	 */
	public void setAnimatableValue(T other);

	/**
	 * set to undefined
	 */
	public void setUndefined();

}
