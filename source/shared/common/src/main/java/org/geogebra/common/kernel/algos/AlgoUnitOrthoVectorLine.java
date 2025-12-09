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
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.util.MyMath;

/**
 *
 * @author Markus
 */
public class AlgoUnitOrthoVectorLine extends AlgoElement {

	private GeoLine g; // input
	private GeoVector n; // output

	private double length;

	/** Creates new AlgoOrthoVectorLine */
	public AlgoUnitOrthoVectorLine(Construction cons, String label, GeoLine g) {
		super(cons);
		this.g = g;
		n = new GeoVector(cons);
		setInputOutput(); // for AlgoElement

		GeoPoint possStartPoint = g.getStartPoint();
		if (possStartPoint != null && possStartPoint.isLabelSet()) {
			try {
				n.setStartPoint(possStartPoint);
			} catch (CircularDefinitionException e) {
				// can't happen for new vector n
			}
		}

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
		input[0] = g;

		setOnlyOutput(n);
		setDependencies(); // done by AlgoElement
	}

	public GeoVector getVector() {
		return n;
	}

	GeoLine getg() {
		return g;
	}

	// line through P normal to v
	@Override
	public final void compute() {
		length = MyMath.length(g.x, g.y);
		n.x = g.x / length;
		n.y = g.y / length;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("UnitVectorPerpendicularToA",
				"Unit vector perpendicular to %0", g.getLabel(tpl));
	}

}
