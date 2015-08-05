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

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.MacroKernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.implicit.GeoImplicitPoly;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;

/**
 * locus line for Q dependent on P
 */
public abstract class AlgoLocusND<T extends MyPoint> extends AlgoElement {

	// TODO: update locus algorithm
	// * locus of Q=(x(B), a) with a= integral[f(x), 0, x(B)] and B is point on
	// x-axis freezes GeoGebra
	// MAX_TIME handling does not solve this yet
	//

	/** maximum time for the computation of one locus point in millis **/
	public static int MAX_TIME_FOR_ONE_STEP = 500;

	public int MIN_STEPS_INSTANCE = PathMover.MIN_STEPS;

	protected static int MAX_X_PIXEL_DIST = 5;
	private static int MAX_Y_PIXEL_DIST = 5;

	protected GeoPointND movingPoint, locusPoint; // input
	protected GeoLocusND<T> locus; // output

	// for efficient dependency handling
	private GeoElement[] efficientInput, standardInput;

	private Path path; // path of P
	private PathMover pathMover;
	protected int pointCount;

	// copies of P and Q in a macro kernel
	protected GeoPointND Pcopy, Qcopy, PstartPos, QstartPos;
	protected double lastX, lastY;

	protected double[] maxXdist, maxYdist, xmin = new double[3],
			xmax = new double[3], ymin = new double[3], ymax = new double[3],
			farXmin = new double[3], farXmax = new double[3],
			farYmin = new double[3], farYmax = new double[3];

	// private Line2D.Double tempLine = new Line2D.Double();
	private GRectangle2D[] nearToScreenRect = {
			org.geogebra.common.factories.AwtFactory.prototype.newRectangle2D(),
			org.geogebra.common.factories.AwtFactory.prototype.newRectangle2D(),
			org.geogebra.common.factories.AwtFactory.prototype.newRectangle2D() };

	private boolean continuous;
	protected boolean[] lastFarAway = { false, false, false };
	private boolean foundDefined;
	private boolean maxTimeExceeded;
	private Construction macroCons;
	private MacroKernel macroKernel;
	// private AlgorithmSet macroConsAlgoSet;
	// list with all original elements used for the macro construction
	private TreeSet<ConstructionElement> locusConsOrigElements;
	private TreeSet<GeoElement> Qin;

	private int views = 1;

	// private Updater updater;

	// Constructor called from AlgoLocusList
	public AlgoLocusND(Construction cons, GeoPointND Q, GeoPointND P,
			int min_steps, boolean registerCE) {
		super(cons);

		createMaxDistances();

		MIN_STEPS_INSTANCE = min_steps;
		this.movingPoint = P;
		this.locusPoint = Q;

		path = P.getPath();
		pathMover = path.createPathMover();

		createStartPos(cons);

		// we may need locus in init when something goes wrong
		locus = newGeoLocus(cons);
		init();
		updateScreenBorders();

		setInputOutput(); // for AlgoElement

		if (registerCE) {
			cons.registerEuclidianViewCE(this);
		}

		compute();

		// we may have created a starting point for the path now
		// make sure that the movingPoint in the main construction
		// uses the correct path parameter for it
		path.pointChanged(P);
	}

	/**
	 * create max distances arrays
	 */
	protected void createMaxDistances() {
		maxXdist = new double[3];
		maxYdist = new double[3];
	}

	/**
	 * create start pos points
	 * 
	 * @param cons
	 *            construction
	 */
	abstract protected void createStartPos(Construction cons);

	/**
	 * 
	 * @param cons
	 *            construction
	 * @return new GeoLocus
	 */
	abstract protected GeoLocusND<T> newGeoLocus(Construction cons);

	public AlgoLocusND(Construction cons, String label, GeoPointND Q,
			GeoPointND P) {
		this(cons, Q, P, PathMover.MIN_STEPS, true);
		locus.setLabel(label);
	}

	// private void printMacroConsStatus() {
	// System.out.print("MOVER POINT: " + Pcopy);
	// System.out.print(", pp.t: " + Pcopy.getPathParameter().t);
	// System.out.println(", locus point: " + Qcopy);
	//
	// TreeSet geos = macroCons.getGeoSetLabelOrder();
	// Iterator it = geos.iterator();
	// while (it.hasNext()) {
	// GeoElement geo = (GeoElement) it.next();
	// System.out.println("  " + geo);
	// }
	// }

	@Override
	public Commands getClassName() {
		return Commands.Locus;
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
	public GeoPointND getQ() {
		return locusPoint;
	}

	/**
	 * A way more descriptive name for the getter.
	 * 
	 * @return dependent point Q
	 */
	public GeoPointND getLocusPoint() {
		return locusPoint;
	}

	/**
	 * @return moving point P.
	 */
	public GeoPointND getMovingPoint() {
		return movingPoint;
	}

	private void init() {
		// copy the construction
		Qin = ((GeoElement) locusPoint).getAllPredecessors(); // all parents of
																// Q

		// get intersection of all children of P and all parents of Q
		locusConsOrigElements = new TreeSet<ConstructionElement>();
		TreeSet<Long> usedAlgoIds = new TreeSet<Long>();
		Iterator<GeoElement> it = Qin.iterator();
		while (it.hasNext()) {
			GeoElement parent = it.next();

			if (parent.isLabelSet()
					&& parent.isChildOf((GeoElement) movingPoint)) {
				// note: locusConsOrigElements will contain AlgoElement and
				// GeoElement objects
				Macro.addDependentElement(parent, locusConsOrigElements,
						usedAlgoIds);
			}
		}

		// ensure that P and Q have labels set
		// Note: we have to undo this at the end of this method !!!
		boolean isLabeledP = movingPoint.isLabelSet();
		if (!isLabeledP) {
			((GeoElement) movingPoint)
					.setLabelSimple(((GeoElement) movingPoint)
							.getDefaultLabel());
			((GeoElement) movingPoint).labelSet = true;
		}
		boolean isLabeledQ = locusPoint.isLabelSet();
		if (!isLabeledQ) {
			((GeoElement) locusPoint).setLabelSimple(((GeoElement) locusPoint)
					.getDefaultLabel());
			((GeoElement) locusPoint).labelSet = true;
		}

		// add moving point on line
		// locusConsOrigElements.add(movingPoint);

		// instead, add the moving point by adding incidences of it to the path
		// at the same time (see buildLocusMacroConstruction)
		// note: this will add the parent algo of the moving point, which is
		// AlgoPointOnPath...

		// In theory, this is not harmful as locusConsOrigElements is just used
		// in AlgoLocus,
		// and macroCons.updateConstruction is only called one time, after
		// resetMacroConstruction
		Macro.addDependentElement((GeoElement) movingPoint,
				locusConsOrigElements, usedAlgoIds);

		// add locus creating point and its algorithm to locusConsOrigElements
		Macro.addDependentElement((GeoElement) locusPoint,
				locusConsOrigElements, usedAlgoIds);

		// create macro construction
		buildLocusMacroConstruction(locusConsOrigElements);

		// if we used temp labels remove them again
		if (!isLabeledP)
			((GeoElement) movingPoint).labelSet = false;
		if (!isLabeledQ)
			((GeoElement) locusPoint).labelSet = false;
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
		standardInput[0] = (GeoElement) locusPoint;
		standardInput[1] = (GeoElement) movingPoint;

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
	public GeoLocusND<T> getLocus() {
		return locus;
	}

	private void buildLocusMacroConstruction(
			TreeSet<ConstructionElement> locusConsElements) {
		// build macro construction
		macroKernel = kernel.newMacroKernel();
		macroKernel.setGlobalVariableLookup(true);

		// tell the macro construction about reserved names:
		// these names will not be looked up in the parent
		// construction
		Iterator<ConstructionElement> it = locusConsElements.iterator();
		while (it.hasNext()) {
			ConstructionElement ce = it.next();
			if (ce.isGeoElement()) {
				GeoElement geo = (GeoElement) ce;
				macroKernel.addReservedLabel(geo
						.getLabel(StringTemplate.defaultTemplate));
			}
		}

		try {
			// get XML for macro construction of P -> Q
			String locusConsXML = Macro
					.buildMacroXML(kernel, locusConsElements).toString();

			macroKernel.loadXML(locusConsXML);

			// get the copies of P and Q from the macro kernel
			Pcopy = (GeoPointND) macroKernel
					.lookupLabel(((GeoElement) movingPoint).getLabelSimple());
			((GeoElement) Pcopy).setFixed(false);
			Pcopy.setPath(movingPoint.getPath());

			// alternative way to add the incidence of the path to Pcopy
			// see init()
			// Pcopy.addIncidence((GeoElement)path);
			// AlgoIntersectLineConic.resetPossibleSpecialCase();//not
			// implemented

			Qcopy = (GeoPointND) macroKernel
					.lookupLabel(((GeoElement) locusPoint).getLabelSimple());
			macroCons = macroKernel.getConstruction();

			/*
			 * // make sure that the references to e.g. start/end point of a
			 * segment are not // changed later on. This is achieved by setting
			 * isMacroOutput to true it = macroCons.getGeoElementsIterator();
			 * while (it.hasNext()) { GeoElement geo = (GeoElement) it.next();
			 * geo.isAlgoMacroOutput = true; } Pcopy.isAlgoMacroOutput = false;
			 */
		} catch (Exception e) {
			e.printStackTrace();
			locus.setUndefined();
			macroCons = null;
		}

		// //Application.debug("P: " + P + ", kernel class: " +
		// P.kernel.getClass());
		// Application.debug("Pcopy: " + Pcopy + ", kernel class: " +
		// Pcopy.kernel.getClass());
		// //Application.debug("P == Pcopy: " + (P == Pcopy));
		// //Application.debug("Q: " + Q + ", kernel class: " +
		// Q.kernel.getClass());
		// Application.debug("Qcopy: " + Qcopy + ", kernel class: " +
		// Qcopy.kernel.getClass());
		// //Application.debug("Q == Qcopy: " + (Q == Qcopy));
	}

	/**
	 * Set all elements in locusConsElements to the current values of the main
	 * construction
	 */
	private void resetMacroConstruction() {
		Iterator<ConstructionElement> it = locusConsOrigElements.iterator();
		while (it.hasNext()) {
			ConstructionElement ce = it.next();
			if (ce.isGeoElement()) {
				GeoElement geoOrig = (GeoElement) ce;
				// do not copy functions, their expressions already
				// include references to the correct other geos
				if (!geoOrig.isGeoFunction()) {
					GeoElement geoCopy = macroCons.lookupLabel(geoOrig
							.getLabelSimple());
					if (geoCopy != null) {
						try {
							geoCopy.set(geoOrig);
							geoCopy.update();
						} catch (Exception e) {
							App.debug("AlgoLocus: error in resetMacroConstruction(): "
									+ e.getMessage());
						}
					}
				}
			}
		}
	}

	// compute locus line
	@Override
	public final void compute() {
		if (!movingPoint.isDefined() || macroCons == null
				|| !isPathIterable(path.toGeoElement())) {
			locus.setUndefined();
			return;
		}
		updateScreenBordersIfNecessary();

		locus.clearPoints();
		clearCache();
		pointCount = 0;
		lastX = Double.MAX_VALUE;
		lastY = Double.MAX_VALUE;
		maxTimeExceeded = false;
		foundDefined = false;
		boolean prevQcopyDefined = false;
		int max_runs;

		// continuous kernel?
		continuous = kernel.isContinuous();
		macroKernel.setContinuous(continuous);

		// update macro construction with current values of global vars
		resetMacroConstruction();
		macroCons.updateConstruction();

		// use current position of movingPoint to start Pcopy
		pathMover.init(Pcopy, MIN_STEPS_INSTANCE);

		if (continuous) {
			// continous constructions may need several parameter run throughs
			// to draw all parts of the locus
			max_runs = GeoLocus.MAX_PATH_RUNS;
		} else {
			max_runs = 1;
		}

		// update Pcopy to compute Qcopy
		pcopyUpdateCascade();
		prevQcopyDefined = Qcopy.isDefined() && !Qcopy.isInfinite();

		// move Pcopy along the path
		// do this until Qcopy comes back to its start position
		// for continuous constructions
		// this may require several runs of Pcopy along the whole path

		int runs = 1;
		int MAX_LOOPS = 2 * PathMover.MAX_POINTS * views;
		int whileLoops = 0;

		do {
			boolean finishedRun = false;
			while (!finishedRun && !maxTimeExceeded
					&& pointCount <= PathMover.MAX_POINTS * views
					&& whileLoops <= MAX_LOOPS) {
				whileLoops++;

				// lineTo may be false due to a parameter jump
				// i.e. param in [0,1] gets bigger than 1 and thus jumps to 0
				boolean parameterJump = !pathMover.getNext(Pcopy);
				boolean stepChanged = false;

				// update construction
				pcopyUpdateCascade();

				// Qcopy DEFINED
				if (Qcopy.isDefined() && !Qcopy.isInfinite()) {
					// STANDARD CASE: no parameter jump
					if (!parameterJump) {
						// make steps smaller until distance ok to connect with
						// last point
						while (Qcopy.isDefined() && !Qcopy.isInfinite()
								&& !distanceOK(Qcopy) && !maxTimeExceeded) {
							// go back and try smaller step
							boolean smallerStep = pathMover.smallerStep();
							if (!smallerStep)
								break;

							stepChanged = true;
							pathMover.stepBack();
							pathMover.getNext(Pcopy);

							// update construction
							pcopyUpdateCascade();
						}

						if (Qcopy.isDefined() && !Qcopy.isInfinite()) {
							// draw point
							insertPoint(Qcopy, distanceSmall(Qcopy, true));
							prevQcopyDefined = true;
						}
					}

					// PARAMETER jump: !lineTo
					else {
						// draw point
						insertPoint(Qcopy, distanceSmall(Qcopy, true));
						prevQcopyDefined = true;
					}
				}

				// Qcopy NOT DEFINED
				else {
					// check if we moved from defined to undefined case:
					// step back and try with smaller step
					if (prevQcopyDefined && !parameterJump) {
						pathMover.stepBack();
						// set smallest step
						if (!pathMover.smallerStep()) {
							prevQcopyDefined = false;
						} else
							stepChanged = true;
					}

					// add better undefined case support for continuous curves
					// maybe change orientation of path mover
				}

				// if we didn't decrease the step width increase it
				if (!stepChanged) {
					pathMover.biggerStep();
				}

				// end of run: the next step would pass the start position
				if (!pathMover.hasNext()) {
					if (distanceSmall(QstartPos, true)) {
						// draw line back to first point when it's close enough
						insertPoint(QstartPos, true);
						finishedRun = true;
					} else {
						// decrease step until another step is possible
						while (!pathMover.hasNext() && pathMover.smallerStep()) {
							// do nothing
						}
						// no smaller step possible: run finished
						if (!pathMover.hasNext())
							finishedRun = true;
					}
				}
			}

			// calculating the steps took too long, so we stopped somewhere
			if (maxTimeExceeded) {
				System.err.println("AlgoLocus: max time exceeded");
				return;
			}
			// make sure that Pcopy is back at startPos now
			// look at Qcopy at startPos
			((GeoElement) Pcopy).set((GeoElement) PstartPos);
			pcopyUpdateCascade();
			if (differentFromLast(Qcopy))
				insertPoint(Qcopy, distanceSmall(Qcopy, true));

			// Application.debug("run: " + runs);
			// Application.debug("pointCount: " + pointCount);
			// Application.debug("  startPos: " + QstartPos);
			// Application.debug("  Qcopy: " + Qcopy);

			// we are finished with all runs
			// if we got back to the start position of Qcopy
			// AND if the direction of moving along the path
			// is positive like in the beginning
			if (pathMover.hasPositiveOrientation()) {
				boolean equal = areEqual(QstartPos, Qcopy);
				if (equal) {
					break;
				}
			}

			pathMover.resetStartParameter();
			runs++;
		} while (runs < max_runs);

		// set defined/undefined
		locus.setDefined(foundDefined);

		// System.out.println("  first point: " +
		// locus.getMyPointList().get(0));
		// ArrayList list = locus.getMyPointList();
		// for (int i=list.size()-10; i < list.size()-1; i++) {
		// System.out.println("      point: " + list.get(i));
		// }
		// System.out.println("  last  point: " +
		// locus.getMyPointList().get(pointCount-1));

		// Application.debug("LOCUS COMPUTE updateCascades: " + countUpdates +
		// ", cache used: " + useCache);
	}

	/**
	 * 
	 * @param point
	 *            point
	 * @return true if point is not equal to last coords
	 */
	abstract protected boolean differentFromLast(GeoPointND point);

	/**
	 * 
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @return true if p1 and p2 has same coords for min precision
	 */
	abstract protected boolean areEqual(GeoPointND p1, GeoPointND p2);

	private static boolean isPathIterable(GeoElement geoElement) {
		if (geoElement.isGeoImplicitPoly())
			return ((GeoImplicitPoly) geoElement).isOnScreen();
		return geoElement.isDefined();
	}

	/**
	 * Calls Pcopy.updateCascade() to compute Qcopy. For non-continous
	 * constructions caching of previous paramater positions is used.
	 */
	private void pcopyUpdateCascade() {
		if (continuous) {
			// CONTINOUS construction
			// don't use caching for continuous constructions:
			// the same position of Pcopy can have different results for Qcopy
			Pcopy.updateCascade();
		} else {
			// NON-CONTINOUS construction
			// check if the path parameter's resulting Qcopy is already in cache
			double param = Pcopy.getPathParameter().t;
			GPoint2D cachedPoint = getCachedPoint(param);

			if (cachedPoint == null) {
				// measure time needed for update of construction
				long startTime = System.currentTimeMillis();

				// result not in cache: update Pcopy to compute Qcopy
				Pcopy.updateCascade();

				long updateTime = System.currentTimeMillis() - startTime;

				// if it takes too much time to calculate a single step, we stop
				if (updateTime > MAX_TIME_FOR_ONE_STEP) {
					App.debug("AlgoLocus: max time exceeded " + updateTime);
					maxTimeExceeded = true;
				}

				// cache value of Qcopy
				putCachedPoint(param, Qcopy);
			} else {
				// use cached result to set Qcopy
				Qcopy.setCoords(cachedPoint.getX(), cachedPoint.getY(), 1.0);
			}
		}

		// if (Qcopy.isDefined() && !Qcopy.isInfinite()) {
		// if (!foundDefined)
		// System.out.print(locus.label + " FIRST DEFINED param: " +
		// Pcopy.getPathParameter().t);
		// else
		// System.out.print(locus.label + " param: " +
		// Pcopy.getPathParameter().t);
		// System.out.println(", Qcopy: " + Qcopy);
		// } else {
		// System.out.print(locus.label + " param: " +
		// Pcopy.getPathParameter().t);
		// System.out.println(", Qcopy: NOT DEFINED");
		// }

		// check found defined
		if (!foundDefined && Qcopy.isDefined() && !Qcopy.isInfinite()) {
			pathMover.init(Pcopy, MIN_STEPS_INSTANCE);
			((GeoElement) PstartPos).set((GeoElement) Pcopy);
			((GeoElement) QstartPos).set((GeoElement) Qcopy);
			foundDefined = true;

			// insert first point
			insertPoint(Qcopy, false);
		}
	}

	private void clearCache() {
		for (int i = 0; i < paramCache.length; i++) {
			paramCache[i] = Double.NaN;
			if (qcopyCache[i] == null)
				qcopyCache[i] = newCache();
		}
	}

	private GPoint2D getCachedPoint(double param) {
		// search for cached parameter
		for (int i = 0; i < paramCache.length; i++) {
			if (param == paramCache[i])
				return qcopyCache[i];
		}

		return null;
	}

	private void putCachedPoint(double param, GeoPointND Qcopy) {
		cacheIndex++;
		if (cacheIndex >= paramCache.length)
			cacheIndex = 0;

		paramCache[cacheIndex] = param;
		setQCopyCache(qcopyCache[cacheIndex], Qcopy);
	}

	// small cache of 3 last parameters and Qcopy positions
	protected double[] paramCache = new double[3];
	private T[] qcopyCache = createQCopyCache();
	private int cacheIndex = 0;

	/**
	 * 
	 * @return new q copy cache
	 */
	abstract protected T[] createQCopyCache();

	/**
	 * set point's coords to copy
	 * 
	 * @param copy
	 *            copy
	 * @param point
	 *            point
	 */
	abstract protected void setQCopyCache(T copy, GeoPointND point);

	/**
	 * 
	 * @return new instance for cache
	 */
	abstract protected T newCache();

	/**
	 * insert point
	 * 
	 * @param point
	 *            point
	 * @param lineTo
	 *            if line to
	 */
	abstract protected void insertPoint(GeoPointND point, boolean lineTo);

	/**
	 * 
	 * @param point
	 *            point
	 * @return if the point is far away
	 */
	abstract protected boolean isFarAway(GeoPointND point, int i);

	/**
	 * 
	 * @param Q
	 *            point
	 * @param rectangle
	 *            rectangle
	 * @return if distance ok for the point in this rectangle
	 */
	abstract protected boolean distanceOK(GeoPointND Q, GRectangle2D rectangle);

	private boolean distanceOK(GeoPointND Q) {
		boolean[] distanceOK = { false, false, false };

		for (int i = 0; i < distanceOK.length; i++) {
			if (lastFarAway[i] && isFarAway(Q, i)) {
				distanceOK[i] = distanceOK(Q, nearToScreenRect[i]);
			} else {
				distanceOK[i] = distanceSmall(Q, false);
			}
		}

		for (int i = 0; i < distanceOK.length; i++) {
			if (!distanceOK[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 
	 * @param Q
	 *            point
	 * @param orInsteadOfAnd
	 * @return true if distance is small to last point
	 */
	abstract protected boolean distanceSmall(GeoPointND Q,
			boolean orInsteadOfAnd);

	protected boolean[] visibleEV = { false, false, false };

	boolean isVisibleInEV(int i) {
		switch (i) {
		case 1:
			if (!locus.isVisibleInView(App.VIEW_EUCLIDIAN))
				return false;
			if (!kernel.getApplication().getEuclidianView1().isShowing())
				return false;
			return true;

		case 2:
			if (!locus.isVisibleInView(App.VIEW_EUCLIDIAN2))
				return false;
			if (!kernel.getApplication().hasEuclidianView2(1))
				return false;
			return true;

		case 3:
			if (!locus.isVisibleInView3D())
				return false;
			if (kernel.getApplication().isEuclidianView3Dinited())
				return kernel.getApplication().getEuclidianView3D().isShowing();
		}
		return false;

	}

	void updateScreenBordersIfNecessary() {
		for (int i = 0; i < visibleEV.length; i++) {
			if (isVisibleInEV(i + 1) != visibleEV[i]) {
				updateScreenBorders();
				return;
			}
		}
	}

	boolean updateScreenBorders(int i) {
		boolean changed = xmax[i] != kernel.getXmax(i);
		xmax[i] = kernel.getXmax(i);
		changed = changed || xmin[i] != kernel.getXmin(i);
		xmin[i] = kernel.getXmin(i);
		changed = changed || ymax[i] != kernel.getYmax(i);
		ymax[i] = kernel.getYmax(i);
		changed = changed || ymin[i] != kernel.getYmin(i);
		ymin[i] = kernel.getYmin(i);

		setMaxDistances(i);

		// near to screen rectangle
		nearToScreenRect[i].setFrame(farXmin[i], farYmin[i], farXmax[i]
				- farXmin[i], farYmax[i] - farYmin[i]);
		return changed;
		// App.debug(viewIndex+" -- "+xmin[i]+","+ymin[i]+" -- "+xmax[i]+","+ymax[i]);
	}

	protected void setMaxDistances(int i) {
		maxXdist[i] = MAX_X_PIXEL_DIST / kernel.getXscale(i); // widthRW / 100;
		maxYdist[i] = MAX_Y_PIXEL_DIST / kernel.getYscale(i); // heightRW / 100;

		// we take a bit more than the screen
		// itself so that we don't loose locus
		// lines too often
		// that leave and reenter the screen
		double widthRW = xmax[i] - xmin[i];
		double heightRW = ymax[i] - ymin[i];

		farXmin[i] = xmin[i] - widthRW / 2;
		farXmax[i] = xmax[i] + widthRW / 2;
		farYmin[i] = ymin[i] - heightRW / 2;
		farYmax[i] = ymax[i] + heightRW / 2;

	}

	boolean updateScreenBorders() {

		for (int i = 0; i < visibleEV.length; i++) {
			visibleEV[i] = isVisibleInEV(i + 1);
		}

		if (visibleEV[0] && visibleEV[1]) {
			views = 2;
		} else {
			views = 1;
		}

		if (visibleEV[2]) {
			views++;
		}
		boolean changed = false;
		for (int i = 0; i < visibleEV.length; i++) {
			if (visibleEV[i]) {
				changed = changed || updateScreenBorders(i);
			}
		}
		return changed;

	}

	@Override
	public boolean euclidianViewUpdate() {
		boolean changed = updateScreenBorders();
		if (changed || !locus.getAlgoUpdateSet().isEmpty()) {
			update();
		}
		return false;
	}

	// TODO Consider locusequability

}
