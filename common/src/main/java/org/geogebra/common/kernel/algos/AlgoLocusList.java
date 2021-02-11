/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.util.debug.Log;

/**
 * locus line for Q dependent on P
 */
public class AlgoLocusList extends AlgoElement {

	/** min steps */
	public static final int MIN_STEPS_REALLY = 16;

	ArrayList<AlgoElement> arrLocus;

	private GeoPoint movingPoint; // input
	private GeoPoint locusPoint; // input
	private GeoLocus locus; // output

	// for efficient dependency handling
	private GeoElement[] efficientInput;
	private GeoElement[] standardInput;

	private Path path; // path of P
	private boolean foundDefined;
	private TreeSet<GeoElement> Qin;

	private boolean shouldUpdateScreenBorders = false;

	/**
	 * @param cons
	 *            construction
	 * @param Q
	 *            locus point
	 * @param P
	 *            moving point
	 * @param registerCE
	 *            add to construction?
	 */
	public AlgoLocusList(Construction cons, GeoPoint Q, GeoPoint P,
			boolean registerCE) {

		// just ignoring try_steps here because it would
		// probably not be OK to split MIN_STEPS any more

		super(cons, registerCE);
		this.movingPoint = P;
		this.locusPoint = Q;

		path = P.getPath();

		locus = new GeoLocus(cons);
		// locus.setFillable(false);

		setInputOutput(); // for AlgoElement
		compute();

		// we may have created a starting point for the path now
		// make sure that the movingPoint in the main construction
		// uses the correct path parameter for it
		path.pointChanged(P);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param Q
	 *            locus point
	 * @param P
	 *            moving point
	 */
	public AlgoLocusList(Construction cons, String label, GeoPoint Q,
			GeoPoint P) {
		super(cons);
		this.movingPoint = P;
		this.locusPoint = Q;

		path = P.getPath();

		locus = new GeoLocus(cons);
		// locus.setFillable(false);

		updateScreenBorders();
		setInputOutput(); // for AlgoElement
		cons.registerEuclidianViewCE(this);
		compute();

		// we may have created a starting point for the path now
		// make sure that the movingPoint in the main construction
		// uses the correct path parameter for it
		path.pointChanged(P);

		locus.setLabel(label);
	}

	private void fillLocusArray(GeoPoint Q, GeoPoint P) {

		if (arrLocus == null) {
			arrLocus = new ArrayList<>();
		}

		// AlgoLocusList should be called only when the path is a GeoList
		GeoElement actel, pathp;
		AlgoElement actal;
		Path oldel;
		// however...
		try {
			int try_steps = PathMover.MIN_STEPS / ((GeoList) path).size() + 1;
			if (try_steps < MIN_STEPS_REALLY) {
				try_steps = MIN_STEPS_REALLY;
			}
			int arrLocusSize = arrLocus.size();
			for (int i = arrLocusSize - 1; i >= ((GeoList) path).size(); i--) {
				arrLocus.remove(i);
			}
			arrLocusSize = arrLocus.size();
			for (int i = 0; i < ((GeoList) path).size(); i++) {
				actel = ((GeoList) path).get(i);
				if (actel instanceof Path) {
					if (i < arrLocusSize) {
						if (arrLocus.get(i) instanceof AlgoLocusList) {
							oldel = ((AlgoLocusList) arrLocus.get(i))
									.getMovingPoint().getPath();
						} else if (arrLocus.get(i) instanceof AlgoLocus) {
							oldel = ((AlgoLocus) arrLocus.get(i))
									.getMovingPoint().getPath();
						} else {
							oldel = null;
						}
						if (oldel == actel) {
							if (shouldUpdateScreenBorders) {
								if (arrLocus.get(i) instanceof AlgoLocus) {
									((AlgoLocus) arrLocus.get(i))
											.updateScreenBorders();
								} else if (arrLocus
										.get(i) instanceof AlgoLocusList) {
									((AlgoLocusList) arrLocus.get(i))
											.updateScreenBorders();
								}
							}
							arrLocus.get(i).compute();
							continue;
						}
					}
					P.setPath((Path) actel);

					// new AlgoLocus(List) does not need updateScreenBorders and
					// compute

					if (actel instanceof GeoList) {
						if (((GeoList) actel).shouldUseAlgoLocusList(true)) {
							actal = new AlgoLocusList(cons, Q, P,
									false);
							pathp = ((AlgoLocusList) actal).getLocus();
						} else {
							actal = new AlgoLocus(cons, Q, P, try_steps, false);
							pathp = ((AlgoLocus) actal).getLocus();
						}
					} else {
						actal = new AlgoLocus(cons, Q, P, try_steps, false);
						pathp = ((AlgoLocus) actal).getLocus();
					}
					cons.removeFromAlgorithmList(actal);
					// cons.removeFromConstructionList(actal);
					// cons.unregisterEuclidianViewCE(actal);
					cons.removeFromConstructionList(pathp);
					P.setPath(path);
					if (i < arrLocusSize) {
						arrLocus.set(i, actal);
					} else {
						arrLocus.add(actal);
					}
				} else {
					if (i < arrLocusSize) {
						arrLocus.set(i, null);
					} else {
						arrLocus.add(null);
					}
				}
			}
		} catch (Exception ex) {
			Log.error(ex.getMessage());
		}
	}

	@Override
	public Commands getClassName() {
		return Commands.Locus;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_LOCUS;
	}

	/**
	 * Returns the dependent point
	 * 
	 * @return dependent point Q
	 */
	public GeoPoint getQ() {
		return locusPoint;
	}

	/**
	 * A way more descriptive name for the getter.
	 * 
	 * @return dependent point Q
	 */
	public GeoPoint getLocusPoint() {
		return locusPoint;
	}

	/**
	 * @return moving point P.
	 */
	public GeoPoint getMovingPoint() {
		return movingPoint;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		// it is inefficient to have Q and P as input
		// let's take all independent parents of Q
		// and the path as input
		TreeSet<GeoElement> inSet = new TreeSet<>();
		inSet.add(path.toGeoElement());

		// we need all independent parents of Q PLUS
		// all parents of Q that are points on a path
		Qin = locusPoint.getAllPredecessors();
		Iterator<GeoElement> it = Qin.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isIndependent() || geo.isPointOnPath()) {
				inSet.add(geo);
			}
		}
		// remove P from input set!
		inSet.remove(movingPoint);

		efficientInput = new GeoElement[inSet.size()];
		it = inSet.iterator();
		int i = 0;
		while (it.hasNext()) {
			efficientInput[i] = it.next();
			i++;
		}

		// the standardInput array should be used for
		// the dependency graph
		standardInput = new GeoElement[2];
		standardInput[0] = locusPoint;
		standardInput[1] = movingPoint;

		setOutputLength(1);
		setOutput(0, locus);

		// handle dependencies
		setEfficientDependencies(standardInput, efficientInput);
	}

	/**
	 * Returns locus
	 * 
	 * @return locus
	 */
	public GeoLocus getLocus() {
		return locus;
	}

	// compute locus line
	@Override
	public final void compute() {
		if (!movingPoint.isDefined() || !isPathIterable(path.toGeoElement())) {
			locus.setUndefined();
			return;
		}

		// it is necessary to call this here to make arrLocus up-to-date
		fillLocusArray(locusPoint, movingPoint);

		locus.clearPoints();
		foundDefined = false;

		AlgoElement actLocus;
		GeoLocus actGeo;
		for (int i = 0; i < arrLocus.size(); i++) {
			actLocus = arrLocus.get(i);
			if (actLocus instanceof AlgoLocusList) {
				actGeo = ((AlgoLocusList) actLocus).getLocus();
			} else if (actLocus instanceof AlgoLocus) {
				actGeo = (GeoLocus) ((AlgoLocus) actLocus).getLocus();
			} else {
				continue;
			}
			for (int j = 0; j < actGeo.getPointLength(); j++) {
				insertPoint(actGeo.getPoints().get(j).x, actGeo.getPoints()
						.get(j).y, j != 0
						&& actGeo.getPoints().get(j).getLineTo());
			}
			if (actGeo.getPointLength() > 0) {
				foundDefined = true;
			}
		}
		// set defined/undefined
		locus.setDefined(foundDefined);
		shouldUpdateScreenBorders = false;
	}

	private static boolean isPathIterable(GeoElement geoElement) {
		if (geoElement.isGeoImplicitCurve()) {
			return ((GeoImplicit) geoElement).isOnScreen();
		}
		return geoElement.isDefined();
	}

	private void insertPoint(double x, double y, boolean lineTo) {

		// Application.debug("insertPoint: " + x + ", " + y + ", lineto: " +
		// lineTo);
		locus.insertPoint(x, y, lineTo ? SegmentType.LINE_TO
				: SegmentType.MOVE_TO);
	}

	@Override
	public boolean euclidianViewUpdate() {
		updateScreenBorders();
		update();
		return false;
	}

	/**
	 * This should register the wish that screen borders should be updated in
	 * the subloci in time
	 */
	void updateScreenBorders() {
		shouldUpdateScreenBorders = true;
	}
}
