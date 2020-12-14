package org.geogebra.common.kernel;

import org.geogebra.common.kernel.Construction.Constants;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;

import com.google.j2objc.annotations.Weak;

/**
 * Extends Construction (for 3D stuff)
 * 
 * @author Mathieu
 *
 */
public class ConstructionCompanion {
	/** construction */
	@Weak
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

	/**
	 * Create 3D constant objects
	 */
	public void init() {
		// nothing needed here
	}

	/**
	 * @return z-axis (in 3D)
	 */
	public GeoAxisND getZAxis() {
		return null;
	}

	/**
	 * @return xOy plane (in 3D)
	 */
	public GeoDirectionND getXOYPlane() {
		return null;
	}

	/**
	 * @return space placeholder
	 */
	public GeoDirectionND getSpace() {
		return null;
	}

	/**
	 * @return clipping cube
	 */
	public GeoElement getClippingCube() {
		return null;
	}

	/**
	 * @return the ConstructionDefaults consDefaults
	 */
	public ConstructionDefaults newConstructionDefaults() {
		return new ConstructionDefaults(cons);
	}

	/**
	 * init 3D geos
	 */
	protected void initGeoTables() {
		// no 3D geos in 2D
	}

	/**
	 * update z-axis name
	 */
	public void updateLocalAxesNames() {
		// no z-axis in 2D
	}

	/**
	 * @param geo
	 *            geo element, xAxis or yAxis never as input
	 * @return whether it's space, xYo plane
	 */
	public Constants isConstantElement(GeoElement geo) {

		return Constants.NOT;
	}

	/**
	 * @return whether it's 3D construction
	 */
	public boolean is3D() {
		return false;
	}
}
