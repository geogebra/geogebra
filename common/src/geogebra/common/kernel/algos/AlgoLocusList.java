/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * locus line for Q dependent on P
 */
public class AlgoLocusList extends AlgoElement {

	public static int MIN_STEPS_REALLY = 16;

	ArrayList<AlgoElement> arrLocus;

	private GeoPoint movingPoint, locusPoint; // input
	private GeoLocus locus; // output

	// for efficient dependency handling
	private GeoElement[] efficientInput, standardInput;

	private Path path; // path of P
	private boolean foundDefined;
	private TreeSet<GeoElement> Qin;

	public AlgoLocusList(Construction cons, GeoPoint Q, GeoPoint P, int try_steps) {

		// just ignoring try_steps here because it would
		// probably not be OK to split MIN_STEPS any more

		super(cons);
		this.movingPoint = P;
		this.locusPoint = Q;

		path = P.getPath();

		locus = new GeoLocus(cons);
		setInputOutput(); // for AlgoElement
		cons.registerEuclidianViewCE(this);
		compute();

		// we may have created a starting point for the path now
		// make sure that the movingPoint in the main construction
		// uses the correct path parameter for it
		path.pointChanged(P);
	}

	public AlgoLocusList(Construction cons, String label, GeoPoint Q, GeoPoint P) {
		super(cons);
		this.movingPoint = P;
		this.locusPoint = Q;

		path = P.getPath();

		locus = new GeoLocus(cons);
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

		if (arrLocus == null)
			arrLocus = new ArrayList<AlgoElement>();

		// AlgoLocusList should be called only when the path is a GeoList
		GeoElement actel, pathp;
		AlgoElement actal;
		Path oldel;
		// however...
		try {
			int try_steps = PathMover.MIN_STEPS / ((GeoList)path).size() + 1;
			if (try_steps < MIN_STEPS_REALLY) {
				try_steps = MIN_STEPS_REALLY;
			}
			int arrLocusSize = arrLocus.size();
			for (int i = arrLocusSize - 1; i >= ((GeoList)path).size(); i--) {
				arrLocus.remove(i);
			}
			arrLocusSize = arrLocus.size();
			for (int i = 0; i < ((GeoList)path).size(); i++) {
				actel = ((GeoList)path).get(i);
				if (actel != null && actel instanceof Path) {
					if (i < arrLocusSize) {
						if (arrLocus.get(i) instanceof AlgoLocusList) {
							oldel = ((AlgoLocusList)arrLocus.get(i)).getMovingPoint().getPath();
						} else if (arrLocus.get(i) instanceof AlgoLocus) {
							oldel = ((AlgoLocus)arrLocus.get(i)).getMovingPoint().getPath();
						} else {
							oldel = null;
						}
						if (oldel == actel)
							continue;
					}
					P.setPath((Path)actel);
					if (actel instanceof GeoList) {
						actal = new AlgoLocusList(cons, Q, P, try_steps);
						pathp = ((AlgoLocusList)actal).getLocus();
					} else {
						actal = new AlgoLocus(cons, Q, P, try_steps);
						pathp = ((AlgoLocus)actal).getLocus();
					}
					cons.removeFromAlgorithmList(actal);
					cons.removeFromConstructionList(actal);
					cons.removeFromConstructionList(pathp);
					P.setPath(path);
					if (i < arrLocusSize)
						arrLocus.set(i, actal);
					else
						arrLocus.add(actal);
				} else {
					if (i < arrLocusSize)
						arrLocus.set(i, null);
					else
						arrLocus.add(null);
				}
			}
		} catch (Exception ex) {
			app.error(ex.getMessage());
		}
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoLocus;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_LOCUS;
	}

	public ArrayList getMoveableInputPoints() {
		// TODO ?
		return null;
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
	 * A way more descriptive name for
	 * the getter.
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
		TreeSet<GeoElement> inSet = new TreeSet<GeoElement>();
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

	@Override
	final public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
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
			if (actLocus instanceof AlgoLocusList)
				actGeo = ((AlgoLocusList) actLocus).getLocus();
			else if (actLocus instanceof AlgoLocus)
				actGeo = ((AlgoLocus)actLocus).getLocus();
			else
				continue;
			for (int j = 0; j < actGeo.getPointLength(); j++) {
				insertPoint(
					actGeo.getPoints().get(j).x,
					actGeo.getPoints().get(j).y,
					(j == 0) ? false :
					actGeo.getPoints().get(j).lineTo
				);
			}
			if (actGeo.getPointLength() > 0)
				foundDefined = true;
		}
		// set defined/undefined
		locus.setDefined(foundDefined);
	}

	private static boolean isPathIterable(GeoElement geoElement) {
		if(geoElement.isGeoImplicitPoly())
			return ((GeoImplicitPoly)geoElement).isOnScreen();
		return geoElement.isDefined();
	}

	private void insertPoint(double x, double y, boolean lineTo) {

		// Application.debug("insertPoint: " + x + ", " + y + ", lineto: " +
		// lineTo);
		locus.insertPoint(x, y, lineTo);
	}

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Consider locusequability
		return false;
	}

}
