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
