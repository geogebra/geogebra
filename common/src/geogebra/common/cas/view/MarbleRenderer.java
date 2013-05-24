package geogebra.common.cas.view;

/**
 * Interface for GUI component rendering the marble
 * @author Zbynek
 *
 */
public interface MarbleRenderer {

	/**
	 * @param value true when the GeoElement should be ploted
	 */
	void setMarbleValue(boolean value);

	/**
	 * @param visible whether the marble itself should be visible
	 */
	void setMarbleVisible(boolean visible);

}
