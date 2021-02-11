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
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.FixedPathRegionAlgo;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.util.debug.Log;

/**
 * Algorithm to invoke a specific macro.
 * 
 * @author Markus
 */
public class AlgoMacro extends AlgoElement
		implements AlgoMacroInterface, FixedPathRegionAlgo {

	private Macro macro;

	// macro construction, its input and output used by this algo
	private GeoElement[] macroInput;
	private GeoElement[] macroOutput;

	// maps macro geos to algo geos
	private HashMap<GeoElementND, GeoElement> macroToAlgoMap;

	// all keys of macroToAlgoMap that are not part of macroInput
	private ArrayList<GeoElementND> macroOutputAndReferencedGeos;
	private ArrayList<GeoElementND> algoOutputAndReferencedGeos; // for
																	// efficiency,
																	// see
																	// getMacroConstructionState()

	private boolean locked;

	/**
	 * Creates a new algorithm that applies a macro to the given input objects.
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param macro
	 *            macro
	 * @param input
	 *            input objects
	 * @param add
	 *            add to construction?
	 */
	public AlgoMacro(Construction cons, String[] labels, Macro macro,
			GeoElement[] input, boolean add) {
		super(cons, add);

		this.input = input;
		this.macro = macro;

		this.macroInput = macro.getMacroInput();
		this.macroOutput = macro.getMacroOutput();

		// register algorithm with macro
		macro.registerAlgorithm(this);

		// create copies for the output objects
		createOutputObjects();

		// initialize the mapping between macro geos and algo geos
		initMap();

		setInputOutput();
		compute();

		// check if macro construction has euclidianAlgos
		if (macro.getMacroConstruction().hasEuclidianViewCE()) {
			cons.registerEuclidianViewCE(this);
		}

		if (add) {
			LabelManager.setLabels(labels, getOutput());
			// we hide objects that are hidden in macro construction, but
			// we want to do this only with 4.0 macros
			if (macro.isCopyCaptionsAndVisibility()) {
				for (int i = 0; i < macroOutput.length; i++) {
					if (!macroOutput[i].isSetEuclidianVisible()) {
						getOutput(i).setEuclidianVisible(false);
						getOutput(i).update();
					}
				}
			} else {
				// for <=3.2 macros hide all angles
				for (int i = 0; i < macroOutput.length; i++) {
					if (macroOutput[i] instanceof GeoAngle) {
						getOutput(i).setEuclidianVisible(false);
						getOutput(i).update();
					}
				}
			}
		}
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		macro.unregisterAlgorithm(this);
		super.remove();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoMacro;
	}

	@Override
	public String getDefinitionName(StringTemplate tpl) {
		return macro.getCommandName();
	}

	@Override
	protected void setInputOutput() {
		setDependencies();
	}

	/**
	 * The CopyPaste class needs a list of used macros to work perfectly
	 * 
	 * @return Macro macro
	 */
	public Macro getMacro() {
		return macro;
	}

	@Override
	final public void compute() {
		try {
			// set macro geos to algo geos state
			setMacroConstructionState();

			// update all algorithms of macro-construction
			macro.getMacroConstruction().updateAllAlgorithms();
			boolean pointsChanged = false;
			for (int i = 0; i < macroOutput.length; i++) {
				GeoElement geoPoint = macroOutput[i];

				if (geoPoint.isPointOnPath()) {
					GeoPointND P = (GeoPointND) getOutput(i);
					double t = P.getPathParameter().getT();
					Path path = ((GeoPointND) geoPoint).getPath();
					PathParameter pp = ((GeoPointND) geoPoint)
							.getPathParameter();
					// Application.debug(param.getDouble()+"
					// "+path.getMinParameter()+" "+path.getMaxParameter());
					pp.setT(t);
					// Application.debug(pp.t);

					path.pathChanged(P);
					P.updateCoords();
					pointsChanged = true;
				}
			}
			if (pointsChanged) {
				macro.getMacroConstruction().updateAllAlgorithms();
			}
			// set algo geos to macro geos state
			getMacroConstructionState();

		} catch (Exception e) {
			Log.debug("AlgoMacro compute():\n");
			this.locked = false;
			e.printStackTrace();
			for (int i = 0; i < getOutputLength(); i++) {
				getOutput(i).setUndefined();
			}
		}
	}

	/**
	 * Returns true when macroGeo is part of macroInput.
	 */
	private boolean isMacroInputObject(GeoElementND macroGeo) {
		for (int i = 0; i < macroInput.length; i++) {
			if (macroGeo == macroInput[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets macro geos to the current state of algo geos. Start points of
	 * vectors should not be copied.
	 */
	final void setMacroConstructionState() {
		// set input objects of macro construction
		for (int i = 0; i < macroInput.length; i++) {
			macroInput[i].set(input[i]);
			try {
				if (macroInput[i] instanceof GeoVector) {
					((GeoVector) macroInput[i]).setStartPoint(null);
				}
			} catch (Exception e) {
				Log.debug("Exception while handling vector input: " + e);
			}
			macroInput[i].setRealLabel(input[i].getLabelSimple());
			// Application.debug("SET INPUT object: " + input[i] + " => " +
			// macroInput[i]);
		}
	}

	/**
	 * Sets algo geos to the current state of macro geos.
	 */
	final void getMacroConstructionState() {
		this.locked = true;
		// for efficiency: instead of lookups in macroToAlgoMap
		// we use an array list algoOutputAndReferencedGeos with corresponding
		// macro and algo geos
		int size = macroOutputAndReferencedGeos.size();
		for (int i = 0; i < size; i++) {
			GeoElementND macroGeo = macroOutputAndReferencedGeos.get(i);
			GeoElementND algoGeo = algoOutputAndReferencedGeos.get(i);
			if (macroGeo.isDefined()) {
				algoGeo.set(macroGeo);
				AlgoElement drawAlgo = macroGeo.getParentAlgorithm();
				if (macro.isCopyCaptionsAndVisibility()) {
					algoGeo.setAdvancedVisualStyleCopy(macroGeo);
				}
				boolean oldVisible = algoGeo.isSetEuclidianVisible();
				if (drawAlgo instanceof DrawInformationAlgo) {
					((GeoNumeric) algoGeo).setDrawable(true, oldVisible);
					algoGeo.setDrawAlgorithm(
							((DrawInformationAlgo) drawAlgo).copy());
				}

			} else {
				algoGeo.setUndefined();
			}
		}
		this.locked = false;
	}

	/**
	 * Creates the output objects of this macro algorithm
	 */
	private void createOutputObjects() {
		setOutputLength(macroOutput.length);

		int layer = kernel.getApplication().getMaxLayerUsed();
		for (int i = 0; i < macroOutput.length; i++) {
			// copy output object of macro and make the copy part of this
			// construction
			setOutput(i, macroOutput[i].copyInternal(cons));
			GeoElement out = getOutput(i);
			out.setUseVisualDefaults(false);
			out.setVisualStyle(macroOutput[i]);

			// set layer
			out.setLayer(macroOutput[i].getLayer());
			out.setAdvancedVisualStyleCopy(macroOutput[i]);
			if (macro.isCopyCaptionsAndVisibility()) {
				out.setCaption(macroOutput[i].getRawCaption());
			}
			out.setLayer(layer);
			AlgoElement drawAlgo = macroOutput[i].getParentAlgorithm();
			if (drawAlgo instanceof DrawInformationAlgo) {
				((GeoNumeric) out).setDrawable(true);
				out.setDrawAlgorithm(((DrawInformationAlgo) drawAlgo).copy());
			}

			out.setAlgoMacroOutput(true);
		}
	}

	/**
	 * Inits the mapping of macro geos to algo geos construction. The map is
	 * used to set and get the state of the macro construction in compute() and
	 * to make sure that all output geos of the algorithm and all their
	 * references (e.g. the start point of a ray) are part of the algorithm's
	 * construction.
	 */
	private void initMap() {
		macroToAlgoMap = new HashMap<>();
		macroOutputAndReferencedGeos = new ArrayList<>();
		algoOutputAndReferencedGeos = new ArrayList<>();

		// INPUT initing
		// map macro input to algo input
		for (int i = 0; i < macroInput.length; i++) {
			map(macroInput[i], input[i]);
		}

		// OUTPUT initing
		// map macro output to algo output
		for (int i = 0; i < macroOutput.length; i++) {
			map(macroOutput[i], getOutput(i));
		}
		// SPECIAL REFERENCES of output
		// make sure all algo-output objects reference objects in their own
		// construction
		// note: we do this in an extra loop to make sure we don't create output
		// objects twice
		for (int i = 0; i < macroOutput.length; i++) {
			initSpecialReferences(macroOutput[i], getOutput(i));
		}
	}

	/**
	 * Adds a (macroGeo, algoGeo) pair to the map.
	 */
	private void map(GeoElementND macroGeo, GeoElement algoGeo) {
		if (macroToAlgoMap.get(macroGeo) == null) {
			// map macroGeo to algoGeo
			macroToAlgoMap.put(macroGeo, algoGeo);

			if (!isMacroInputObject(macroGeo)) {
				macroOutputAndReferencedGeos.add(macroGeo);
				// for efficiency: to avoid lookups in macroToAlgoMap
				algoOutputAndReferencedGeos.add(algoGeo);
			}
		}
	}

	/**
	 * Returns a GeoElement in this algo's construction that corresponds to the
	 * given macroGeo from the macro construction. If a macro-geo is not yet
	 * mapped to an algo-geo, a new algo-geo is created and added to the map
	 * automatically.
	 */
	private GeoElement getAlgoGeo(GeoElementND macroGeo) {
		if (macroGeo == null) {
			return null;
		}
		GeoElement algoGeo = macroToAlgoMap.get(macroGeo);

		// if we don't have a corresponding GeoElement in our map yet,
		// create a new geo and update the map
		if (algoGeo == null) {
			algoGeo = createAlgoCopy(macroGeo);
			map(macroGeo, algoGeo);
		}

		return algoGeo;
	}

	/**
	 * Creates a new algo-geo in this construction that is copy of macroGeo from
	 * the macro construction.
	 */
	private GeoElement createAlgoCopy(GeoElementND macroGeo) {
		GeoElement algoGeo = macroGeo.copyInternal(cons);
		return algoGeo;
	}

	/**
	 * Some GeoElement types need special settings as they reference other
	 * GeoElement objects. We need to make sure that algoGeo only reference
	 * objects in its own construction.
	 */
	private void initSpecialReferences(GeoElement macroGeo,
			GeoElement algoGeo) {

		switch (macroGeo.getGeoClassType()) {
		case FUNCTION:
			initFunction(((GeoFunction) algoGeo).getFunction());
			break;

		case LIST:
			initList((GeoList) macroGeo, (GeoList) algoGeo);
			break;

		case LINE:
			initLine((GeoLine) macroGeo, (GeoLine) algoGeo);
			break;

		case POLYGON:
			initPolygon((GeoPolygon) macroGeo, (GeoPolygon) algoGeo);
			break;

		case CONIC:
			initConic((GeoConic) macroGeo, (GeoConic) algoGeo);
			break;

		case TEXT:
		case VECTOR:
		case IMAGE:
			initLocateable((Locateable) macroGeo, (Locateable) algoGeo);
			break;

		default:
			// no special treatment necessary at the moment
			// case ANGLE:
			// case BOOLEAN:
			// case CONICPART:
			// case LOCUS:
			// case NUMERIC:
			// case POINT:
			// case AXIS:
			// case RAY:
			// case SEGMENT:
			// case POLYGON:
		}
	}

	/**
	 * Makes sure that the start and end point of a line are in its construction
	 * (if the line has this kind of information).
	 */
	private void initLine(GeoLine macroLine, GeoLine line) {
		GeoPoint startPoint = (GeoPoint) getAlgoGeo(macroLine.getStartPoint());
		GeoPoint endPoint = (GeoPoint) getAlgoGeo(macroLine.getEndPoint());
		line.setStartPoint(startPoint);
		line.setEndPoint(endPoint);
	}

	/**
	 * Makes sure that all points on conic are in its construction.
	 */
	private void initConic(GeoConic macroConic, GeoConic conic) {
		ArrayList<GeoPointND> macroPoints = macroConic.getPointsOnConic();
		if (macroPoints == null) {
			return;
		}

		int size = macroPoints.size();
		ArrayList<GeoPointND> points = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			points.add((GeoPointND) getAlgoGeo(macroPoints.get(i)));
		}
		conic.setPointsOnConic(points);
	}

	/**
	 * Makes sure that the start points of locateable are in its construction.
	 */
	private void initLocateable(Locateable macroLocateable,
			Locateable locateable) {
		GeoPointND[] macroStartPoints = macroLocateable.getStartPoints();
		if (macroStartPoints == null) {
			return;
		}

		try {
			for (int i = 0; i < macroStartPoints.length; i++) {
				GeoPointND point = (GeoPointND) getAlgoGeo(macroStartPoints[i]);
				locateable.initStartPoint(point, i);

				// Application.debug("set start point: " + locateable + " => " +
				// point + "(" + point.cons +")");

			}
		} catch (Exception e) {
			Log.debug("AlgoMacro.initLocateable:");
			e.printStackTrace();
		}
	}

	/**
	 * Makes sure that the points and segments of poly are in its construction.
	 */
	private void initPolygon(GeoPolygon macroPoly, GeoPolygon poly) {
		// points
		GeoPointND[] macroPolyPoints = macroPoly.getPoints();
		GeoPoint[] polyPoints = new GeoPoint[macroPolyPoints.length];
		for (int i = 0; i < macroPolyPoints.length; i++) {
			polyPoints[i] = (GeoPoint) getAlgoGeo(macroPolyPoints[i]);
		}
		poly.setPoints(polyPoints);

		// // segments
		// GeoSegment [] macroPolySegments = macroPoly.getSegments();
		// GeoSegment [] polySegments = new
		// GeoSegment[macroPolySegments.length];
		// for (int i=0; i < macroPolySegments.length; i++) {
		// polySegments[i] = (GeoSegment) getAlgoGeo( macroPolySegments[i] );
		// initLine(macroPolySegments[i], polySegments[i]);
		// }
		// poly.setSegments(polySegments);

	}

	/**
	 * Makes sure that all referenced GeoElements of geoList are in its
	 * construction.
	 * 
	 * @param macroList
	 *            GeoList of macro geos
	 * @param geoList
	 *            GeoList of construction geos
	 */
	@Override
	final public void initList(GeoList macroList, GeoList geoList) {
		// make sure all referenced GeoElements are from the algo-construction

		int size = macroList.size();
		geoList.clear();
		geoList.ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			geoList.add(getAlgoGeo(macroList.get(i)));
		}
	}

	/**
	 * Makes sure that all referenced GeoElements of fun are in this algorithm's
	 * construction.
	 * 
	 * @param fun
	 *            function
	 */
	@Override
	final public void initFunction(FunctionNVar fun) {
		// geoFun was created as a copy of macroFun,
		// make sure all referenced GeoElements are from the algo-construction
		replaceReferencedMacroObjects(fun.getExpression());
	}

	/**
	 * Replaces all references to macroGeos in expression exp by references to
	 * the corresponding algoGeos
	 */
	private void replaceReferencedMacroObjects(ExpressionNode exp) {
		ExpressionValue left = exp.getLeft();
		ExpressionValue right = exp.getRight();

		// left tree
		if (left.isGeoElement()) {
			GeoElement referencedGeo = (GeoElement) left;
			if (macro.isInMacroConstruction(referencedGeo)) {
				exp.setLeft(getAlgoGeo(referencedGeo));
			}
		} else if (left.isExpressionNode()) {
			replaceReferencedMacroObjects((ExpressionNode) left);
		}

		// right tree
		if (right == null) {
			return;
		} else if (right.isGeoElement()) {
			GeoElement referencedGeo = (GeoElement) right;
			if (macro.isInMacroConstruction(referencedGeo)) {
				exp.setRight(getAlgoGeo(referencedGeo));
			}
		} else if (right.isExpressionNode()) {
			replaceReferencedMacroObjects((ExpressionNode) right);
		}
	}

	@Override
	public int drawBefore(GeoElement geoElement, GeoElement other) {
		int myIndex = 0, otherIndex = 0;
		for (int i = 0; i < this.getOutputLength(); i++) {
			if (this.algoOutputAndReferencedGeos.get(i) == geoElement) {
				myIndex = this.macroOutputAndReferencedGeos.get(i)
						.getConstructionIndex();
			}
			if (this.algoOutputAndReferencedGeos.get(i) == other) {
				otherIndex = this.macroOutputAndReferencedGeos.get(i)
						.getConstructionIndex();
			}
		}
		return myIndex - otherIndex;
	}

	@Override
	public boolean isChangeable(GeoElement out) {
		for (int i = 0; i < macroOutput.length; i++) {
			if (getOutput(i) == out && macroOutput[i].isPointerChangeable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param geoPoint
	 *            parent construction point to be moved
	 * @param x
	 *            desired homogeneous x-coord
	 * @param y
	 *            desired homogeneous y-coord
	 * @param z
	 *            desired homogeneous z-coord
	 */
	public void setCoords(GeoPoint geoPoint, double x, double y, double z) {
		if (!isChangeable(geoPoint)) {
			return;
		}
		if (this.locked) {
			geoPoint.setCoords2D(x, y, z);
			geoPoint.updateCoords();
			return;
		}
		setMacroConstructionState();

		// update all algorithms of macro-construction
		macro.getMacroConstruction().updateAllAlgorithms();

		// set algo geos to macro geos state
		//
		for (Entry<GeoElementND, GeoElement> entry : macroToAlgoMap
				.entrySet()) {
			GeoElementND me = entry.getKey();
			if (entry.getValue() == geoPoint) {
				GeoPoint mp = ((GeoPoint) me);
				mp.setCoords(x, y, z);
				mp.updateCascade();
				geoPoint.setCoords2D(mp.getX(), mp.getY(), mp.getZ());
				geoPoint.updateCoords();
			}
		}
		macro.getMacroConstruction().updateAllAlgorithms();
		getMacroConstructionState();
		ArrayList<GeoElement> outputList = new ArrayList<>(
				getOutputLength());
		for (int i = 0; i < getOutputLength(); i++) {
			outputList.add(getOutput(i));
		}
		GeoElement.updateCascade(outputList, new TreeSet<AlgoElement>(), true);
		kernel.notifyRepaint();

	}

	@Override
	public int getRelatedModeID() {
		return kernel.getMacroID(macro)
				+ EuclidianConstants.MACRO_MODE_ID_OFFSET;
	}

}
