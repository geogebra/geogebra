/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.GRectangle2D;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.MacroKernel;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.SliderMover;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * locus line for Q dependent on P where P is a slider
 */
public class AlgoLocusSlider extends AlgoElement implements AlgoLocusSliderInterface {

	// TODO: update locus algorithm
	// * locus of Q=(x(B), a) with a= integral[f(x), 0, x(B)] and B is point on
	// x-axis freezes GeoGebra
	// MAX_TIME handling does not solve this yet
	//

	/** maximum time for the computation of one locus point in millis **/
	public static int MAX_TIME_FOR_ONE_STEP = 500;

	private static int MAX_X_PIXEL_DIST = 5;
	private static int MAX_Y_PIXEL_DIST = 5;

	private GeoPoint locusPoint; // input (Q)
	private GeoNumeric movingSlider; // input (P)
	private GeoLocus locus; // output

	// for efficient dependency handling
	private GeoElement[] efficientInput, standardInput;

	private SliderMover sliderMover;
	private int pointCount;

	// copies of P and Q in a macro kernel
	private GeoPoint Qcopy, QstartPos;
	private GeoNumeric Pcopy, PstartPos;
	private double lastX, lastY, maxXdist, maxYdist, xmin, xmax, ymin, ymax,
			farXmin, farXmax, farYmin, farYmax;
	// private Line2D.Double tempLine = new Line2D.Double();
	private GRectangle2D nearToScreenRect = geogebra.common.factories.AwtFactory.prototype
			.newRectangle2D();
	private boolean continuous;
	private boolean lastFarAway;
	private boolean foundDefined;
	private boolean maxTimeExceeded;
	private Construction macroCons;
	private MacroKernel macroKernel;
	// private AlgorithmSet macroConsAlgoSet;
	// list with all original elements used for the macro construction
	private TreeSet<ConstructionElement> locusConsOrigElements;
	private TreeSet<GeoElement> Qin;

	private long countUpdates = 0;

	// private Updater updater;

	public AlgoLocusSlider(Construction cons, String label, GeoPoint Q,
			GeoNumeric P) {
		super(cons);
		this.movingSlider = P;
		this.locusPoint = Q;

		sliderMover = new SliderMover(P);

		QstartPos = new GeoPoint(cons);
		PstartPos = new GeoNumeric(cons);

		init();
		updateScreenBorders();
		locus = new GeoLocus(cons);
		setInputOutput(); // for AlgoElement
		cons.registerEuclidianViewCE(this);
		compute();

		// we may have created a starting point for the path now
		// make sure that the movingPoint in the main construction
		// uses the correct path parameter for it
		// path.pointChanged(P);

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
	public Algos getClassName() {
		return Algos.AlgoLocusSlider;
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

	private void init() {
		// copy the construction
		Qin = locusPoint.getAllPredecessors(); // all parents of Q

		// get intersection of all children of P and all parents of Q
		locusConsOrigElements = new TreeSet<ConstructionElement>();
		TreeSet<Long> usedAlgoIds = new TreeSet<Long>();
		Iterator<GeoElement> it = Qin.iterator();
		while (it.hasNext()) {
			GeoElement parent = it.next();

			if (parent.isLabelSet() && parent.isChildOf(movingSlider)) {
				// note: locusConsOrigElements will contain AlgoElement and
				// GeoElement objects
				Macro.addDependentElement(parent, locusConsOrigElements,
						usedAlgoIds);
			}
		}

		// ensure that P and Q have labels set
		// Note: we have to undo this at the end of this method !!!
		boolean isLabeledP = movingSlider.isLabelSet();
		if (!isLabeledP) {
			movingSlider.setLabelSimple(movingSlider.getDefaultLabel());
			movingSlider.labelSet = true;
		}
		boolean isLabeledQ = locusPoint.isLabelSet();
		if (!isLabeledQ) {
			locusPoint.setLabelSimple(locusPoint.getDefaultLabel());
			locusPoint.labelSet = true;
		}

		// add moving point on line
		locusConsOrigElements.add(movingSlider);

		// add locus creating point and its algorithm to locusConsOrigElements
		Macro.addDependentElement(locusPoint, locusConsOrigElements,
				usedAlgoIds);

		// create macro construction
		buildLocusMacroConstruction(locusConsOrigElements);

		// if we used temp labels remove them again
		if (!isLabeledP)
			movingSlider.labelSet = false;
		if (!isLabeledQ)
			locusPoint.labelSet = false;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		// it is inefficient to have Q and P as input
		// let's take all independent parents of Q
		// and the path as input
		TreeSet<GeoElement> inSet = new TreeSet<GeoElement>();
		// inSet.add(movingSlider);

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
		// don't do this as we doesn't have anything else
		// inSet.remove(movingSlider);

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
		standardInput[1] = movingSlider;

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
				macroKernel.addReservedLabel(geo.getLabel(StringTemplate.defaultTemplate));
			}
		}

		try {
			// get XML for macro construction of P -> Q
			String locusConsXML = Macro
					.buildMacroXML(kernel, locusConsElements);

			macroKernel.loadXML(locusConsXML);

			// get the copies of P and Q from the macro kernel
			Pcopy = (GeoNumeric) macroKernel.lookupLabel(movingSlider.getLabelSimple());
			Pcopy.setFixed(false);
			// Pcopy.setPath(movingSlider.getPath());

			Qcopy = (GeoPoint) macroKernel.lookupLabel(locusPoint.getLabelSimple());
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
					GeoElement geoCopy = macroCons.lookupLabel(geoOrig.getLabelSimple());
					if (geoCopy != null) {
						try {
							geoCopy.set(geoOrig);
							geoCopy.update();
						} catch (Exception e) {
							App
									.debug("AlgoLocusSlider: error in resetMacroConstruction(): "
											+ e.getMessage());
						}
					}
				}
			}
		}
	}

	// compute locus line
	@Override
	final public void compute() {
		if (!movingSlider.isDefined() || !movingSlider.isSlider()
				|| !movingSlider.isAnimatable() || macroCons == null) {
			locus.setUndefined();
			return;
		}

		locus.clearPoints();
		clearCache();
		pointCount = 0;
		useCache = 0;
		countUpdates = 0;
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
		sliderMover.init(Pcopy);

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
		int MAX_LOOPS = 2 * PathMover.MAX_POINTS;
		int whileLoops = 0;

		do {
			boolean finishedRun = false;
			while (!finishedRun && !maxTimeExceeded
					&& pointCount <= PathMover.MAX_POINTS
					&& whileLoops <= MAX_LOOPS) {
				whileLoops++;

				// lineTo may be false due to a parameter jump
				// i.e. param in [0,1] gets bigger than 1 and thus jumps to 0
				boolean parameterJump = !sliderMover.getNext(Pcopy);
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
							boolean smallerStep = sliderMover.smallerStep();
							if (!smallerStep)
								break;

							stepChanged = true;
							sliderMover.stepBack();
							sliderMover.getNext(Pcopy);

							// update construction
							pcopyUpdateCascade();
						}

						if (Qcopy.isDefined() && !Qcopy.isInfinite()) {
							// draw point
							insertPoint(Qcopy.inhomX, Qcopy.inhomY,
									distanceSmall(Qcopy));
							prevQcopyDefined = true;
						}
					}

					// PARAMETER jump: !lineTo
					else {
						// draw point
						insertPoint(Qcopy.inhomX, Qcopy.inhomY,
								distanceSmall(Qcopy));
						prevQcopyDefined = true;
					}
				}

				// Qcopy NOT DEFINED
				else {
					// check if we moved from defined to undefined case:
					// step back and try with smaller step
					if (prevQcopyDefined && !parameterJump) {
						sliderMover.stepBack();
						// set smallest step
						if (!sliderMover.smallerStep()) {
							prevQcopyDefined = false;
						} else
							stepChanged = true;
					}

					// add better undefined case support for continuous curves
					// maybe change orientation of path mover
				}

				// if we didn't decrease the step width increase it
				if (!stepChanged) {
					sliderMover.biggerStep();
				}

				// end of run: the next step would pass the start position
				if (!sliderMover.hasNext()) {
					if (distanceSmall(QstartPos)) {
						// draw line back to first point when it's close enough
						insertPoint(QstartPos.inhomX, QstartPos.inhomY, true);
						finishedRun = true;
					} else {
						// decrease step until another step is possible
						while (!sliderMover.hasNext()
								&& sliderMover.smallerStep()){
							// do nothing
						}
							

						// no smaller step possible: run finished
						if (!sliderMover.hasNext())
							finishedRun = true;
					}
				}
			}

			// calculating the steps took too long, so we stopped somewhere
			if (maxTimeExceeded) {
				System.err.println("AlgoLocusSlider: max time exceeded");
				return;
			}

			// make sure that Pcopy is back at startPos now
			// look at Qcopy at startPos
			Pcopy.set(PstartPos);
			pcopyUpdateCascade();
			if (Qcopy.inhomX != lastX || Qcopy.inhomY != lastY)
				insertPoint(Qcopy.inhomX, Qcopy.inhomY, distanceSmall(Qcopy));

			// Application.debug("run: " + runs);
			// Application.debug("pointCount: " + pointCount);
			// Application.debug("  startPos: " + QstartPos);
			// Application.debug("  Qcopy: " + Qcopy);

			// we are finished with all runs
			// if we got back to the start position of Qcopy
			// AND if the direction of moving along the path
			// is positive like in the beginning
			if (sliderMover.hasPositiveOrientation()) {
				Kernel.setMinPrecision();
				boolean equal = QstartPos.isEqual(Qcopy);
				Kernel.resetPrecision();
				if (equal)
					break;
			}

			sliderMover.resetStartParameter();
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
	 * Calls Pcopy.updateCascade() to compute Qcopy. For non-continous
	 * constructions caching of previous paramater positions is used.
	 */
	private void pcopyUpdateCascade() {
		countUpdates++;

		if (continuous) {
			// CONTINOUS construction
			// don't use caching for continuous constructions:
			// the same position of Pcopy can have different results for Qcopy
			Pcopy.updateCascade();
		} else {
			// NON-CONTINOUS construction
			// check if the path parameter's resulting Qcopy is already in cache
			double param = Pcopy.getValue();
			GPoint2D cachedPoint = getCachedPoint(param);

			if (cachedPoint == null) {
				// measure time needed for update of construction
				long startTime = System.currentTimeMillis();

				// result not in cache: update Pcopy to compute Qcopy
				Pcopy.updateCascade();

				long updateTime = System.currentTimeMillis() - startTime;

				// if it takes too much time to calculate a single step, we stop
				if (updateTime > MAX_TIME_FOR_ONE_STEP) {
					App
							.debug("AlgoLocusSlider: max time exceeded "
									+ updateTime);
					maxTimeExceeded = true;
				}

				// cache value of Qcopy
				putCachedPoint(param, Qcopy);
			} else {
				// use cached result to set Qcopy
				Qcopy.setCoords(cachedPoint.getX(), cachedPoint.getY(), 1.0);
				useCache++;
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
			sliderMover.init(Pcopy);
			PstartPos.set(Pcopy);
			QstartPos.set((GeoElement) Qcopy);
			foundDefined = true;

			// insert first point
			insertPoint(Qcopy.inhomX, Qcopy.inhomY, false);
		}
	}

	private void clearCache() {
		for (int i = 0; i < paramCache.length; i++) {
			paramCache[i] = Double.NaN;
			if (qcopyCache[i] == null)
				qcopyCache[i] = geogebra.common.factories.AwtFactory.prototype
						.newPoint2D();
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

	private void putCachedPoint(double param, GeoPoint Qcopy) {
		cacheIndex++;
		if (cacheIndex >= paramCache.length)
			cacheIndex = 0;

		paramCache[cacheIndex] = param;
		qcopyCache[cacheIndex].setX(Qcopy.inhomX);
		qcopyCache[cacheIndex].setY(Qcopy.inhomY);
	}

	// small cache of 3 last parameters and Qcopy positions
	private double[] paramCache = new double[3];
	private GPoint2D[] qcopyCache = new GPoint2D[paramCache.length];
	private int cacheIndex = 0;
	private long useCache = 0;

	private void insertPoint(double x, double y, boolean lineTo) {
		pointCount++;

		// Application.debug("insertPoint: " + x + ", " + y + ", lineto: " +
		// lineTo);
		locus.insertPoint(x, y, lineTo);
		lastX = x;
		lastY = y;
		lastFarAway = isFarAway(lastX, lastY);
	}

	private boolean isFarAway(double x, double y) {
		boolean farAway = (x > farXmax || x < farXmin || y > farYmax || y < farYmin);
		return farAway;
	}

	private boolean distanceOK(GeoPoint Q) {
		boolean distanceOK;

		if (lastFarAway && isFarAway(Q.inhomX, Q.inhomY)) {
			// if last point Q' was far away and Q is far away
			// then the distance is probably OK (return true),
			// so we probably don't need smaller step,
			// except if the rectangle of the segment Q'Q
			// intersects the near to screen rectangle
			// (it will probably not be on the screen anyway)
			double minX = lastX;
			double minY = lastY;
			double lengthX = Q.inhomX - lastX;
			double lengthY = Q.inhomY - lastY;
			if (Q.inhomX < minX)
				minX = Q.inhomX;
			if (Q.inhomY < minY)
				minY = Q.inhomY;
			if (lengthX < 0)
				lengthX = -lengthX;
			if (lengthY < 0)
				lengthY = -lengthY;
			distanceOK = !nearToScreenRect.intersects(minX, minY, lengthX,
					lengthY);
		} else {
			distanceOK = distanceSmall(Q);
		}

		return distanceOK;
	}

	private boolean distanceSmall(GeoPoint Q) {
		boolean distSmall = Math.abs(Q.inhomX - lastX) < maxXdist
				&& Math.abs(Q.inhomY - lastY) < maxYdist;
		return distSmall;
	}

	private void updateScreenBorders() {
		xmax = kernel.getXmax();
		xmin = kernel.getXmin();
		ymax = kernel.getYmax();
		ymin = kernel.getYmin();

		double widthRW = xmax - xmin;
		double heightRW = ymax - ymin;
		maxXdist = MAX_X_PIXEL_DIST / kernel.getXscale(); // widthRW / 100;
		maxYdist = MAX_Y_PIXEL_DIST / kernel.getYscale(); // heightRW / 100;

		// we take a bit more than the screen
		// itself so that we don't loose locus
		// lines too often
		// that leave and reenter the screen

		farXmin = xmin - widthRW / 2;
		farXmax = xmax + widthRW / 2;
		farYmin = ymin - heightRW / 2;
		farYmax = ymax + heightRW / 2;

		// near to screen rectangle
		nearToScreenRect.setFrame(farXmin, farYmin, farXmax - farXmin, farYmax
				- farYmin);
	}

	@Override
	public boolean euclidianViewUpdate() {
		updateScreenBorders();
		update();
		return false;
	}

	// TODO Consider locusequability

}
