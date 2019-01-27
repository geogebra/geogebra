package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.kernel.Construction;

/**
 * Extension of GeoPoint3D for 3D view cursor
 */
public class GeoCursor3D extends GeoPoint3D {

	private boolean isCaptured;

	/**
	 * constructor
	 * 
	 * @param c
	 *            construction
	 */
	public GeoCursor3D(Construction c) {
		super(c);
	}

	/**
	 * set that the cursor is captured (e.g. snapped to grid)
	 * 
	 * @param flag
	 *            flag
	 */
	public void setIsCaptured(boolean flag) {
		isCaptured = flag;
	}

	/**
	 * 
	 * @return true if the cursor is captured (e.g. snapped to grid)
	 */
	public boolean getIsCaptured() {
		return isCaptured;
	}

}
