package geogebra.kernel;

public interface Animatable {
	
	/**
	 * Performs the next animation step for this GeoElement. This may
	 * change the value of this GeoElement but will NOT call update() or updateCascade().
	 * 
	 * @param frameRate current frames/second used in animation
	 * @return whether the value of this GeoElement was changed
	 */
	public boolean doAnimationStep(double frameRate);

	public boolean isAnimating();

}
