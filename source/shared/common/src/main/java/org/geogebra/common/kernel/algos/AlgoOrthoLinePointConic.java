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

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;

/**
 *
 * @author Markus
 */
public class AlgoOrthoLinePointConic extends AlgoElement {

	private GeoPoint P; // input
	private GeoConic l; // input
	private GeoLine[] g; // output

	private GeoNumeric[] n;
	private AlgoPointOnPath[] algoPoint;
	private AlgoClosestPoint closestPoint;

	/**
	 * Creates new AlgoOrthoLinePointConic.
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param P
	 *            point
	 * @param l
	 *            conic result should be orthogonal to
	 */
	public AlgoOrthoLinePointConic(Construction cons, String label, GeoPoint P,
			GeoConic l) {
		super(cons);
		this.P = P;
		this.l = l;
		g = new GeoLine[4];
		n = new GeoNumeric[4];
		algoPoint = new AlgoPointOnPath[4];
		closestPoint = new AlgoClosestPoint(cons, l, P);
		for (int i = 0; i < 4; i++) {
			g[i] = new GeoLine(cons);
			g[i].setStartPoint(P);
			n[i] = new GeoNumeric(cons);
			algoPoint[i] = new AlgoPointOnPath(cons, l, 0, 0, 0, n[i]);
			cons.removeFromConstructionList(algoPoint[i]);
			// algoPoint[i].remove();
		}
		setInputOutput(); // for AlgoElement

		// compute line
		compute();
		for (int i = 0; i < 4; i++) {
			g[0].setLabel(label);
		}

		addIncidence();
	}

	@Override
	public Commands getClassName() {
		return Commands.OrthogonalLine;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_ORTHOGONAL;
	}

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		// for (int i=0; i<4; i++)
		P.addIncidence(g[0], false);
	}

	// for AlgoElement
	@Override
	public void setInputOutput() {
		input = new GeoElement[2];
		input[0] = P;
		input[1] = l.toGeoElement();

		setOutputLength(4);
		for (int i = 0; i < 4; i++) {
			setOutput(i, g[i]);
		}

		setDependencies(); // done by AlgoElement
	}

	GeoConic getC() {
		return l;
	}

	GeoLine[] getLines() {
		return g;
	}

	GeoPoint getP() {
		return P;
	}

	/**
	 * calc the line g through P and normal to l
	 */
	@Override
	public final void compute() {
		GeoVec3D.lineThroughPoints(P, (GeoPoint) closestPoint.getP(), g[0]);
	}

	@Override
	public final String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("LineThroughAPerpendicularToB",
				"Line through %0 perpendicular to %1",
				P.getLabel(tpl), l.toGeoElement().getLabel(tpl));
	}

}
