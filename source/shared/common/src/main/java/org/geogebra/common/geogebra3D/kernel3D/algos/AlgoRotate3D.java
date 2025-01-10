/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoRotatePoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.RotatableND;

/**
 *
 * @author mathieu
 */
public abstract class AlgoRotate3D extends AlgoTransformation {

	protected RotatableND out;
	protected GeoNumberValue angle;

	/**
	 * Creates new unlabeled rotation algo
	 * 
	 * @param cons
	 *            construction
	 * @param in
	 *            rotated geo
	 * @param angle
	 *            angle
	 */
	public AlgoRotate3D(Construction cons, GeoElement in,
			GeoNumberValue angle) {

		super(cons);
		this.inGeo = in;
		this.angle = angle;

		// create output object
		outGeo = getResultTemplate(inGeo);
		if (!(outGeo instanceof GeoList)) {
			out = (RotatableND) outGeo;
		}
	}

	/**
	 * Set output.
	 */
	protected void setOutput() {
		setOnlyOutput(outGeo);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the rotated point
	 * 
	 * @return rotated point
	 */
	@Override
	public GeoElement getResult() {
		return outGeo;
	}

	@Override
	protected void setTransformedObject(GeoElement g, GeoElement g2) {
		inGeo = g;
		outGeo = g2;
		if (!(outGeo instanceof GeoList)) {
			out = (RotatableND) outGeo;
		}

	}

	@Override
	protected GeoElement getResultTemplate(GeoElement geo) {
		if (geo instanceof GeoFunction || geo instanceof GeoCurveCartesian) {
			return new GeoCurveCartesian3D(cons);
		}

		return super.getResultTemplate(geo);
	}

	@Override
	protected GeoElement copy(GeoElement geo) {
		return kernel.copy3D(geo);
	}

	@Override
	protected GeoElement copyInternal(Construction consCopy, GeoElement geo) {
		return kernel.copyInternal3D(consCopy, geo);
	}

}
