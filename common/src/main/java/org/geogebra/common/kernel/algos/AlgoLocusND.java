/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPoint3DInRegion;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPoint3DOnPath;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.MacroKernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusND;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;

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
	public static final int MAX_TIME_FOR_ONE_STEP = 500;

	private int minStepsInstance = PathMover.MIN_STEPS;

	protected static final int MAX_X_PIXEL_DIST = 5;
	private static int MAX_Y_PIXEL_DIST = 5;

	protected GeoPointND movingPoint; // input
	protected GeoPointND locusPoint; // input
	protected GeoLocusND<T> locus; // output

	// for efficient dependency handling
	private GeoElement[] efficientInput;
	private GeoElement[] standardInput;

	private Path path; // path of P
	private PathMover pathMover;
	protected int pointCount;

	// copies of P and Q in a macro kernel
	protected GeoPointND copyP;
	protected GeoPointND copyQ;
	protected GeoPointND startPPos;
	protected GeoPointND startQPos;
	protected double lastX;
	protected double lastY;

	protected double[] maxXdist;
	protected double[] maxYdist;
	protected double[] xmin = new double[3];
	protected double[] xmax = new double[3];
	protected double[] ymin = new double[3];
	protected double[] ymax = new double[3];
	protected double[] farXmin = new double[3];
	protected double[] farXmax = new double[3];
	protected double[] farYmin = new double[3];
	protected double[] farYmax = new double[3];

	// private Line2D.Double tempLine = new Line2D.Double();

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
	protected boolean[] visibleEV = { false, false, false };

	// small cache of 3 last parameters and Qcopy positions
	private double[] paramCache = new double[3];
	private T[] qcopyCache = createQCopyCache(3);
	private int cacheIndex = 0;

	// private Updater updater;

	// Constructor called from AlgoLocusList
	/**
	 * @param cons
	 *            construction
	 * @param Q
	 *            locus point
	 * @param P
	 *            moving point
	 * @param min_steps
	 *            number of steps
	 * @param registerCE
	 *            whether to listen to zooming
	 */
	public AlgoLocusND(Construction cons, GeoPointND Q, GeoPointND P,
			int min_steps, boolean registerCE) {
		super(cons, registerCE);

		createMaxDistances();

		minStepsInstance = min_steps;
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
	 * @param cons1
	 *            construction
	 */
	abstract protected void createStartPos(Construction cons1);

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @return new GeoLocus
	 */
	abstract protected GeoLocusND<T> newGeoLocus(Construction cons1);

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
	// System.out.println(" " + geo);
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
		locusConsOrigElements = new TreeSet<>();
		TreeSet<Long> usedAlgoIds = new TreeSet<>();
		Iterator<GeoElement> it = Qin.iterator();
		while (it.hasNext()) {
			GeoElement parent = it.next();
			if (parent.isLabelSet()
					&& parent.isChildOf(movingPoint)) {
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
			((GeoElement) movingPoint).setLabelSimple(
					((GeoElement) movingPoint).getDefaultLabel());
			((GeoElement) movingPoint).setLabelSet(true);
		}
		boolean isLabeledQ = locusPoint.isLabelSet();
		if (!isLabeledQ) {
			((GeoElement) locusPoint).setLabelSimple(
					((GeoElement) locusPoint).getDefaultLabel());
			((GeoElement) locusPoint).setLabelSet(true);
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
		Macro.addDependentAlgo(locusPoint.getParentAlgorithm(),
				locusConsOrigElements, usedAlgoIds);

		// create macro construction
		buildLocusMacroConstruction(locusConsOrigElements);

		// if we used temp labels remove them again
		if (!isLabeledP) {
			((GeoElement) movingPoint).setLabelSet(false);
		}
		if (!isLabeledQ) {
			((GeoElement) locusPoint).setLabelSet(false);
		}
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
		Iterator<GeoElement> it = Qin.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo.isPointerChangeable()) {
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
				macroKernel.addReservedLabel(
						geo.getLabel(StringTemplate.defaultTemplate));
			}
		}

		try {
			// get XML for macro construction of P -> Q
			String locusConsXML = Macro.buildMacroXML(kernel, locusConsElements)
					.toString();
			macroKernel.loadXML(locusConsXML);

			// get the copies of P and Q from the macro kernel
			copyP = (GeoPointND) macroKernel
					.lookupLabel(((GeoElement) movingPoint).getLabelSimple());
			((GeoElement) copyP).setFixed(false);
			copyP.setPath(movingPoint.getPath());

			// alternative way to add the incidence of the path to Pcopy
			// see init()
			// Pcopy.addIncidence((GeoElement)path);
			// AlgoIntersectLineConic.resetPossibleSpecialCase();//not
			// implemented

			copyQ = (GeoPointND) macroKernel
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
					GeoElement geoCopy = macroCons
							.lookupLabel(geoOrig.getLabelSimple());
					if (geoCopy != null) {
						try {
							ExpressionNode en = geoCopy.getDefinition();
							geoCopy.set(geoOrig);
							geoCopy.setDefinition(en);
							geoCopy.update();
						} catch (Exception e) {
							Log.debug(
									"AlgoLocus: error in resetMacroConstruction(): "
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
				|| !isPathIterable(path.toGeoElement())
				|| !validLocus(locusPoint, movingPoint)) {
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
		int max_runs;

		// continuous kernel?
		continuous = kernel.isContinuous();
		macroKernel.setContinuous(continuous);

		// update macro construction with current values of global vars
		resetMacroConstruction();
		macroCons.updateConstruction(false);

		// lines: start from startpoint to avoid inf. problems.
		// Otherwise go from endpoint to endpoint
		if (!MyDouble.isFinite(path.getMinParameter())
				&& 0 < path.getMaxParameter()) {
			copyP.getPathParameter().setT(0);
		} else {
			copyP.getPathParameter().setT(path.getMinParameter());
		}
		pathMover.init(copyP, minStepsInstance);

		if (continuous) {
			// continous constructions may need several parameter run throughs
			// to draw all parts of the locus
			max_runs = GeoLocusND.MAX_PATH_RUNS;
		} else {
			max_runs = 1;
		}

		// update Pcopy to compute Qcopy
		pcopyUpdateCascade();
		boolean prevQcopyDefined = copyQ.isDefined() && !copyQ.isInfinite();

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
				boolean parameterJump = !pathMover.getNext(copyP);
				boolean stepChanged = false;

				// update construction
				pcopyUpdateCascade();

				// Qcopy DEFINED
				if (copyQ.isDefined() && !copyQ.isInfinite()) {
					// STANDARD CASE: no parameter jump
					if (!parameterJump) {
						// make steps smaller until distance ok to connect with
						// last point
						while (copyQ.isDefined() && !copyQ.isInfinite()
								&& !distanceOK(copyQ) && !maxTimeExceeded) {
							// go back and try smaller step
							boolean smallerStep = pathMover.smallerStep();
							if (!smallerStep) {
								break;
							}

							stepChanged = true;
							pathMover.stepBack();
							pathMover.getNext(copyP);

							// update construction
							pcopyUpdateCascade();
						}

						if (copyQ.isDefined() && !copyQ.isInfinite()) {
							// draw point
							insertPoint(copyQ, distanceSmall(copyQ, true));
							prevQcopyDefined = true;
						}
					}

					// PARAMETER jump: !lineTo
					else {
						// draw point
						insertPoint(copyQ, distanceSmall(copyQ, true));
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
						} else {
							stepChanged = true;
						}
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
					if (distanceSmall(startQPos, true)) {
						// draw line back to first point when it's close enough
						insertPoint(startQPos, true);
						finishedRun = true;
					} else {
						// decrease step until another step is possible
						boolean check = true;
						while (check) {
							check = !pathMover.hasNext()
									&& pathMover.smallerStep();
						}
						// no smaller step possible: run finished
						if (!pathMover.hasNext()) {
							finishedRun = true;
						}
					}
				}
			}

			// calculating the steps took too long, so we stopped somewhere
			if (maxTimeExceeded) {
				Log.error("AlgoLocus: max time exceeded");
				return;
			}
			// make sure that Pcopy is back at startPos now
			// look at Qcopy at startPos
			((GeoElement) copyP).set(startPPos);
			pcopyUpdateCascade();
			if (differentFromLast(copyQ)) {
				insertPoint(copyQ, distanceSmall(copyQ, true));
			}

			// Application.debug("run: " + runs);
			// Application.debug("pointCount: " + pointCount);
			// Application.debug(" startPos: " + QstartPos);
			// Application.debug(" Qcopy: " + Qcopy);

			// we are finished with all runs
			// if we got back to the start position of Qcopy
			// AND if the direction of moving along the path
			// is positive like in the beginning
			if (pathMover.hasPositiveOrientation()) {
				boolean equal = areEqual(startQPos, copyQ);
				if (equal) {
					break;
				}
			}

			pathMover.resetStartParameter();
			runs++;
		} while (runs < max_runs);

		// set defined/undefined
		locus.setDefined(foundDefined);

		// System.out.println(" first point: " +
		// locus.getMyPointList().get(0));
		// ArrayList list = locus.getMyPointList();
		// for (int i=list.size()-10; i < list.size()-1; i++) {
		// System.out.println(" point: " + list.get(i));
		// }
		// System.out.println(" last point: " +
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
		if (geoElement.isGeoImplicitCurve()) {
			return ((GeoImplicit) geoElement).isOnScreen();
		}
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
			copyP.updateCascade();
		} else {
			// NON-CONTINOUS construction
			// check if the path parameter's resulting Qcopy is already in cache
			double param = copyP.getPathParameter().t;
			GPoint2D cachedPoint = getCachedPoint(param);

			if (cachedPoint == null) {
				// measure time needed for update of construction
				long startTime = System.currentTimeMillis();

				// result not in cache: update Pcopy to compute Qcopy
				copyP.updateCascade();

				long updateTime = System.currentTimeMillis() - startTime;

				// if it takes too much time to calculate a single step, we stop
				if (updateTime > MAX_TIME_FOR_ONE_STEP) {
					Log.debug("AlgoLocus: max time exceeded " + updateTime);
					maxTimeExceeded = true;
				}

				// cache value of Qcopy
				putCachedPoint(param, copyQ);
			} else {
				// use cached result to set Qcopy
				ExpressionNode qDef = copyQ.getDefinition();
				copyQ.setCoords(cachedPoint.getX(), cachedPoint.getY(), 1.0);
				copyQ.setDefinition(qDef);
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
		if (!foundDefined && copyQ.isDefined() && !copyQ.isInfinite()) {
			pathMover.init(copyP, minStepsInstance);
			((GeoElement) startPPos).set(copyP);
			((GeoElement) startQPos).set(copyQ);
			foundDefined = true;

			// insert first point
			insertPoint(copyQ, false);
		}
	}

	private void clearCache() {
		for (int i = 0; i < paramCache.length; i++) {
			paramCache[i] = Double.NaN;
			if (qcopyCache[i] == null) {
				qcopyCache[i] = newCache();
			}
		}
	}

	private GPoint2D getCachedPoint(double param) {
		// search for cached parameter
		for (int i = 0; i < paramCache.length; i++) {
			if (param == paramCache[i]) {
				return qcopyCache[i];
			}
		}

		return null;
	}

	private void putCachedPoint(double param, GeoPointND Qcopy0) {
		cacheIndex++;
		if (cacheIndex >= paramCache.length) {
			cacheIndex = 0;
		}

		paramCache[cacheIndex] = param;
		setQCopyCache(qcopyCache[cacheIndex], Qcopy0);
	}

	/**
	 * 
	 * @param length
	 *            number of elements
	 * @return new q copy cache
	 */
	abstract protected T[] createQCopyCache(int length);

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
	 * @param i
	 *            view index
	 * @return if distance ok for the point in this rectangle
	 */
	abstract protected boolean distanceOK(GeoPointND Q, int i);

	private boolean distanceOK(GeoPointND Q) {
		boolean[] distanceOK = { false, false, false };

		for (int i = 0; i < distanceOK.length; i++) {
			if (lastFarAway[i] && isFarAway(Q, i)) {
				distanceOK[i] = distanceOK(Q, i);
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
	 *            check that at least one coord is close?
	 * @return true if distance is small to last point
	 */
	abstract protected boolean distanceSmall(GeoPointND Q,
			boolean orInsteadOfAnd);

	void updateScreenBordersIfNecessary() {
		for (int i = 0; i < visibleEV.length; i++) {
			if (locus.isVisibleInEV(i + 1) != visibleEV[i]) {
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
		return changed;
		// Log.debug(viewIndex+" -- "+xmin[i]+","+ymin[i]+" --
		// "+xmax[i]+","+ymax[i]);
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
			visibleEV[i] = locus.isVisibleInEV(i + 1);
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
		for (int i = 0; i < visibleEV.length
				&& i < kernel.getXmaxLength(); i++) {
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

	/**
	 * Decide if the locus definition is valid in the sense that there is no
	 * ad-hoc definition of a point on a path/region between the locus point and
	 * the moving point on the dependency graph of the construction.
	 * 
	 * @param locusPoint
	 *            locus point
	 * @param movingPoint
	 *            moving point
	 * @return whether the points can be used for locus
	 */
	public static boolean validLocus(GeoPointND locusPoint,
			GeoPointND movingPoint) {
		HashSet<GeoElement> mPChildren = new HashSet<>();
		mPChildren.addAll(movingPoint.getAllChildren());
		HashSet<GeoElement> lPParents = new HashSet<>();
		lPParents.addAll(((GeoElement) locusPoint).getAllPredecessors());
		mPChildren.retainAll(lPParents);
		Log.debug("Elements between mover and tracer: " + mPChildren);
		for (GeoElement ge : mPChildren) {
			AlgoElement ae = ge.getParentAlgorithm();
			if (ae != null && (ae instanceof AlgoPointOnPath
					|| ae instanceof AlgoPoint3DOnPath
					|| ae instanceof AlgoPointInRegion
					|| ae instanceof AlgoPoint3DInRegion)) {
				Log.debug("Element " + ge
						+ " is defined ad-hoc by GeoGebra, no valid locus can be generated");
				return false;
			}
		}
		return true;
	}

}
