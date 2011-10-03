package geogebra.kernel;


/**
 * GeoElement whose line properties (thickness, type)
 * can be set.
 *
 */
public interface LineProperties {
	/**
	 * Set line thicknes
	 * @param thickness
	 */
	public void setLineThickness(int thickness);
	/**
	 * Get line thickness
	 * @return line thickness
	 */
	public int getLineThickness();
	/**
	 * Set line type (see {@link geogebra.euclidian.EuclidianView#getLineTypes})
	 * @param type
	 */
	public void setLineType(int type);
	/**
	 * Get line type (see {@link geogebra.euclidian.EuclidianView#getLineTypes})
	 * @return line type
	 */
	public int getLineType();
}