package geogebra.kernel;

/**
 * Represents geos that can be mirrored atline or point
 * 
 */
public interface ConicMirrorable {
	/**
	 * Miror at circle
	 * @param c mirror circle
	 */
	public void mirror(GeoConic c);
	
}