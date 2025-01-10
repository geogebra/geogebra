/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoElement3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 *
 * @author ggb3D
 * 
 *         Calculate the GeoPoint3D intersection of two coord sys (eg line and
 *         plane).
 * 
 */
public abstract class AlgoIntersectCoordSys extends AlgoElement3D {

	// inputs
	/** first coord sys */
	private GeoElementND cs1;
	/** second coord sys */
	private GeoElementND cs2;

	// output
	/** intersection */
	private GeoElement3D intersection;

	/**
	 * Creates new AlgoIntersectLinePlane
	 * 
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of point
	 * @param cs1
	 *            first coord sys
	 * @param cs2
	 *            second coord sys
	 * @param swapInputs
	 *            may swap inputs order
	 */
	AlgoIntersectCoordSys(Construction cons, String label, GeoElementND cs1,
			GeoElementND cs2, boolean swapInputs) {

		this(cons, cs1, cs2, swapInputs);
		intersection.setLabel(label);

	}

	AlgoIntersectCoordSys(Construction cons, GeoElementND cs1, GeoElementND cs2,
			boolean swapInputs) {

		super(cons);

		this.cs1 = cs1;
		this.cs2 = cs2;

		intersection = createIntersection(cons);

		setInputOutput(
				swapInputs
						? new GeoElement[] { (GeoElement) cs2,
								(GeoElement) cs1 }
						: new GeoElement[] { (GeoElement) cs1,
								(GeoElement) cs2 },
				new GeoElement[] { intersection });

	}

	/**
	 * return new intersection (default is 3D point)
	 * 
	 * @param cons1
	 *            construction
	 * @return new intersection
	 */
	protected GeoElement3D createIntersection(Construction cons1) {
		return new GeoPoint3D(cons1);
	}

	/**
	 * return the first coord sys
	 * 
	 * @return the first coord sys
	 */
	GeoElementND getCS1() {
		return cs1;
	}

	/**
	 * return the second coord sys
	 * 
	 * @return the second coord sys
	 */
	GeoElementND getCS2() {
		return cs2;
	}

	/**
	 * return the intersection
	 * 
	 * @return the intersection
	 */
	public GeoElement3D getIntersection() {
		return intersection;
	}

	// /////////////////////////////////////////////
	// COMPUTE

	/**
	 * sets the output to "undefined" if inputs are not defined
	 * 
	 * @return if the output is defined
	 */
	protected boolean outputIsDefined() {

		if (!cs1.isDefined() || !cs2.isDefined()) {
			intersection.setUndefined();
			return false;
		}

		return true;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain(getIntersectionTypeString(),
				getCS1().getLabel(tpl), getCS2().getLabel(tpl));
	}

	abstract protected String getIntersectionTypeString();

	/*
	 * This should apply to every subclass. In case it does not, a case per case
	 * should be used. It produces a GeoNumeric, so beware GeoNumeric will be
	 * treated differently than points.
	 */

}
