/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
		ConstructionDefaults constructionDefaults = new ConstructionDefaults(cons);
		cons.getApplication()
				.getSettings()
				.getLabelSettings()
				.addListener(constructionDefaults);
		return constructionDefaults;
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
	 * @param geo GeoElement
	 * @return The constant element associated with {@code geo},
	 * {@link Constants#NOT} if it is no constant element.
	 */
	public Constants getConstantElement(GeoElement geo) {
		return Constants.NOT;
	}

	/**
	 * @return whether it's 3D construction
	 */
	public boolean is3D() {
		return false;
	}
}
