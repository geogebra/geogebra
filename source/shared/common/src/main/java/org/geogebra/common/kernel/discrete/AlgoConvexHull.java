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

package org.geogebra.common.kernel.discrete;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.GraphAlgo;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.discrete.geom.Point2D;
import org.geogebra.common.kernel.discrete.geom.algorithms.ConvexHull;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.DoubleUtil;

/**
 * Convex hull of a set of points
 * 
 * @author Michael Borcherds
 * 
 */

public class AlgoConvexHull extends AlgoElement implements GraphAlgo {

	private GeoList inputList; // input
	private GeoLocus locus; // output
	private ArrayList<MyPoint> al;
	private ArrayList<Point2D> vl;

	/**
	 * @param cons
	 *            cons
	 * @param label
	 *            label
	 * @param inputList
	 *            list of GeoPoints
	 */
	public AlgoConvexHull(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		locus = new GeoLocus(cons);

		setInputOutput();
		compute();
		locus.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ConvexHull;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = inputList;

		setOnlyOutput(locus);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return convex hull as a GeoLocus
	 */
	public GeoLocus getResult() {
		return locus;
	}

	@Override
	public void compute() {

		int size = inputList.size();
		if (!inputList.isDefined() || size == 0) {
			locus.setUndefined();
			return;
		}

		if (vl == null) {
			vl = new ArrayList<>();
		} else {
			vl.clear();
		}

		double[] inhom = new double[2];

		for (int i = 0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointND p = (GeoPointND) geo;
				p.getInhomCoords(inhom);

				// make sure duplicates aren't added
				if (!contains(vl, inhom[0], inhom[1])) {
					if (Double.isNaN(inhom[0]) || Double.isNaN(inhom[1])) {
						locus.setUndefined();
						return;
					}
					vl.add(new Point2D(inhom[0], inhom[1]));
				}
			}
		}

		if (al == null) {
			al = new ArrayList<>();
		} else {
			al.clear();
		}

		if (vl.size() == 1) {
			Point2D p = vl.get(0);
			al.add(new MyPoint(p.getX(), p.getY(), SegmentType.MOVE_TO));
			al.add(new MyPoint(p.getX(), p.getY(), SegmentType.LINE_TO));
			locus.setPoints(al);
			locus.setDefined(true);
			return;
		}

		if (vl.size() == 0) {
			locus.setUndefined();
			return;
		}

		List<Point2D> hull = ConvexHull.makeHull(vl);

		for (int i = 0; i < hull.size(); i++) {
			Point2D p = hull.get(i);
			al.add(new MyPoint(p.getX(), p.getY(), i != 0 ? SegmentType.LINE_TO
					: SegmentType.MOVE_TO));

		}

		if (hull.size() == 0) {
			locus.setDefined(false);
			return;
		}

		// close the polygon
		Point2D p = hull.get(0);
		al.add(new MyPoint(p.getX(), p.getY(), SegmentType.LINE_TO));

		locus.setPoints(al);
		locus.setDefined(true);

	}

	private static boolean contains(ArrayList<Point2D> vl2, double x,
			double y) {
		for (int i = 0; i < vl2.size(); i++) {
			Point2D p = vl2.get(i);
			if (DoubleUtil.isEqual(p.getX(), x) && DoubleUtil.isEqual(p.getY(), y)) {
				return true;
			}
		}

		return false;
	}

}
