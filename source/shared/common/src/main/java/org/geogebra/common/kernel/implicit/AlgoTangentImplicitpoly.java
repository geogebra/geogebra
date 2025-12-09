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

package org.geogebra.common.kernel.implicit;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoIntersect;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.algos.TangentAlgo;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algorithm to calculate all tangents to the implicit polynomial equation
 * either going threw a given point or parallel to given line.
 */
public class AlgoTangentImplicitpoly extends AlgoElement
		implements TangentAlgo {

	private GeoImplicit p;

	private GeoPoint[] ip; // tangent points.
	private OutputHandler<GeoLine> tangents;

	private AlgoIntersect algoIntersect;

	private String[] labels;
	private AlgoTangentHelper algoTangentPoly;

	/**
	 * @param c
	 *            construction
	 * @param labels
	 *            labels for output
	 * @param p
	 *            implicit polynomial
	 */
	protected AlgoTangentImplicitpoly(Construction c, String[] labels,
			GeoImplicit p) {
		super(c);
		this.labels = labels;
		this.p = p;

	}

	/**
	 * To compute tangents to poly through given point
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            labels for output
	 * @param p
	 *            implicit polynomial
	 * @param R
	 *            point on tangent
	 */
	public AlgoTangentImplicitpoly(Construction c, String[] labels,
			GeoImplicit p, GeoPointND R) {
		this(c, labels, p);

		boolean pointOnPath = false;

		if (R.getParentAlgorithm() != null) {
			if (R.getParentAlgorithm() instanceof AlgoPointOnPath) {
				AlgoPointOnPath a = (AlgoPointOnPath) R.getParentAlgorithm();
				if (a.getPath() == p) {
					pointOnPath = true; // AlgoPointOnPath (on this curve)
				}
			}
		}
		this.algoTangentPoly = new AlgoImplicitPolyTangentCurve(c, p, R,
				pointOnPath);

		if (!pointOnPath) {
			GeoImplicit tangentCurve = algoTangentPoly.getTangentCurve();
			algoIntersect = new AlgoIntersectImplicitPolynomials(cons, p,
					tangentCurve);

			cons.removeFromConstructionList(algoIntersect);
			ip = algoIntersect.getIntersectionPoints();
		}

		setInputOutput();
	}

	/**
	 * To compute tangents to poly in given direction
	 * 
	 * @param c
	 *            construction
	 * 
	 * @param labels
	 *            labels for output
	 * 
	 * @param p
	 *            implicit polynomial
	 * 
	 * @param g
	 *            line
	 * 
	 * 
	 *            not working #4380
	 */
	public AlgoTangentImplicitpoly(Construction c, String[] labels,
			GeoImplicit p, GeoLineND g) {
		this(c, labels, p);

		this.algoTangentPoly = new AlgoImplicitPolyTangentLine(
				c, p, g);

		GeoImplicit tangentCurve = algoTangentPoly.getTangentCurve();
		algoIntersect = new AlgoIntersectImplicitPolynomials(cons, p, tangentCurve);
		cons.removeFromConstructionList(algoIntersect);
		ip = algoIntersect.getIntersectionPoints();

		setInputOutput();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[1] = p.toGeoElement();

		input[0] = algoTangentPoly.getVec();

		tangents = new OutputHandler<>(() -> {
			GeoLine g1 = new GeoLine(getConstruction());
			g1.setParentAlgorithm(this);
			return g1;
		});
		tangents.setLabels(labels);
		setDependencies();
	}

	@Override
	public void compute() {
		// idea: find intersection points between given curve and
		// tangent curve
		// and construct lines through (x_p, y_p) and intersection points,
		// where (x_p, y_p) is given point.

		if (!algoTangentPoly.vecDefined()) {
			tangents.adjustOutputSize(0);
			return;
		}

		tangents.adjustOutputSize(0);
		ip = algoIntersect == null ? null : algoIntersect
				.getIntersectionPoints();
		this.algoTangentPoly.getTangents(ip, tangents);
	}

	@Override
	public Commands getClassName() {
		return Commands.Tangent;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TANGENTS;
	}

	/**
	 * @return resulting tangents
	 */
	public GeoLine[] getTangents() {
		return tangents.getOutput(new GeoLine[tangents.size()]);
	}

	/**
	 * @param labels
	 *            set labels of tangents
	 */
	public void setLabels(String[] labels) {
		tangents.setLabels(labels);

		update();
	}

	/**
	 * @return tangent points
	 */
	public GeoPoint[] getTangentPoints() {
		return ip;
	}

	@Override
	public GeoPointND getTangentPoint(GeoElement geo, GeoLine line) {
		return this.algoTangentPoly.getTangentPoint(geo, line);
	}

}
