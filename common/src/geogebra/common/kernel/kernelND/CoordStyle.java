package geogebra.common.kernel.kernelND;

/**
 * interface to handle coord style
 * @author mathieu
 *
 */
public interface CoordStyle {

	/** set to 2D cartesian coords */
	public void setCartesian();
	/** set to polar coords */
	public void setPolar();
	/** set to comple coords */
	public void setComplex();
	/** set to 3D cartesian coords */
	public void setCartesian3D();
	/** @return toStringMode */
	public int getMode();
	/**
	 * set toStringMode
	 * @param mode mode
	 */
	public void setMode(int mode);

}
