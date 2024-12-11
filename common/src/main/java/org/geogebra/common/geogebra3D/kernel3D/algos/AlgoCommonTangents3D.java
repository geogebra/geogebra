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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoCommonTangentsND;
import org.geogebra.common.kernel.algos.AlgoIntersectConics;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Two tangents through point P to conic section c
 */
public class AlgoCommonTangents3D extends AlgoCommonTangentsND {

	private GeoLine3D[] tangents;
	// private GeoPoint[] tangentPoints;
	private GeoConic tg;
	private GeoConic c2d;
	private GeoConic d2d;
	private GeoLine3D currentTangent;
	private AlgoIntersectConics algoIntersect;
	private GeoLine polar;

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
	public AlgoCommonTangents3D(Construction cons, String[] labels,
			GeoConicND c,
			GeoConicND d) {
		super(cons);
		this.c = c;
		this.d = d;

		tg = new GeoConic(cons);
		c2d = new GeoConic(cons);
		d2d = new GeoConic(cons);
		currentTangent = new GeoLine3D(cons);
		tangents = new GeoLine3D[4];
		for (int i = 0; i < 4; i++) {
			tangents[i] = new GeoLine3D(cons);
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
		// TODO
		// for (int i = 0; i < tangents.length; i++) {
		// tangents[i].setCoord((GeoPoint) algoIntersect.getOutput(i), null);
		// c.polarPoint(tangents[i], (GeoPoint) algoIntersect.getOutput(i));
		// }
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
		AlgoIntersectConics3D.transformConics(c, d, c2d, d2d);
		CoordMatrix nm = c2d.getSymmetricMatrix()
				.mul(d2d.getSymmetricMatrix().inverse())
				.mul(c2d.getSymmetricMatrix());
		tg.setMatrix(nm);
		tg.update();
		if (algoIntersect == null) {
			algoIntersect = new AlgoIntersectConics(cons, tg, c2d);
		}
		algoIntersect.compute();
		int inner = 0;
		int outer = 0;
		for (int i = 0; i < 4; i++) {
			tangents[i].setUndefined();
		}
		Coords cross = c.getCoordSys().getNormal()
				.crossProduct(d.getCoordSys().getNormal());
		if (!cross.equalsForKernel(0, Kernel.MIN_PRECISION)) {
			return;
		}
		for (int i = 0; i < 4; i++) {
			polarLine((GeoPoint) algoIntersect.getOutput(i));
			if (isInner(polar)) {
				tangents[2 + inner].set(currentTangent);
				// tangents[inner]
				// .setStartPoint((GeoPointND) algoIntersect.getOutput(i));
				inner++;
			} else if (outer < 2) {
				tangents[outer].set(currentTangent);
				// tangents[2 + outer]
				// .setStartPoint((GeoPointND) algoIntersect.getOutput(i));
				outer++;
			}
		}

	}

	private void polarLine(GeoPoint output) {
		polar = new GeoLine(cons);
		c2d.polarLine(output, polar);
		if (!DoubleUtil.isZero(polar.x)) {
			currentTangent.setCoord(
					c.getCoordSys().getPoint(-polar.z / polar.x, 0),
				c.getCoordSys().getVector(-polar.y, polar.x));
		} else {
			currentTangent.setCoord(
					c.getCoordSys().getPoint(0, -polar.z / polar.y),
					c.getCoordSys().getVector(-polar.y, polar.x));
		}

	}

	private boolean isInner(GeoLine currentTangent2) {
		Coords c0 = currentTangent2.getCoords();
		c2d.classifyConic(false);
		d2d.classifyConic(false);
		double sgnC = c2d.getMidpoint().dotproduct(c0);
		double sgnD = d2d.getMidpoint().dotproduct(c0);

		return sgnC * sgnD < 0;
	}

	@Override
	public GeoPointND getTangentPoint(GeoElement geo, GeoLine line) {
		if (geo == c) {
			return line.getStartPoint();
		}
		GeoPoint ret = new GeoPoint(cons);
		d.polarPoint(line, ret);
		return ret;
	}

}
