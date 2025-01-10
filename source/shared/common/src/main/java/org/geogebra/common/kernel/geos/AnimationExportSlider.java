package org.geogebra.common.kernel.geos;

/**
 * Interface for sliders usable for e.g. animated gif export
 *
 */
public interface AnimationExportSlider {

	/**
	 * 
	 * @return string displayed in slider combo box
	 */
	@Override
	public String toString();

	/**
	 * will be called before each frame rendering
	 */
	public void updateRepaint();

	/**
	 * @return animation type (ANIMATION_*)
	 */
	public int getAnimationType();

	/**
	 * 
	 * @return slider min value
	 */
	public double getIntervalMin();

	/**
	 * 
	 * @return slider max value
	 */
	public double getIntervalMax();

	/**
	 * 
	 * @return slider step value
	 */
	public double getAnimationStep();

	/**
	 * 
	 * @param x
	 *            slider value
	 */
	public void setValue(double x);

}
