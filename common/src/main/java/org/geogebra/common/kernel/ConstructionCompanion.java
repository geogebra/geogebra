package org.geogebra.common.kernel;

import org.geogebra.common.kernel.Construction.Constants;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;

/**
 * Extends Construction (for 3D stuff)
 * 
 * @author Mathieu
 *
 */
public class ConstructionCompanion {

	protected Construction cons;

	/**
	 * default constructor
	 * 
	 * @param cons
	 *            construction
	 * 
	 */
	public ConstructionCompanion(Construction cons) {
		this.cons = cons;
	}

	public void init() {
		// nothing needed here
	}

	public GeoAxisND getZAxis() {
		return null;
	}

	public GeoDirectionND getXOYPlane() {
		return null;
	}

	public GeoDirectionND getSpace() {
		return null;
	}

	public GeoElement getClippingCube() {
		return null;
	}

	/**
	 * @return the ConstructionDefaults consDefaults
	 */
	public ConstructionDefaults newConstructionDefaults() {
		return new ConstructionDefaults(cons);
	}

	protected void initGeoTables() {

	}

	public void updateLocalAxesNames() {

	}

	public Constants isConstantElement(GeoElement geo) {

		return Constants.NOT;
	}

	public boolean is3D() {
		return false;
	}
}
