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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 */
public class AlgoUnitOrthoVectorVector extends AlgoElement {

	private GeoVec3D v; // input
	private GeoVector n; // output

	private double length;

	/** Creates new AlgoUnitOrthoVectorVector */
	public AlgoUnitOrthoVectorVector(Construction cons, String label,
			GeoVec3D v) {
		super(cons);
		this.v = v;
		n = new GeoVector(cons);
		if (v instanceof GeoVector) {
			GeoPointND possStartPoint = ((GeoVector) v).getStartPoint();
			if (possStartPoint != null && possStartPoint.isLabelSet()) {
				try {
					n.setStartPoint(possStartPoint);
				} catch (CircularDefinitionException e) {
					// vector just created, no cycle possible
				}
			}
		}

		setInputOutput(); // for AlgoElement

		// compute line through P, Q
		n.z = 0.0d;
		compute();
		n.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.UnitOrthogonalVector;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = v;

		setOnlyOutput(n);
		setDependencies(); // done by AlgoElement
	}

	public GeoVector getVector() {
		return n;
	}

	GeoVec3D getv() {
		return v;
	}

	// line through P normal to v
	@Override
	public final void compute() {
		length = MyMath.length(v.x, v.y);
		n.x = -v.y / length;
		n.y = v.x / length;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("UnitVectorPerpendicularToA",
				"Unit vector perpendicular to %0", v.getLabel(tpl));

	}

}
