/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCommonTangents.java, dsun48 [6/26/2011]
 *
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Two tangents through point P to conic section c
 */
public class AlgoCommonTangents extends AlgoCommonTangentsND {

	private GeoLine[] tangents;
	// private GeoPoint[] tangentPoints;
	private GeoConic tg;

	private GeoLine currentTangent;
	private AlgoIntersectConics algoIntersect;

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param c
	 *            first conic
	 * @param d
	 *            second conic
	 */
	public AlgoCommonTangents(Construction cons, String[] labels, GeoConicND c,
			GeoConicND d) {
		super(cons);
		this.c = c;
		this.d = d;

		tg = new GeoConic(cons);
		currentTangent = new GeoLine(cons);
		tangents = new GeoLine[4];
		for (int i = 0; i < 4; i++) {
			tangents[i] = new GeoLine(cons);
			tangents[i].setStandardStartPoint();
		}
		setInputOutput();
		compute();
		LabelManager.setLabels(labels, getOutput());

	}

	/**
	 * Inits the helping intersection algorithm to take the current position of
	 * the lines into account. This is important so the the tangent lines are
	 * not switched after loading a file
	 */
	@Override
	public void initForNearToRelationship() {
		for (int i = 0; i < tangents.length; i++) {
			c.polarPoint(tangents[i], (GeoPoint) algoIntersect.getOutput(i));
		}
		algoIntersect.initForNearToRelationship();

	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[] { c.toGeoElement(), d.toGeoElement() };
		setOutputLength(4);
		for (int i = 0; i < 4; i++) {
			setOutput(i, tangents[i]);
		}
		setDependencies();

	}

	@Override
	public void compute() {
		CoordMatrix nm = c.getSymmetricMatrix()
				.mul(d.getSymmetricMatrix().inverse())
				.mul(c.getSymmetricMatrix());
		tg.setMatrix(nm);
		tg.update();
		if (algoIntersect == null) {
			algoIntersect = new AlgoIntersectConics(cons, tg, (GeoConic) c);
		}
		algoIntersect.compute();
		int inner = 0;
		int outer = 0;
		for (int i = 0; i < 4; i++) {
			tangents[i].setUndefined();
		}
		for (int i = 0; i < 4; i++) {
			c.polarLine((GeoPoint) algoIntersect.getOutput(i), currentTangent);
			if (isInner(currentTangent) && inner < 2) {
				tangents[2 + inner].set(currentTangent);
				tangents[2 + inner].getStartPoint()
						.set(algoIntersect.getOutput(i));
				inner++;
			} else if (outer < 2) {
				tangents[outer].set(currentTangent);
				tangents[outer].getStartPoint()
						.set(algoIntersect.getOutput(i));
				outer++;
			}
		}
	}

	private boolean isInner(GeoLine currentTangent2) {
		Coords c0 = currentTangent2.getCoords();
		double sgnC = c.getMidpoint().dotproduct(c0);
		double sgnD = d.getMidpoint().dotproduct(c0);

		return sgnC * sgnD < 0 || !currentTangent2.isDefined();
	}

	@Override
	public GeoPointND getTangentPoint(GeoElement geo, GeoLine line) {
		if (geo == c) {
			return line.getStartPoint();
		}
		return null;
	}

}
