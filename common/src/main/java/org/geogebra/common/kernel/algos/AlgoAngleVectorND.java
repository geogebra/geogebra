/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.draw.DrawAngle;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public abstract class AlgoAngleVectorND extends AlgoAngle {

	protected GeoElement vec; // input
	protected GeoAngle angle; // output

	protected double[] coords = new double[2];

	public AlgoAngleVectorND(Construction cons, String label, GeoElement vec) {
		super(cons);
		this.vec = vec;

		angle = newGeoAngle(cons);
		setInputOutput(); // for AlgoElement
		compute();
		angle.setLabel(label);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = vec;

		setOutputLength(1);
		setOutput(0, angle);
		setDependencies(); // done by AlgoElement
	}

	public GeoAngle getAngle() {
		return angle;
	}

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("AngleOfA", vec.getLabel(tpl));

	}

	public boolean updateDrawInfo(double[] m, double[] firstVec,
			DrawAngle drawable) {
		if (vec.isGeoVector()) {
			GeoPointND vertex = ((GeoVector) vec).getStartPoint();
			if (vertex != null)
				vertex.getInhomCoords(m);
			return vertex != null && vertex.isDefined() && !vertex.isInfinite();
		}
		m[0] = 0;
		m[1] = 0;
		return vec.isDefined();
	}

	// TODO Consider locusequability
}
