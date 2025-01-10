/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoPoint.java
 *
 * The point (x,y) has homogeneous coordinates (x,y,1)
 *
 * Created on 30. August 2001, 17:39
 */

package org.geogebra.common.kernel.geos;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.AnimationManager;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.FixedPathRegionAlgo;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.LocateableList;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.PathOrPoint;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoMacro;
import org.geogebra.common.kernel.algos.AlgoPointInRegion;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.algos.SymbolicParametersAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.commands.ParametricProcessor;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * 2D Point
 *
 * @author Markus
 */
public class GeoPoint extends GeoVec3D implements VectorValue, PathOrPoint,
		ConicMirrorable, GeoPointND,
		Transformable, SymbolicParametersAlgo, SymbolicParametersBotanaAlgo, HasCoordinates {
	private static volatile Comparator<GeoPoint> comparatorX;

	// don't set point size here as this would overwrite
	// setConstructionDefaults()
	// in GeoElement constructor
	// public int pointSize = EuclidianStyleConstants.DEFAULT_POINT_SIZE;
	private int pointSize;
	private int pointStyle;

	private double animationValue;

	private Path path;
	private PathParameter pathParameter;

	private Region region;
	private RegionParameters regionParameters;
	/** equals x/z when updated */
	private double x2D = 0;
	/** equals y/z when updated */
	private double y2D = 0;

	/** inhomogeneous x-coord */
	public double inhomX;
	/** inhomogeneous y-coord */
	public double inhomY;
	private boolean isInfinite;
	private boolean isDefined;
	private boolean showUndefinedInAlgebraView = true;
	private PVariable variableCoordinate1 = null;
	private PVariable variableCoordinate2 = null;
	private PVariable[] botanaVars;

	// list of Locateables (GeoElements) that this point is start point of
	// if this point is removed, the Locateables have to be notified
	private LocateableList locateableList;
	private ArrayList<NumberValue> changeableCoordNumbers = null;
	private boolean hasPolarParentNumbers = false;

	private PathParameter tempPathParameter;

	private StringBuilder sbToString = new StringBuilder(50);

	private static volatile TreeSet<AlgoElement> tempSet;

	private Coords coords2D;
	private Coords inhomCoords3D;
	private Coords inhomCoords2D;

	private CoordMatrix4x4 tmpMatrix4x4;

	private Coords tmpCoords;

	private ArrayList<GeoElement> incidenceList;
	private NumberValue verticalIncrement;

	/**
	 * create an undefined GeoPoint
	 *
	 * @param c
	 *            construction
	 */
	public GeoPoint(Construction c) {
		this(c, false);
	}

	/**
	 * @param c
	 *            construction
	 * @param coordMode
	 *            cartesian / polar / ... ; see Kernel.COORD_* constants
	 */
	public GeoPoint(Construction c, int coordMode) {
		this(c, false, coordMode);
	}

	/**
	 * @param c
	 *            construction
	 * @param isHelper
	 *            if is helper point, then don't set construction defaults, etc.
	 */
	public GeoPoint(Construction c, boolean isHelper) {
		this(c, isHelper, Kernel.COORD_CARTESIAN);
	}

	/**
	 * @param c
	 *            construction
	 * @param isHelper
	 *            if is helper point, then don't set construction defaults, etc.
	 * @param coordMode
	 *            cartesian / polar / ... ; see Kernel.COORD_* constants
	 */
	public GeoPoint(Construction c, boolean isHelper, int coordMode) {
		super(c);
		if (!isHelper) {
			setMode(coordMode);
			setConstructionDefaults();
			setAnimationType(ANIMATION_INCREASING);
		}
		setUndefined();
	}

	/**
	 * Creates new GeoPoint
	 *
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param x
	 *            homogeneous x-coord
	 * @param y
	 *            homogeneous y-coord
	 * @param z
	 *            homogeneous z-coord
	 */
	public GeoPoint(Construction c, String label, double x, double y,
			double z) {
		this(c, x, y, z);
		setLabel(label);
	}

	/**
	 * Creates new GeoPoint
	 *
	 * @param c
	 *            construction
	 * @param x
	 *            homogeneous x-coord
	 * @param y
	 *            homogeneous y-coord
	 * @param z
	 *            homogeneous z-coord
	 */
	public GeoPoint(Construction c, double x, double y, double z) {
		super(c, x, y, z); // GeoVec3D constructor
		setConstructionDefaults();
		setAnimationType(ANIMATION_INCREASING);
		this.setIncidenceList(null);
	}

	/**
	 * Creates point on path
	 *
	 * @param c
	 *            construction
	 * @param path
	 *            path
	 */
	public GeoPoint(Construction c, Path path) {
		super(c);
		setConstructionDefaults();
		setAnimationType(ANIMATION_INCREASING);
		this.path = path;
	}

	/**
	 * Creates point in region
	 *
	 * @param c
	 *            construction
	 * @param region
	 *            region
	 */
	public GeoPoint(Construction c, Region region) {
		super(c);
		setConstructionDefaults();
		this.region = region;
	}

	/**
	 * @return whether given geo is a point with complex coordinates
	 */
	public static boolean isComplexNumber(GeoElementND geo) {
		return geo.isGeoPoint()
				&& ((GeoPointND) geo).getToStringMode() == Kernel.COORD_COMPLEX;
	}

	/**
	 * Sets path parameter to null
	 */
	final public void clearPathParameter() {
		pathParameter = null;
	}

	@Override
	final public PathParameter getPathParameter() {
		if (pathParameter == null) {
			pathParameter = new PathParameter(0);
		}
		return pathParameter;
	}

	@Override
	final public RegionParameters getRegionParameters() {
		if (regionParameters == null) {
			regionParameters = new RegionParameters();
		}
		return regionParameters;
	}

	@Override
	public int getRelatedModeID() {
		return getToStringMode() == Kernel.COORD_COMPLEX
				? EuclidianConstants.MODE_COMPLEX_NUMBER
				: EuclidianConstants.MODE_POINT;
	}

	@Override
	public String getTypeString() {
		if (getToStringMode() == Kernel.COORD_COMPLEX) {
			return "ComplexNumber";
		}
		return "Point";
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.POINT;
	}

	/**
	 * Copy constructor
	 *
	 * @param point
	 *            point to copy
	 */
	public GeoPoint(GeoPoint point) {
		super(point.cons);
		setConstructionDefaults();
		set(point);
	}

	@Override
	public void set(GeoElementND geo) {
		set(geo, true);
	}

	@Override
	public void set(GeoElementND geo, boolean macroFeedback) {
		this.isDefined = geo.isDefined();
		if (geo instanceof GeoPointND) {
			GeoPointND p = (GeoPointND) geo;
			if (p.getPathParameter() != null) {
				pathParameter = getPathParameter();
				pathParameter.set(p.getPathParameter());
			}
			animationValue = p.getAnimationValue();
			if (geo instanceof GeoPoint) {
				GeoPoint pt = (GeoPoint) p;
				setCoords(pt.x, pt.y, pt.z, macroFeedback);
			} else {
				setCoords(p.getInhomX(), p.getInhomY(), 1, macroFeedback);
			}
			setMode(p.getToStringMode()); // complex etc
		} else if (geo.isGeoVector()) {
			GeoVector v = (GeoVector) geo;
			setCoords(v.getX(), v.getY(), 1d);
			setMode(v.getToStringMode()); // complex etc
		} else if (geo.isGeoNumeric()) {
			GeoNumeric v = (GeoNumeric) geo;
			setCoords(v.getDouble(), 0, 1d);
			setMode(Kernel.COORD_COMPLEX);
		} else if (geo instanceof GeoList) {
			// GGB-1981
			GeoList list = (GeoList) geo;
			double xList = list.size() > 0 ? list.get(0).evaluateDouble()
					: Double.NaN;
			double yList = list.size() > 1 ? list.get(1).evaluateDouble()
					: Double.NaN;

			setCoords(xList, yList, 1);

		} else {
			Log.error(geo.getGeoClassType() + " invalid as point");
			throw new IllegalArgumentException();
		}
		reuseDefinition(geo);
	}

	@Override
	public GeoPoint copy() {
		return new GeoPoint(this);
	}

	/*
	 * void initSetLabelVisible() { setLabelVisible(true); }
	 */

	@Override
	public void setPointSize(int i) {
		pointSize = i;
	}

	@Override
	public int getPointSize() {
		return pointSize;
	}

	/**
	 * @author Florian Sonner
	 * @version 2008-07-17
	 */
	@Override
	final public int getPointStyle() {
		return pointStyle;
	}

	@Override
	public boolean showPointProperties() {
		return true;
	}

	/**
	 * @author Florian Sonner
	 * @version 2008-07-17
	 * @param style
	 *            the new style to use
	 */
	@Override
	public void setPointStyle(int style) {

		if (style > -1 && style <= EuclidianStyleConstants.MAX_POINT_STYLE) {
			pointStyle = style;
		} else {
			pointStyle = -1;
		}

	}

	@Override
	public boolean isPointerChangeable() {

		return isPointChangeable(this);
	}

	/**
	 * static method for used in GeoPoint3D
	 *
	 * @param point
	 *            point
	 * @return true if point is Changeable
	 */
	public static final boolean isPointChangeable(GeoElement point) {

		// if we drag a AlgoDynamicCoordinates, we want its point to be dragged
		AlgoElement algo = point.getParentAlgorithm();

		// make sure Point[circle, param] is not draggable
		if (algo instanceof FixedPathRegionAlgo) {
			return ((FixedPathRegionAlgo) algo).isChangeable(point)
					&& !point.isLocked();
		}

		return !point.isLocked() && (point.isIndependent()
				|| point.isPointOnPath() || point.isPointInRegion());
	}

	@Override
	public boolean moveFromChangeableCoordParentNumbers(Coords rwTransVec,
			Coords targetPosition, ArrayList<GeoElement> updateGeos,
			ArrayList<GeoElement> tempMoveObjectList) {
		Coords endPosition = targetPosition;
		if (!hasChangeableCoordParentNumbers()) {
			return false;
		}

		if (endPosition == null) {
			endPosition = getInhomCoords().add(rwTransVec);
		}

		// move Point like curve(slider)
		if (isPointOnCurveWithSlider()) {

			GeoPoint p = new GeoPoint(cons, endPosition.getX(),
					endPosition.getY(), 1);

			ExpressionNode exp = getDefinition();

			GeoCurveCartesian curve = (GeoCurveCartesian) exp.getLeft();
			GeoNumeric param = (GeoNumeric) exp.getRight();

			double t = curve.getClosestParameter(p, param.getValue());

			param.setValue(t);
			param.updateRepaint();

			return true;

		}

		// translate x and y coordinates by changing the parent coords
		// accordingly
		ArrayList<NumberValue> freeCoordNumbers = getCoordParentNumbers();
		NumberValue xvar = freeCoordNumbers.get(0);
		NumberValue yvar = freeCoordNumbers.get(1);

		// polar coords (r; phi)
		if (hasPolarParentNumbers()) {
			// radius
			double radius = MyMath.length(endPosition.getX(),
					endPosition.getY());
			if (xvar instanceof GeoNumeric && xvar != yvar) {
				((GeoNumeric) xvar).setValue(xvar.getDouble()
						- MyMath.length(inhomX, inhomY) + radius);
			}
			if (yvar instanceof GeoNumeric) {
				// angle
				double endAngle = Math.atan2(endPosition.getY(),
						endPosition.getX());
				double oldAngle = Math.atan2(inhomY, inhomX);

				double angle = DoubleUtil.convertToAngleValue(
						yvar.getDouble() - oldAngle + endAngle);
				// angle outside of slider range
				if (((GeoNumeric) yvar).isIntervalMinActive()
						&& ((GeoNumeric) yvar).isIntervalMaxActive()
						&& (angle < ((GeoNumeric) yvar).getIntervalMin()
								|| angle > ((GeoNumeric) yvar)
										.getIntervalMax())) {
					// use angle value closest to closest border
					double minDiff = Math.abs(
							angle - ((GeoNumeric) yvar).getIntervalMin());
					if (minDiff > Math.PI) {
						minDiff = Kernel.PI_2 - minDiff;
					}
					double maxDiff = Math.abs(
							angle - ((GeoNumeric) yvar).getIntervalMax());
					if (maxDiff > Math.PI) {
						maxDiff = Kernel.PI_2 - maxDiff;
					}

					if (minDiff < maxDiff) {
						angle = angle - Kernel.PI_2;
					} else {
						angle = angle + Kernel.PI_2;
					}
				}
				((GeoNumeric) yvar).setValue(angle);
			}
		}

		// cartesian coords (xvar + constant, yvar + constant)
		else {

			// only change if GeoNumeric

			if (xvar instanceof GeoNumeric) {
				incrementParentNumeric(endPosition.getX() - inhomX,
						(GeoNumeric) xvar, targetPosition);
			}

			if (xvar != yvar && yvar instanceof GeoNumeric) {
				incrementParentNumeric(endPosition.getY() - inhomY,
						(GeoNumeric) yvar, targetPosition);
			}
		}

		if (xvar instanceof GeoNumeric) {
			addParentToUpdateList((GeoNumeric) xvar, updateGeos,
					tempMoveObjectList);
		}
		if (yvar instanceof GeoNumeric) {
			addParentToUpdateList((GeoNumeric) yvar, updateGeos,
					tempMoveObjectList);
		}

		return true;
	}

	/**
	 * Increment single coordinate of a draggable point depending on sliders.
	 * If we know target position, use the diff, if not use max(slider increment, diff)
	 * @param diff suggested increment
	 * @param num number/slider to be increased
	 * @param target target position
	 */
	public static void incrementParentNumeric(double diff, GeoNumeric num, Coords target) {
		double increment = target != null ? diff
				: Math.max(Math.abs(diff), num.getAnimationStep()) * Math.signum(diff);
		double newVal = num.getDouble()  + increment;
		if (num.isSlider()) {
			newVal = num.restrictToSliderValues(newVal);
		}
		num.setValue(newVal);
	}

	/**
	 * Returns whether this point has two changeable numbers as coordinates,
	 * e.g. point A = (a, b) where a and b are free GeoNumeric objects.
	 */
	@Override
	final public boolean hasChangeableCoordParentNumbers() {

		if (isLocked()) {
			return false;
		}

		if (isPointOnCurveWithSlider()) {
			return true;
		}

		ArrayList<NumberValue> coords = getCoordParentNumbers();
		if (coords.size() == 0) {
			return false;
		}

		NumberValue num1 = coords.get(0);
		NumberValue num2 = coords.get(1);

		if (num1 == null && num2 == null) {
			return false;
		}

		if (num1 instanceof GeoNumeric && num2 instanceof GeoNumeric) {
			GeoElement maxObj1 = GeoElement
					.as(((GeoNumeric) num1).getIntervalMaxObject());
			GeoElement maxObj2 = GeoElement
					.as(((GeoNumeric) num2).getIntervalMaxObject());
			GeoElement minObj1 = GeoElement
					.as(((GeoNumeric) num1).getIntervalMinObject());
			GeoElement minObj2 = GeoElement
					.as(((GeoNumeric) num2).getIntervalMinObject());
			if (maxObj1 != null && maxObj1.isChildOrEqual((GeoElement) num2)) {
				return false;
			}
			if (minObj1 != null && minObj1.isChildOrEqual((GeoElement) num2)) {
				return false;
			}
			if (maxObj2 != null && maxObj2.isChildOrEqual((GeoElement) num1)) {
				return false;
			}
			if (minObj2 != null && minObj2.isChildOrEqual((GeoElement) num1)) {
				return false;
			}
		}

		boolean ret = (num1 instanceof GeoNumeric
				&& ((GeoNumeric) num1).isPointerChangeable())
				|| (num2 instanceof GeoNumeric
						&& ((GeoNumeric) num2).isPointerChangeable());

		return ret;
	}

	/**
	 *
	 * @return true if we are a Point on a Curve defined like c(a) for Curve a,
	 *         Slider a
	 */
	private boolean isPointOnCurveWithSlider() {
		ExpressionNode exp = getDefinition();

		if (exp == null) {
			return false;
		}

		ExpressionValue left = exp.getLeft();
		ExpressionValue right = exp.getRight();
		Operation op = exp.getOperation();

		return op == Operation.VEC_FUNCTION && left instanceof GeoCurveCartesian
				&& right instanceof GeoNumeric
				&& ((GeoNumeric) right).isSlider();
	}

	/**
	 * Returns an array of GeoNumeric/MyDouble objects that directly control
	 * this point's coordinates. For point P = (a, b) the array [a, b] is
	 * returned, for P = (x(A) + c, d + y(A)) the array [c, d] is returned, for
	 * P = (x(A) + c, y(A)) the array [c, null] is returned.
	 *
	 * for (a,1), [GeoNumeric, MyDouble] is returned
	 *
	 * @return null if this point is not defined using two GeoNumeric objects
	 */
	final public ArrayList<NumberValue> getCoordParentNumbers() {
		// init changeableCoordNumbers
		if (changeableCoordNumbers == null) {
			changeableCoordNumbers = new ArrayList<>(2);
			ExpressionNode en = getDefinition();
			// dependent point of form P = (a, b)
			if (!isIndependent() && en != null) {
				// (xExpression, yExpression)
				if (en.unwrap() instanceof MyVecNode) {
					// (xExpression, yExpression)
					MyVecNode vn = (MyVecNode) en.getLeft();
					hasPolarParentNumbers = vn.hasPolarCoords();

					try {
						// try to get free number variables used in coords for
						// this point
						// don't allow expressions like "a + x(A)" for polar
						// coords (r; phi)
						ExpressionValue xcoord = vn.getX();
						ExpressionValue ycoord = vn.getY();
						ParametricProcessor proc = kernel.getAlgebraProcessor()
								.getParamProcessor();
						NumberValue xNum = proc.getCoordNumber(xcoord);
						NumberValue yNum = proc.getCoordNumber(ycoord);

						if (xNum instanceof GeoNumeric
								&& ((GeoNumeric) xNum).isPointerChangeable()) {
							changeableCoordNumbers.add(xNum);
						} else {
							changeableCoordNumbers.add(null);
						}
						if (yNum instanceof GeoNumeric
								&& ((GeoNumeric) yNum).isPointerChangeable()) {
							changeableCoordNumbers.add(yNum);
						} else {
							changeableCoordNumbers.add(null);
						}
					} catch (Throwable e) {
						changeableCoordNumbers.clear();
						Log.debug(e);
					}
				}
			}
		}

		return changeableCoordNumbers;
	}

	/**
	 * @return whether getCoordParentNumbers() returns polar variables (r; phi).
	 */
	private boolean hasPolarParentNumbers() {
		return hasPolarParentNumbers;
	}

	/**
	 * Returns whether this number can be animated. Only free numbers with min
	 * and max interval values can be animated (i.e. shown or hidden sliders).
	 */
	@Override
	public boolean isAnimatable() {
		return isPointOnPath() && isPointerChangeable();
	}

	@Override
	public boolean isPointOnPath() {
		return path != null;
	}

	@Override
	final public Path getPath() {
		return path;
	}

	/**
	 * @param p
	 *            path restricting this point
	 */
	@Override
	public void setPath(Path p) {
		path = p;
		// tell conic that this point is on it, that's needed to handle
		// reflections
		// of conics correctly for path parameter calculation of point P
		if (path != null && path.isGeoConic()) {
			((GeoConicND) path).addPointOnConic(this); // GeoConicND
		}
	}

	/**
	 * Increments path parameter
	 *
	 * @param a
	 *            increment
	 */
	@Override
	public void addToPathParameter(double a) {
		PathParameter parameter = getPathParameter();
		parameter.t += a;

		// update point relative to path
		path.pathChanged(this);
		updateCoords();
	}

	@Override
	public void updatePathParameter(double t) {
		PathParameter parameter = getPathParameter();
		parameter.t = t;

		// update point relative to path
		path.pathChanged(this);
		updateCoords();
	}

	@Override
	final public boolean isInfinite() {
		return isInfinite;
	}

	@Override
	final public boolean isFinite() {
		return isDefined && !isInfinite;
	}

	@Override
	final public boolean showInEuclidianView() {
		return isDefined && !isInfinite;
	}

	@Override
	public final boolean showInAlgebraView() {
		return isDefined || showUndefinedInAlgebraView;
	}

	@Override
	public boolean isDefined() {
		return isDefined;
	}

	/*
	 * Order of instructions is important here because we need to avoid infinite
	 * loop setUndefined -> setCoords -> pointChangedForRegion -> setUndefined
	 */
	@Override
	public void setUndefined() {
		isDefined = false;
		super.setUndefined();
	}

	@Override
	public void setCoords2D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	final public void setCoords(double x, double y, double z) {
		setCoords(x, y, z, true);
	}

	/**
	 * Sets homogeneous coordinates and updates inhomogeneous coordinates
	 *
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param macroFeedback
	 *            whether parent macro may be notified
	 */

	final public void setCoords(double x, double y, double z,
			boolean macroFeedback) {
		// set coordinates
		this.x = x;
		this.y = y;
		this.z = z;
		setDefinition(null);
		// update point on path: this may change coords
		// so updateCoords() is called afterwards
		if (path != null) {
			// remember path parameter for undefined case
			PathParameter tempParameter = getTempPathparameter();
			tempParameter.set(getPathParameter());
			path.pointChanged(this);

			// make sure animation starts from the correct place
			animationValue = PathNormalizer.toNormalizedPathParameter(
					getPathParameter().t, path.getMinParameter(),
					path.getMaxParameter());
		}

		// region
		else if (hasRegion()) {
			region.pointChangedForRegion(this);
		} else if (getParentAlgorithm() != null && macroFeedback) {
			if (getParentAlgorithm() instanceof AlgoMacro) {
				((AlgoMacro) getParentAlgorithm()).setCoords(this, x, y, z);
			}
		}

		// this avoids multiple computations of inhomogeneous coords;
		// see for example distance()
		updateCoords();

		// undefined and on path: remember old path parameter
		if (path != null) {
			if (!isDefined) {
				PathParameter parameter = getPathParameter();
				PathParameter tempParameter = getTempPathparameter();
				parameter.set(tempParameter);
			} else { // store current path parameter (needed e.g. on file
						// loading)
				PathParameter tempParameter = getTempPathparameter();
				tempParameter.set(getPathParameter());
			}
		}

	}

	@Override
	protected void setUndefinedCoords() {

		// set coordinates
		this.x = Double.NaN;
		this.y = Double.NaN;
		this.z = Double.NaN;

		inhomX = Double.NaN;
		inhomY = Double.NaN;

		// undefined and on path: remember old path parameter
		if (path != null) {
			PathParameter parameter = getPathParameter();
			PathParameter tempParameter = getTempPathparameter();
			parameter.set(tempParameter);
		}
	}

	@Override
	public void setCoords(Coords v, boolean doPathOrRegion) {

		if (doPathOrRegion) {
			setCoords(v.getX(), v.getY(), v.getLast());
		} else {
			// set coordinates
			this.x = v.getX();
			this.y = v.getY();
			this.z = v.getLast();
		}
		updateCoords();
	}

	private PathParameter getTempPathparameter() {
		if (tempPathParameter == null) {
			tempPathParameter = new PathParameter();
		}
		return tempPathParameter;
	}

	@Override
	final public void updateCoords() {
		// infinite point
		// #5202
		if (!Double.isNaN(z) && DoubleUtil.isEpsilon(z, x, y)) {
			isInfinite = true;
			isDefined = !(Double.isNaN(x) || Double.isNaN(y));
			inhomX = Double.NaN;
			inhomY = Double.NaN;
		}
		// finite point
		else {
			isInfinite = false;
			isDefined = !(Double.isNaN(x) || Double.isNaN(y)
					|| Double.isNaN(z));

			if (isDefined) {
				// make sure the z coordinate is always positive
				// this is important for the orientation of a line or ray
				// computed using two points P, Q with cross(P, Q)
				if (z < 0) {
					x = -x;
					y = -y;
					z = -z;
				}

				// update inhomogeneous coords
				if (z == 1.0) {
					inhomX = x;
					inhomY = y;
				} else {
					inhomX = x / z;
					inhomY = y / z;
				}
			} else {
				inhomX = Double.NaN;
				inhomY = Double.NaN;
			}
		}
	}

	/**
	 * @param r
	 *            radius
	 * @param phi
	 *            phase
	 */
	final public void setPolarCoords(double r, double phi) {
		setCoords(r * Math.cos(phi), r * Math.sin(phi), 1.0d);
	}

	@Override
	final public void setCoords(GeoVec3D v) {
		setCoords(v.x, v.y, v.z);
	}

	/**
	 * Sets coords to (x,y,1)
	 *
	 * @param v
	 *            vector (x,y)
	 */
	final public void setCoords(GeoVec2D v) {
		setCoords(v.getX(), v.getY(), 1.0);
	}

	/**
	 * Yields true if the inhomogeneous coordinates of this point are equal to
	 * those of point P. Infinite points are checked for linear dependency.
	 */
	// Michael Borcherds 2008-04-30
	@Override
	final public ExtendedBoolean isEqualExtended(GeoElementND geo) {
		return ExtendedBoolean.newExtendedBoolean(isEqual(geo, Kernel.STANDARD_PRECISION));
	}

	/**
	 * Checks whether geo is a point and whether it's same as this with given
	 * precision
	 *
	 * @param geo
	 *            element
	 * @param eps
	 *            precision
	 * @return whether the two points are equal with given precision
	 */
	final public boolean isEqual(GeoElementND geo, double eps) {

		if (!geo.isGeoPoint()) {
			return false;
		}

		if (geo.isGeoElement3D()) {
			return geo.isEqual(this); // do the 3D test
		}

		return isEqualPoint2D((GeoPoint) geo);

	}

	@Override
	public boolean isEqualPointND(GeoPointND geo) {

		if (geo == null) {
			return false;
		}

		if (geo.isGeoElement3D()) {
			return geo.isEqualPointND(this); // do the 3D test
		}

		return isEqualPoint2D((GeoPoint) geo);
	}

	private boolean isEqualPoint2D(GeoPoint P) {

		if (P == null || !(isDefined() && P.isDefined())) {
			return false;
		}

		// both finite
		if (isFinite() && P.isFinite()) {
			return DoubleUtil.isEqual(inhomX, P.inhomX)
					&& DoubleUtil.isEqual(inhomY, P.inhomY);
		} else if (isInfinite() && P.isInfinite()) {
			return linDep(P);
		} else {
			return false;
		}
	}

	/**
	 * Writes (x/z, y/z) to res.
	 */
	@Override
	final public void getInhomCoords(double[] res) {
		res[0] = inhomX;
		res[1] = inhomY;
	}

	/**
	 * Gets polar coords of this point
	 *
	 * @param res
	 *            array to store results
	 */
	final public void getPolarCoords(double[] res) {
		res[0] = MyMath.length(inhomX, inhomY);
		res[1] = Math.atan2(inhomY, inhomX);
	}

	/**
	 * @return inhomogeneous X
	 */
	@Override
	final public double getInhomX() {
		return inhomX;
	}

	/**
	 * @return inhomogeneous Y
	 */
	@Override
	final public double getInhomY() {
		return inhomY;
	}

	/**
	 * @return inhomogeneous Z (in 3D space)
	 */
	@Override
	final public double getInhomZ() {
		return 0;
	}

	@Override
	final public double[] vectorTo(GeoPointND QI) {
		return new double[] { QI.getInhomX() - getInhomX(),
				QI.getInhomY() - getInhomY(), QI.getInhomZ() };
	}

	@Override
	public double distance(GeoPointND P) {
		// TODO dimension ?
		return getInhomCoordsInD3().distance(P.getInhomCoordsInD3());
	}

	// euclidian distance between this GeoPoint and P
	@Override
	final public double distance(GeoPoint P) {
		return MyMath.length(P.inhomX - inhomX, P.inhomY - inhomY);
	}

	/**
	 *
	 * @param x2
	 *            x coord
	 * @param y2
	 *            y coord
	 * @return distance between this and (x,y)
	 */
	final public double distance(double x2, double y2) {
		return MyMath.length(x2 - inhomX, y2 - inhomY);
	}

	/**
	 * Convenience method to tell whether these two points are in the same place
	 *
	 * @param P
	 *            first point
	 * @param Q
	 *            second point
	 * @return true if they are in the same place
	 */
	final public static boolean samePosition(GeoPointND P, GeoPointND Q) {
		return DoubleUtil.isZero(P.distance(Q));
	}

	/**
	 * returns the square distance of this point and P (may return infinity or
	 * NaN).
	 *
	 * @param P
	 *            other point
	 * @return square distance to other point
	 */
	final public double distanceSqr(GeoPoint P) {
		double vx = P.inhomX - inhomX;
		double vy = P.inhomY - inhomY;
		return vx * vx + vy * vy;
	}

	/**
	 * Returns if three points are collinear.
	 *
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param C
	 *            third point
	 * @return whether the three points A, B and C are collinear.
	 */
	public static boolean collinear(GeoPoint A, GeoPoint B, GeoPoint C) {
		// A, B, C are collinear iff det(ABC) == 0

		// calculate the determinante of ABC
		// det(ABC) = sum1 - sum2

		double sum1 = A.x * B.y * C.z + B.x * C.y * A.z + C.x * A.y * B.z;
		double sum2 = A.z * B.y * C.x + B.z * C.y * A.x + C.z * A.y * B.x;

		// det(ABC) == 0 <=> sum1 == sum2

		// A.z, B.z, C.z could be zero
		double eps = Math.max(Kernel.MIN_PRECISION,
				Kernel.MIN_PRECISION * A.z * B.z * C.z);

		return DoubleUtil.isEqual(sum1, sum2, eps);
	}

	/**
	 * Decides if 4 points are lying on the same circle.
	 *
	 * @param inputPoint1
	 *            first object
	 * @param inputPoint2
	 *            second object
	 * @param inputPoint3
	 *            third object
	 * @param inputPoint4
	 *            forth object
	 * @return if the inputs are concyclic.
	 */
	public static boolean concyclic(GeoPoint inputPoint1, GeoPoint inputPoint2,
			GeoPoint inputPoint3, GeoPoint inputPoint4) {
		double ax = inputPoint1.getX(), ay = inputPoint1.getY(),
				az = inputPoint1.getZ(), bx = inputPoint2.getX(),
				by = inputPoint2.getY(), bz = inputPoint2.getZ(),
				cx = inputPoint3.getX(), cy = inputPoint3.getY(),
				cz = inputPoint3.getZ(), dx = inputPoint4.getX(),
				dy = inputPoint4.getY(), dz = inputPoint4.getZ();

		// Using Ptolemy's theorem

		double ab = Math.hypot(bx * az - ax * bz, by * az - ay * bz);
		double ac = Math.hypot(cx * az - ax * cz, cy * az - ay * cz);
		double ad = Math.hypot(dx * az - ax * dz, dy * az - ay * dz);
		double bc = Math.hypot(cx * bz - bx * cz, cy * bz - by * cz);
		double bd = Math.hypot(dx * bz - bx * dz, dy * bz - by * dz);
		double cd = Math.hypot(dx * cz - cx * dz, dy * cz - cy * dz);

		// each product of distances is scaled by D = az * bz * cz * dz,
		// compatible with 458df513 where it was scaled by D^2 and divided by D
		return DoubleUtil.isZero(ab * cd + bc * ad - ac * bd, Kernel.MIN_PRECISION)
				|| DoubleUtil.isZero(ab * cd + ac * bd - bc * ad, Kernel.MIN_PRECISION)
				|| DoubleUtil.isZero(bc * ad + ac * bd - ab * cd, Kernel.MIN_PRECISION);
	}

	/**
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param C
	 *            third point
	 * @return whether the three points A, B and C are collinear.
	 */
	public static boolean collinearND(GeoPointND A, GeoPointND B,
			GeoPointND C) {
		// A, B, C are collinear iff (A-B)x(A-C) == (0,0,0)

		Coords diffB = A.getInhomCoordsInD3().sub(B.getInhomCoordsInD3());
		Coords diffC = A.getInhomCoordsInD3().sub(C.getInhomCoordsInD3());
		return !diffB.isLinearIndependent(diffC);

	}

	/**
	 * Calcs determinant of P and Q. Note: no test for defined or infinite is
	 * done here.
	 *
	 * @param P
	 *            first point
	 * @param Q
	 *            second point
	 * @return determinant
	 */
	public static final double det(GeoPoint P, GeoPoint Q) {
		return (P.x * Q.y - Q.x * P.y) / (P.z * Q.z);
	}

	/**
	 * Returns the affine ratio for three collinear points A, B and C. The ratio
	 * is lambda with C = A + lambda * AB, i.e. lambda = AC/AB. Note: the
	 * collinearity is not checked in this method.
	 *
	 * @param A
	 *            A
	 * @param B
	 *            B
	 * @param C
	 *            C
	 * @return lambda = AC/AB.
	 */
	public static final double affineRatio(GeoPointND A, GeoPointND B,
			GeoPointND C) {
		Coords cA = A.getInhomCoordsInD3();
		Coords cB = B.getInhomCoordsInD3();
		Coords cC = C.getInhomCoordsInD3();

		double ABx = cB.getX() - cA.getX();
		double ABy = cB.getY() - cA.getY();
		double ABz = cB.getZ() - cA.getZ();

		// avoid division by a number close to zero
		if (Math.abs(ABx) > Math.abs(ABy)) {
			if (Math.abs(ABx) > Math.abs(ABz)) {
				return (cC.getX() - cA.getX()) / ABx;
			}
			return (cC.getZ() - cA.getZ()) / ABz;
		}
		if (Math.abs(ABy) > Math.abs(ABz)) {
			return (cC.getY() - cA.getY()) / ABy;
		}
		return (cC.getZ() - cA.getZ()) / ABz;
	}

	/*
	 * MOVEMENTS
	 ***********************************************************/

	/**
	 * translate by vector v
	 */
	@Override
	final public void translate(Coords v) {
		setCoords(x + v.getX() * z, y + v.getY() * z, z);
	}

	@Override
	final public boolean isTranslateable() {
		return true;
	}

	/**
	 * dilate from S by r
	 */
	@Override
	final public void dilate(NumberValue rval, Coords S) {
		double r = rval.getDouble();
		double temp = 1 - r;
		setCoords(r * x + temp * S.getX() * z, r * y + temp * S.getY() * z, z);
	}

	/**
	 * dilate from O
	 *
	 * @param r
	 *            ratio
	 */
	final public void dilate(double r) {
		setCoords(r * x, r * y, z);
	}

	/**
	 * rotate this point by angle phi around (0,0)
	 */
	@Override
	final public void rotate(NumberValue phiValue) {
		double phi = phiValue.getDouble();
		double cos = MyMath.cos(phi);
		double sin = Math.sin(phi);

		setCoords(x * cos - y * sin, x * sin + y * cos, z);
	}

	/**
	 * rotate this point by angle phi around Q
	 */
	@Override
	final public void rotate(NumberValue phiValue, Coords point) {
		Coords Q = point;
		double phi = phiValue.getDouble();
		double cos = MyMath.cos(phi);
		double sin = Math.sin(phi);
		double qx = z * Q.getX();
		double qy = z * Q.getY();

		setCoords((x - qx) * cos + (qy - y) * sin + qx,
				(x - qx) * sin + (y - qy) * cos + qy, z);
	}

	@Override
	final public void rotate(NumberValue phiValue, GeoPointND point) {
		rotate(phiValue, point.getInhomCoords());
	}

	/**
	 * mirror this point at point Q
	 */
	@Override
	final public void mirror(Coords Q) {
		double qx = z * Q.getX();
		double qy = z * Q.getY();

		setCoords(2.0 * qx - x, 2.0 * qy - y, z);
	}

	/*
	 * Michael Borcherds 2008-02-10 Invert point in circle
	 */
	@Override
	final public void mirror(GeoConic c) {
		if (c.getType() == GeoConicNDConstants.CONIC_CIRCLE) {
			// Mirror point in circle
			double r = c.getHalfAxes()[0];
			GeoVec2D midpoint = c.getTranslationVector();
			double a = midpoint.getX();
			double b = midpoint.getY();
			if (Double.isInfinite(x) || Double.isInfinite(y2D)) {
				setCoords(a, b, 1.0);
			} else {
				double sf = r * r / ((inhomX - a) * (inhomX - a)
						+ (inhomY - b) * (inhomY - b));
				setCoords(a + sf * (inhomX - a), b + sf * (inhomY - b), 1.0);
			}
		} else if (/*
					 * c.getType() == GeoConic.CONIC_LINE ||
					 */ c.getType() == GeoConicNDConstants.CONIC_PARALLEL_LINES) {
			/* In the case the conic is a line we mirror about that line. */
			GeoLine g = c.getLines()[0];
			/* g = Line[P1,P2] */
			mirror(g);
			/* g is not needed anymore, so we remove it. */
			g.remove();

		} else {
			setUndefined();
		}
	}

	/**
	 * mirror this point at line g
	 */
	@Override
	final public void mirror(GeoLineND g1) {

		GeoLine g = (GeoLine) g1;

		// Y = S(phi).(X - Q) + Q
		// where Q is a point on g, S(phi) is the mirrorTransform(phi)
		// and phi/2 is the line's slope angle

		// get arbitrary point of line
		double qx, qy;
		if (Math.abs(g.getX()) > Math.abs(g.getY())) {
			qx = -z * g.getZ() / g.getX();
			qy = 0.0d;
		} else {
			qx = 0.0d;
			qy = -z * g.getZ() / g.getY();
		}

		// translate -Q
		x -= qx;
		y -= qy;

		// S(phi)
		mirrorXY(2.0 * Math.atan2(-g.getX(), g.getY()));

		// translate back +Q
		x += qx;
		y += qy;

		// update inhom coords
		updateCoords();
	}

	/***********************************************************/

	@Override
	final public String toString(StringTemplate tpl) {
		return label
				+ getEqualSign(getToStringMode(), tpl.getCoordStyle(kernel.getCoordStyle()), tpl)
				+ toValueString(tpl);
	}

	@Override
	final public String toStringDescription(StringTemplate tpl) {
		boolean isAvDescrip = Kernel.ALGEBRA_STYLE_DESCRIPTION == getKernel()
				.getAlgebraStyle();
		if (isAvDescrip) {
			sbToString.setLength(0);
			sbToString.append(
					getKernel().getLocalization().getMenu("Point"));
			sbToString.append(" ");
			sbToString.append(label);
			return sbToString.toString();
		}
		return toString(tpl);
	}

	/**
	 * get "=" or not for "A=(...)"
	 *
	 * @param toStringMode
	 *            point string mode
	 * @param coordStyle
	 *            point coord style
	 * @return the correct equals sign
	 */
	public static String getEqualSign(int toStringMode, int coordStyle, StringTemplate tpl) {
		if (toStringMode == Kernel.COORD_COMPLEX) {
			return tpl.getEqualsWithSpace();
		} else {
			switch (coordStyle) {
			case Kernel.COORD_STYLE_FRENCH:
				// no equal sign
				return ": ";

			case Kernel.COORD_STYLE_AUSTRIAN:
				// no equal sign
				if (!tpl.isForEditorParser()) {
					return "";
				}

			default:
				return tpl.getEqualsWithSpace();
			}
		}
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (tpl.getStringType() == ExpressionNodeConstants.StringType.GIAC) {
			if (getDefinition() != null) {
				return getDefinition().toValueString(tpl);
			}

			String xStr = kernel.format(getInhomX(), tpl);
			String yStr = kernel.format(getInhomY(), tpl);

			if (getToStringMode() == Kernel.COORD_COMPLEX) {
				return "(" + xStr + "+i*" + yStr + ")";
			} else {
				return "point(" + xStr + ',' + yStr + ")";
			}
		}

		if (isInfinite() || (!isDefined && getToStringMode() == Kernel.COORD_COMPLEX)) {
			return "?";
		}

		sbToString.setLength(0);
		if (getToStringMode() == Kernel.COORD_CARTESIAN_3D) {
			buildValueStringCoordCartesian3D(kernel, tpl, getInhomX(),
					getInhomY(), 0, sbToString);
		} else if (getToStringMode() == Kernel.COORD_SPHERICAL) {
			buildValueStringCoordSpherical(kernel, tpl, getInhomX(),
					getInhomY(), 0, sbToString);
		} else {
			buildValueString(kernel, tpl, getToStringMode(), getInhomX(),
					getInhomY(), sbToString);
		}

		return sbToString.toString();
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param tpl
	 *            string template
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param sb
	 *            string builder
	 */
	public static final void buildValueStringCoordCartesian3D(Kernel kernel,
			StringTemplate tpl, double x, double y, double z,
			StringBuilder sb) {

		if (tpl.hasCASType()) {

			sb.append("point(");
			sb.append(kernel.format(x, tpl));
			sb.append(',');
			sb.append(kernel.format(y, tpl));
			sb.append(',');
			sb.append(kernel.format(z, tpl));
			sb.append(")");

			return;
		}
		sb.append(tpl.leftBracket());
		sb.append(kernel.format(x, tpl));
		String separatorWithSpace = getValueSeparatorWithSpace(kernel, tpl);

		sb.append(separatorWithSpace);
		sb.append(kernel.format(y, tpl));

		sb.append(separatorWithSpace);
		sb.append(kernel.format(z, tpl));

		sb.append(tpl.rightBracket());
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param tpl
	 *            output template
	 * @return separator for cartesian coords
	 */
	public static final String getValueSeparatorWithSpace(Kernel kernel,
			StringTemplate tpl) {
		if (tpl.hasCASType()) {
			return ",";
		}
		StringBuilder sb = new StringBuilder();
		if (tpl.getCoordStyle(kernel.getCoordStyle()) == Kernel.COORD_STYLE_AUSTRIAN) {
			tpl.appendOptionalSpace(sb);
			sb.append(tpl.getPointCoordBar());
			tpl.appendOptionalSpace(sb);
		} else {
			tpl.getCommaOptionalSpace(sb, kernel.getLocalization());
		}
		return sb.toString();
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param tpl
	 *            string template
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 * @param sbBuildValueString
	 *            string builder
	 */
	public static final void buildValueStringCoordSpherical(Kernel kernel,
			StringTemplate tpl, double x, double y, double z,
			StringBuilder sbBuildValueString) {

		double lengthXY = MyMath.length(x, y);
		String radius = kernel.format(MyMath.length(lengthXY, z), tpl);

		if (tpl.hasCASType()) {
			String theta = kernel.format(Math.atan2(y, x), tpl);
			String phi = kernel.format(Math.atan2(z, lengthXY), tpl);

			//x
			sbBuildValueString.append("point((");
			sbBuildValueString.append(radius).append(")");
			sbBuildValueString.append("*cos(").append(theta).append(")");
			sbBuildValueString.append("*cos(").append(phi).append(")");
			sbBuildValueString.append(",");

			//y
			sbBuildValueString.append("(").append(radius).append(")");
			sbBuildValueString.append("*sin(").append(theta).append(")");
			sbBuildValueString.append("*cos(").append(phi).append(")");
			sbBuildValueString.append(",");

			//z
			sbBuildValueString.append("(").append(radius).append(")");
			sbBuildValueString.append("*sin(").append(phi).append(")");
			sbBuildValueString.append(")");

			return;
		}

		sbBuildValueString.append('(');
		sbBuildValueString.append(radius);
		sbBuildValueString.append("; ");
		sbBuildValueString
				.append(kernel.formatAngle(Math.atan2(y, x), tpl, false));
		sbBuildValueString.append("; ");
		sbBuildValueString
				.append(kernel.formatAngle(Math.atan2(z, lengthXY), tpl, true));
		sbBuildValueString.append(')');

	}

	/**
	 * @param kernel
	 *            kernel
	 * @param tpl
	 *            string template
	 * @param toStringMode
	 *            Kernel.POLAR, Kernel.CARTESIAN, ...
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param sbBuildValueString
	 *            string builder
	 */
	public static final void buildValueString(Kernel kernel, StringTemplate tpl,
			int toStringMode, double x, double y,
			StringBuilder sbBuildValueString) {
		switch (toStringMode) {
		case Kernel.COORD_POLAR:
			sbBuildValueString.append(tpl.leftBracket());
			sbBuildValueString.append(kernel.format(MyMath.length(x, y), tpl));
			sbBuildValueString.append(tpl.polarSeparator());
			tpl.appendOptionalSpace(sbBuildValueString);
			sbBuildValueString
					.append(kernel.formatAngle(Math.atan2(y, x), tpl, false));
			sbBuildValueString.append(tpl.rightBracket());
			break;

		case Kernel.COORD_COMPLEX:
			if (x != 0) {
				sbBuildValueString.append(kernel.format(x, tpl));
				tpl.appendOptionalSpace(sbBuildValueString);
				kernel.formatSignedCoefficient(y, sbBuildValueString, tpl);
			} else if (y != 1) {
				sbBuildValueString.append(kernel.format(y, tpl));
			}
			sbBuildValueString.append(tpl.getImaginary());
			break;

		default: // CARTESIAN
			sbBuildValueString.append(tpl.leftBracket());
			sbBuildValueString.append(kernel.format(x, tpl));
			switch (tpl.getCoordStyle(kernel.getCoordStyle())) {
			case Kernel.COORD_STYLE_AUSTRIAN:
				tpl.appendOptionalSpace(sbBuildValueString);
				sbBuildValueString.append(tpl.getPointCoordBar());
				tpl.appendOptionalSpace(sbBuildValueString);
				break;
			default:
				tpl.getCommaOptionalSpace(sbBuildValueString, kernel.getLocalization());
			}
			sbBuildValueString.append(kernel.format(y, tpl));
			sbBuildValueString.append(tpl.rightBracket());
		}

	}

	/**
	 * interface VectorValue implementation
	 */
	@Override
	public GeoVec2D getVector() {
		GeoVec2D ret = new GeoVec2D(kernel, inhomX, inhomY);
		ret.setMode(getToStringMode());
		return ret;
	}

	/** POLAR or CARTESIAN */

	/**
	 * returns all class-specific xml tags for saveXML GeoGebra File Format
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		AlgoElement algo = getParentAlgorithm();
		if (algo instanceof AlgoPointOnPath) {
			// write parameter just for GeoCurveCartesian/GeoCurveCartesian3D
			// as curve may cross itself so just coords doesn't determine unique
			// pos
			if (((AlgoPointOnPath) algo)
					.getPath() instanceof GeoCurveCartesianND) {
				sb.append("\t<curveParam t=\"");
				sb.append(getPathParameter().t);
				sb.append("\"/>\n");
			}
		}

		// write x,y,z after <curveParam>
		super.getXMLtags(sb);
	}

	@Override
	protected void getStyleXML(StringBuilder sb) {
		super.getStyleXML(sb);
		// polar or cartesian coords
		switch (getToStringMode()) {
		case Kernel.COORD_POLAR:
			sb.append("\t<coordStyle style=\"polar\"/>\n");
			break;

		case Kernel.COORD_COMPLEX:
			sb.append("\t<coordStyle style=\"complex\"/>\n");
			break;

		case Kernel.COORD_CARTESIAN_3D:
			sb.append("\t<coordStyle style=\"cartesian3d\"/>\n");
			break;

		case Kernel.COORD_SPHERICAL:
			sb.append("\t<coordStyle style=\"spherical\"/>\n");
			break;

		default:
			// don't save default
			// sb.append("\t<coordStyle style=\"cartesian\"/>\n");
		}
		if (verticalIncrement != null) {
			XMLBuilder.appendVerticalIncrement(sb, verticalIncrement);
		}
		XMLBuilder.appendPointProperties(sb, this);
	}

	@Override
	public void appendStartPointXML(StringBuilder sb, boolean absPosition) {
		sb.append("\t<startPoint ");

		if (isAbsoluteStartPoint()) {
			sb.append("x=\"");
			sb.append(x);
			sb.append("\" y=\"");
			sb.append(y);
			sb.append("\" z=\"");
			sb.append(z);
			sb.append("\"");
		} else {
			sb.append("exp=\"");
			StringUtil.encodeXML(sb, getLabel(StringTemplate.xmlTemplate));
			sb.append("\"");
		}
		if (absPosition) {
			sb.append(" absolute=\"true\"");
		}
		sb.append("/>\n");
	}

	@Override
	final public boolean isAbsoluteStartPoint() {
		return isIndependent() && !isLabelSet();
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public boolean evaluatesToNonComplex2DVector() {
		return getToStringMode() != Kernel.COORD_COMPLEX;
	}

	@Override
	public boolean evaluatesToVectorNotPoint() {
		return false;
	}

	/**
	 * Calls super.update() and updateCascade() for all registered locateables.
	 */
	@Override
	public void update(boolean drag) {
		super.update(drag);

		// update all registered locatables (they have this point as start
		// point)
		if (locateableList != null) {
			GeoElement.updateCascadeLocation(locateableList, cons);
		}
	}

	protected static TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<>();
		}
		return tempSet;
	}

	@Override
	public LocateableList getLocateableList() {
		if (locateableList == null) {
			locateableList = new LocateableList(this);
		}
		return locateableList;
	}

	/**
	 * Tells Locateables that their start point is removed and calls
	 * super.remove()
	 */
	@Override
	public void doRemove() {
		if (locateableList != null) {
			locateableList.doRemove();
		}

		// TODO: remove this part because the path should be in incidenceList
		// already.
		if (path != null) {
			GeoElement geo = path.toGeoElement();
			if (geo.isGeoConic()) {
				((GeoConicND) geo).removePointOnConic(this); // GeoConicND
			}
		}

		// TODO: modify this using removeIncidence
		if (incidenceList != null) {
			incidenceList.remove(this);
			for (int i = 0; i < incidenceList.size(); ++i) {
				GeoElement geo = incidenceList.get(i);
				if (geo.isGeoConic()) {
					((GeoConicND) geo).removePointOnConic(this); // GeoConicND
				} else if (geo.isGeoLine()) {
					((GeoLineND) geo).removePointOnLine(this);
				}
			}
		}

		super.doRemove();
	}

	@Override
	public void setBasicVisualStyle(GeoElement geo) {
		super.setBasicVisualStyle(geo);
		if (geo.isGeoPoint()) {
			setPointSize(((GeoPointND) geo).getPointSize());
			pointStyle = ((GeoPointND) geo).getPointStyle();
		} else if (geo instanceof PointProperties) {
			setPointSize(((PointProperties) geo).getPointSize());
			setPointStyle(((PointProperties) geo).getPointStyle());
		}
	}

	@Override
	final public boolean isGeoPoint() {
		return true;
	}

	@Override
	public void showUndefinedInAlgebraView(boolean flag) {
		showUndefinedInAlgebraView = flag;
	}

	/**
	 * Returns a comparator for GeoPoint objects. (sorts on X coordinate) If
	 * equal, doesn't return zero (otherwise TreeSet deletes duplicates)
	 *
	 * @return comparator for GeoPoint objects.
	 */
	public static Comparator<GeoPoint> getComparatorX() {
		if (comparatorX == null) {
			comparatorX = new Comparator<GeoPoint>() {
				@Override
				public int compare(GeoPoint itemA, GeoPoint itemB) {

					double compX = itemA.inhomX - itemB.inhomX;

					if (DoubleUtil.isZero(compX)) {
						double compY = itemA.inhomY - itemB.inhomY;

						// if x-coords equal, sort on y-coords
						if (!DoubleUtil.isZero(compY)) {
							return compY < 0 ? -1 : +1;
						}

						// don't return 0 for equal objects, otherwise the
						// TreeSet deletes duplicates
						return itemA.getConstructionIndex() > itemB
								.getConstructionIndex() ? -1 : 1;
					}
					return compX < 0 ? -1 : +1;
				}
			};

		}

		return comparatorX;
	}

	// ///////////////////////////////////////////
	// REGION

	@Override
	final public boolean isPointInRegion() {
		return region != null;
	}

	@Override
	public boolean hasRegion() {
		return region != null;
	}

	@Override
	public Region getRegion() {
		return region;
	}

	/**
	 * @param a_region
	 *            region restricting this point
	 */
	@Override
	public void setRegion(Region a_region) {
		region = a_region;
	}

	@Override
	public void updateCoords2D() {
		x2D = x / z;
		y2D = y / z;
	}

	@Override
	public double getX2D() {
		return x2D;
	}

	@Override
	public double getY2D() {
		return y2D;
	}

	// may be used when 2D point is put on 3D path
	@Override
	public void updateCoordsFrom2D(boolean doPathOrRegion, CoordSys coordsys) {
		if (coordsys != null) {
			updateCoords2D();
			setCoords(coordsys.getPoint(getX2D(), getY2D()), doPathOrRegion);
		}

	}

	@Override
	public void updateCoordsFrom2D(boolean doPathOrRegion) {
		// 3D only
	}

	@Override
	public Coords getInhomCoords() {
		if (inhomCoords2D == null) {
			inhomCoords2D = new Coords(new double[] { inhomX, inhomY });
		} else {
			this.inhomCoords2D.set(1, inhomX);
			this.inhomCoords2D.set(2, inhomY);
		}
		return inhomCoords2D;
	}

	@Override
	public Coords getInhomCoordsInD(int dimension) {
		switch (dimension) {
		case 2:
			return getInhomCoords();
		case 3:
			return getInhomCoordsInD3();
		default:
			return null;
		}
	}

	@Override
	public Coords getInhomCoordsInD3() {
		if (inhomCoords3D == null) {
			inhomCoords3D = new Coords(new double[] { inhomX, inhomY, 0, 1 });
		} else {
			this.inhomCoords3D.set(1, inhomX);
			this.inhomCoords3D.set(2, inhomY);
		}
		return inhomCoords3D;
	}

	@Override
	public Coords getInhomCoordsInD2() {
		return getInhomCoords();
	}

	@Override
	public Coords getCoordsInD2IfInPlane(CoordSys coordSys) {

		if (setCoords2D(coordSys)) {
			return coords2D;
		}

		return null;
	}

	@Override
	public Coords getCoordsInD2IfInPlaneInRealCoords(CoordSys coordSys) {
		return getCoordsInD2IfInPlane(coordSys);
	}

	@Override
	public Coords getCoordsInD2(CoordSys coordSys) {

		setCoords2D(coordSys);
		return coords2D;
	}

	private boolean setCoords2D(CoordSys coordSys) {

		if (coords2D == null) {
			coords2D = new Coords(new double[] { x, y, z });
		}

		if (coordSys == null || coordSys == CoordSys.Identity3D
				|| coordSys == CoordSys.XOY) {
			coords2D.set(1, x / z);
			coords2D.set(2, y / z);
			coords2D.set(3, 1);
		} else { // this should happen only when we try to put a 2D point on a
					// 3D path (e.g. GeoConic3D)
					// matrix for projection
			if (tmpMatrix4x4 == null) {
				tmpMatrix4x4 = new CoordMatrix4x4();
			}
			tmpMatrix4x4.set(coordSys.getMatrixOrthonormal());
			if (tmpCoords == null) {
				tmpCoords = new Coords(4);
			}
			getCoordsInD3().projectPlaneInPlaneCoords(tmpMatrix4x4, tmpCoords);
			double w = tmpCoords.getW();
			coords2D.setX(tmpCoords.getX() / w);
			coords2D.setY(tmpCoords.getY() / w);
			coords2D.setZ(1);

			// check if point is included in the plane
			return DoubleUtil.isZero(tmpCoords.getZ());
		}

		return true;
	}

	@Override
	public Coords getCoordsInD2() {
		return getCoordsInD2(null);
	}

	@Override
	public Coords getCoordsInD3() {
		return new Coords(x, y, 0, z);
	}

	@Override
	public Coords getCoordsInD(int dimension) {
		switch (dimension) {
		case 2:
			return getCoordsInD2();
		case 3:
			return getCoordsInD3();
		default:
			return null;
		}
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	@Override
	public void matrixTransform(double a, double b, double c, double d) {

		Double x1 = a * x + b * y;
		Double y1 = c * x + d * y;

		setCoords(x1, y1, z);
	}

	// ////////////////////////////////////
	// 3D stuff
	// ////////////////////////////////////

	@Override
	public boolean hasDrawable3D() {
		return true;
	}

	@Override
	public Coords getLabelPosition() {
		/*
		 * Coords v = new Coords(4); v.set(getInhomCoordsInD3()); v.setW(1);
		 */
		return getInhomCoordsInD3();
	}

	@Override
	public void pointChanged(GeoPointND p) {
		pointChanged(p, x, y, z);
	}

	/**
	 * do pointChanged for (x,y,z) 2D coords
	 *
	 * @param p
	 *            point
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z inhom coord
	 */
	public static void pointChanged(GeoPointND p, double x, double y,
			double z) {
		p.setCoords2D(x, y, z);
		p.updateCoordsFrom2D(false, null);

		p.getPathParameter().setT(0);

	}

	@Override
	public void pathChanged(GeoPointND PI) {
		pointChanged(PI);
	}

	@Override
	public boolean isOnPath(GeoPointND PI, double eps) {
		return isEqual(PI);
	}

	@Override
	public double getMinParameter() {
		return 0;
	}

	@Override
	public double getMaxParameter() {
		return 0;
	}

	@Override
	public boolean isClosedPath() {
		return false;
	}

	@Override
	public PathMover createPathMover() {
		return null;
	}

	@Override
	public double getAnimationValue() {
		return animationValue;
	}

	@Override
	public void setAnimationValue(double val) {
		animationValue = val;
	}

	/**
	 * @param frameRate
	 *            frames per second
	 * @param p
	 *            animated point
	 * @param path
	 *            animation path
	 * @param parent
	 *            parent list for list animations
	 * @return whether the value of this number was changed
	 */
	static public GeoElementND doAnimationStep(double frameRate, GeoPointND p,
			Path path, GeoList parent) {
		PathParameter pp = p.getPathParameter();
		GeoElement geo = (GeoElement) p;

		// remember old value of parameter to decide whether update is necessary
		double oldValue = pp.t;

		// compute animation step based on speed and frame rates
		double intervalWidth = 1;
		double step = intervalWidth * geo.getAnimationSpeed()
				* geo.getAnimationDirection()
				/ (AnimationManager.STANDARD_ANIMATION_TIME * frameRate);

		// update animation value
		if (Double.isNaN(p.getAnimationValue())) {
			p.setAnimationValue(oldValue);
		}
		p.setAnimationValue(p.getAnimationValue() + step);

		// make sure we don't get outside our interval
		switch (geo.getAnimationType()) {
		case GeoElementND.ANIMATION_DECREASING:
		case GeoElementND.ANIMATION_INCREASING:
			// jump to other end of slider
			if (p.getAnimationValue() > 1) {
				p.setAnimationValue(p.getAnimationValue() - intervalWidth);
			} else if (p.getAnimationValue() < 0) {
				p.setAnimationValue(p.getAnimationValue() + intervalWidth);
			}
			break;

		case GeoElementND.ANIMATION_INCREASING_ONCE:
			// stop if outside range
			if (p.getAnimationValue() > 1) {
				p.setAnimationValue(1);
				geo.setAnimating(false);
			} else if (p.getAnimationValue() < 0) {
				p.setAnimationValue(0);
				geo.setAnimating(false);
			}
			break;

		case GeoElementND.ANIMATION_OSCILLATING:
		default:
			if (p.getAnimationValue() >= 1) {
				p.setAnimationValue(1);
				geo.changeAnimationDirection();
			} else if (p.getAnimationValue() <= 0) {
				p.setAnimationValue(0);
				geo.changeAnimationDirection();
			}
			break;
		}

		// change slider's value without changing animationValue
		pp.t = PathNormalizer.toParentPathParameter(p.getAnimationValue(),
				path.getMinParameter(), path.getMaxParameter());

		// return whether value of slider has changed
		if (pp.t != oldValue) {
			path.pathChanged(p);
			p.updateCoords();
			return p;
		}
		return null;
	}

	/**
	 * Performs the next automatic animation step for this numbers. This changes
	 * the value but will NOT call update() or updateCascade().
	 *
	 * @return whether the value of this number was changed
	 */
	@Override
	public synchronized GeoElementND doAnimationStep(double frameRate,
			GeoList parent) {

		return doAnimationStep(frameRate, this, path, parent);
	}

	// ///////////////////////////////////////
	// MOVING THE POINT (3D)
	// ///////////////////////////////////////

	@Override
	public void switchMoveMode(int mode) {
		// 3D only
	}

	@Override
	public int getMoveMode() {
		if (hasChangeableCoordParentNumbers()) {
			return MOVE_MODE_XY;
		}
		if (!isIndependent() || isLocked()) {
			return MOVE_MODE_NONE;
		} else if (isPointOnPath()) {
			return MOVE_MODE_Z;
		} else {
			return MOVE_MODE_XY;
		}
	}

	@Override
	final public boolean isCasEvaluableObject() {
		return true;
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		double x1 = a00 * x + a01 * y + a02 * z;
		double y1 = a10 * x + a11 * y + a12 * z;
		double z1 = a20 * x + a21 * y + a22 * z;
		setCoords(x1, y1, z1);

	}

	/**
	 * Remove path restricting this point
	 */
	@Override
	public void removePath() {
		path = null;
		pathParameter = null;
	}

	// needed for GeoPointND interface for 3D
	// can be called eg SetCoords(A, 1,2,0)
	@Override
	public void setCoords(double x, double y, double z, double w) {

		// ignore z-coord, take nearest 2D point
		setCoords(x, y, w);
	}

	@Override
	public void moveDependencies(GeoElement oldGeo) {
		if (oldGeo.isGeoPoint() && ((GeoPointND) oldGeo).hasLocateableList()) {

			locateableList = ((GeoPointND) oldGeo).getLocateableList();
			for (Locateable locPoint : locateableList) {
				for (int i = 0; i < locPoint.getStartPointCount(); i++) {
					if (locPoint.getStartPoint(i) == oldGeo) {
						locPoint.initStartPoint(this, i);
					}
				}
				locPoint.toGeoElement().updateRepaint();
			}
			((GeoPointND) oldGeo).setLocateableList(null);
		}
	}

	// for identifying incidence by construction
	// case by case.
	// currently implemented for
	// lines: line by two point, intersect lines, line/conic, point on line
	// TODO: parallel line, perpendicular line

	/**
	 * @return list of objects incident by construction
	 */
	@Override
	public ArrayList<GeoElement> getIncidenceList() {
		return incidenceList;
	}

	/**
	 * @param list
	 *            list of objects incident by construction
	 */
	public void setIncidenceList(ArrayList<GeoElement> list) {
		if (list == null) {
			incidenceList = new ArrayList<>();
		} else {
			incidenceList = new ArrayList<>(list);
		}
	}

	/**
	 * initialize incidenceList
	 */
	public void createIncidenceList() {
		incidenceList = new ArrayList<>();
	}

	/**
	 * add geo to incidenceList of this, and also add this to pointsOnConic
	 * (when geo is a conic) or to pointsOnLine (when geo is a line)
	 *
	 * @param geo
	 *            incident object
	 */
	@Override
	public void addIncidence(GeoElement geo, boolean isStartPoint) {
		if (incidenceList == null) {
			createIncidenceList();
		}
		if (!incidenceList.contains(geo)) {
			incidenceList.add(geo);
		}

		// GeoConicND, GeoLine, GeoPoint are the three types who have an
		// incidence list
		if (geo.isGeoConic()) {
			((GeoConicND) geo).addPointOnConic(this); // GeoConicND
		} else if (geo.isGeoLine() && !isStartPoint) {
			((GeoLineND) geo).addPointOnLine(this);
		}
	}

	/**
	 * @param geo
	 *            incident geo tobe removed
	 */
	@Override
	public final void removeIncidence(GeoElement geo) {
		if (incidenceList != null) {
			incidenceList.remove(geo);
		}

		if (geo.isGeoConic()) {
			((GeoConicND) geo).removePointOnConic(this);
		} else if (geo.isGeoLine()) {
			((GeoLineND) geo).removePointOnLine(this);
		}
	}

	@Override
	public boolean isRandomizable() {
		// if we drag a AlgoDynamicCoordinates, we want its point to be dragged
		AlgoElement algo = getParentAlgorithm();

		// make sure Point[circle, param] is not draggable
		// TODO Check if we really want this
		if (algo instanceof FixedPathRegionAlgo) {
			return ((FixedPathRegionAlgo) algo).isChangeable(this);
		}

		return isIndependent() || isPointOnPath() || isPointInRegion();
	}

	@Override
	public void randomizeForProbabilisticChecking() {
		setCoords(x + (Math.random() * 2 - 1) * z,
				y + (Math.random() * 2 - 1) * z, z);
	}

	@Override
	public void setParentAlgorithm(AlgoElement algorithm) {
		super.setParentAlgorithm(algorithm);
		if (algorithm != null) {
			setConstructionDefaults(); // set colors to dependent colors
		}
	}

	@Override
	public boolean movePoint(Coords rwTransVec, Coords endPosition) {
		boolean movedGeo = false;

		final GeoPoint point = this;
		if (endPosition != null) {
			point.setCoords(endPosition.getX(), endPosition.getY(), 1);
			movedGeo = true;
		}

		// translate point
		else {
			double x1 = point.getInhomX() + rwTransVec.getX();
			double y1 = point.getInhomY() + rwTransVec.getY();

			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			if (Math.abs(rwTransVec.getX()) > Kernel.MIN_PRECISION) {
				x1 = DoubleUtil.checkDecimalFraction(x1);
			}
			if (Math.abs(rwTransVec.getY()) > Kernel.MIN_PRECISION) {
				y1 = DoubleUtil.checkDecimalFraction(y1);
			}

			// set translated point coords
			point.setCoords(x1, y1, 1);
			movedGeo = true;
		}

		return movedGeo;
	}

	/**
	 * @param x
	 *            homegenous x-coord
	 */
	public void setX(double x) {
		this.x = x;

	}

	/**
	 * @param y
	 *            homogeneous y-coord
	 */
	public void setY(double y) {
		this.y = y;

	}

	/**
	 * @param z
	 *            homogeneous z-coord
	 */
	public void setZ(double z) {
		this.z = z;
	}

	// protected GeoList spreadsheetTraceList = null;
	// protected String[] spreadsheetColumnHeadings = null;

	@Override
	public void updateColumnHeadingsForTraceValues() {
		resetSpreadsheetColumnHeadings();

		spreadsheetColumnHeadings.add(getColumnHeadingText(new ExpressionNode(
				kernel, kernel.getAlgebraProcessor().getXBracket(), // "x("
				Operation.PLUS,
				new ExpressionNode(kernel, getNameGeo(), // Name[this]
						Operation.PLUS,
						kernel.getAlgebraProcessor().getCloseBracket())))); // ")"
		spreadsheetColumnHeadings.add(getColumnHeadingText(new ExpressionNode(
				kernel, kernel.getAlgebraProcessor().getYBracket(), // "y("
				Operation.PLUS,
				new ExpressionNode(kernel, getNameGeo(), // Name[this]
						Operation.PLUS,
						kernel.getAlgebraProcessor().getCloseBracket())))); // ")"

	}

	@Override
	public TraceModesEnum getTraceModes() {
		return TraceModesEnum.SEVERAL_VALUES_OR_COPY;
	}

	@Override
	public String getTraceDialogAsValues() {
		String name = getLabelTextOrHTML(false);

		StringBuilder sb = new StringBuilder();
		sb.append("x(");
		sb.append(name);
		sb.append("), y(");
		sb.append(name);
		sb.append(")");

		return sb.toString();
	}

	@Override
	public void addToSpreadsheetTraceList(
			ArrayList<GeoNumeric> spreadsheetTraceList) {
		GeoNumeric xx = new GeoNumeric(cons, inhomX);
		spreadsheetTraceList.add(xx);
		if (isPolar()) {
			GeoAngle yy = new GeoAngle(cons, inhomY);
			spreadsheetTraceList.add(yy);
		} else {
			GeoNumeric yy = new GeoNumeric(cons, inhomY);
			spreadsheetTraceList.add(yy);
		}

	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		return new SymbolicParameters(this);
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {

		// if this is a free point
		if (algoParent == null) {
			if (variableCoordinate1 == null) {
				variableCoordinate1 = new PVariable(this);
			}
			if (variableCoordinate2 == null) {
				variableCoordinate2 = new PVariable(this);
			}
			variableCoordinate1.setTwin(variableCoordinate2);
			variableCoordinate2.setTwin(variableCoordinate1);

			variables.add(variableCoordinate1);
			variables.add(variableCoordinate2);
			return;
		}
		if (algoParent instanceof SymbolicParametersAlgo) {
			((SymbolicParametersAlgo) algoParent).getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if (algoParent == null) {
			GeoElement[] fixedElements = a.getFixedPoints();
			if (fixedElements != null) {
				boolean isContained = false;
				for (GeoElement ge : fixedElements) {
					if (ge.equals(this)) {
						isContained = true;
					}
				}
				if (isContained) {
					int[] result = { 0, 0, 0 };
					return result;
				}
			}

			int[] result = { 1, 1, 0 };
			return result;
		}
		if (algoParent instanceof SymbolicParametersAlgo) {
			return ((SymbolicParametersAlgo) algoParent).getDegrees(a);
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			final HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (algoParent == null) {
			BigInteger[] result = new BigInteger[3];
			result[0] = values.get(variableCoordinate1);
			result[1] = values.get(variableCoordinate2);
			result[2] = BigInteger.ONE;
			if (result[0] == null || result[1] == null) {
				throw new NoSymbolicParametersException();
			}
			return result;
		}
		if (algoParent instanceof SymbolicParametersAlgo) {
			return ((SymbolicParametersAlgo) algoParent)
					.getExactCoordinates(values);
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
		// if this is a free point
		if (algoParent == null) {
			if (variableCoordinate1 == null) {
				variableCoordinate1 = new PVariable(this);
			}
			if (variableCoordinate2 == null) {
				variableCoordinate2 = new PVariable(this);
			}
			PPolynomial[] ret = { new PPolynomial(variableCoordinate1),
					new PPolynomial(variableCoordinate2), new PPolynomial(1) };
			return ret;
		}
		if (algoParent instanceof SymbolicParametersAlgo) {
			return ((SymbolicParametersAlgo) algoParent).getPolynomials();
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PVariable[] getBotanaVars(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent)
					.getBotanaVars(this);
		}

		if (algoParent == null) {
			if (botanaVars == null) {
				botanaVars = new PVariable[2];
				botanaVars[0] = new PVariable(kernel); // ,true
				botanaVars[1] = new PVariable(kernel); // ,true
				Log.trace("Free point " + geo.getLabelSimple() + "("
						+ botanaVars[0] + "," + botanaVars[1] + ")");
			}
			return botanaVars;
		}
		Log.debug("There is no implementation yet for " + algoParent);
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent)
					.getBotanaPolynomials(this);
		}
		return null; // Here maybe an exception should be thrown...?
	}

	@Override
	public int getDimension() {
		return 2;
	}

	@Override
	public double distanceToPath(PathOrPoint path1) {
		return path1.toGeoElement().distance(this);
	}

	@Override
	public boolean hasLocateableList() {
		return locateableList != null;
	}

	@Override
	public void setLocateableList(LocateableList locateableList) {
		this.locateableList = locateableList;
	}

	@Override
	public void setCoordsFromPoint(GeoPointND point) {
		if (point instanceof GeoPoint) {
			setCoords((GeoPoint) point);
		} else {
			setCoords(point.getCoordsInD2(), true);
		}
	}

	@Override
	public void set(double param1, double param2, MyPoint leftPoint,
			MyPoint rightPoint) {
		x = param2 * leftPoint.x + param1 * rightPoint.x;
		y = param2 * leftPoint.y + param1 * rightPoint.y;
		z = 1.0;
	}

	@Override
	public ValueType getValueType() {
		return getToStringMode() == Kernel.COORD_COMPLEX ? ValueType.COMPLEX
				: ValueType.NONCOMPLEX2D;
	}

	@Override
	public ExtendedBoolean isCongruent(GeoElement geo) {
		return ExtendedBoolean.newExtendedBoolean(geo.isGeoPoint());
	}

	@Override
	public ValidExpression toValidExpression() {
		return getVector();
	}

	@Override
	public double[] getPointAsDouble() {
		return new double[] { inhomX, inhomY, 0 };
	}

	/**
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param C
	 *            third point
	 * @param wA
	 *            weight of A
	 * @param wB
	 *            weight of B
	 * @param wC
	 *            weight of C
	 * @param w
	 *            value of wA+wB+wC
	 * @param M
	 *            output
	 */
	public static void setBarycentric(GeoPointND A, GeoPointND B, GeoPointND C,
			double wA, double wB, double wC, double w, GeoPointND M) {
		Coords cA = A.getInhomCoordsInD3();
		Coords cB = B.getInhomCoordsInD3();
		Coords cC = C.getInhomCoordsInD3();
		Coords cM = cA.copy().mulInside(wA / w).addInsideMul(cB, wB / w)
				.addInsideMul(cC, wC / w);
		M.setCoords(cM, false);

	}

	@Override
	public void setChangeableParentIfNull(ChangeableParent ccp) {
		// used for GeoPoint3D
	}

	@Override
	public double getZScale() {
		return 1;
	}

	@Override
	public boolean showToolTipText() {
		// do not show tooltip for preview points of function
		if (!isLabelSet()) {
			return false;
		}
		return super.showToolTipText();
	}

	@Override
	public void addAuralOperations(Localization loc, ScreenReaderBuilder sb) {
		addAuralArrows(loc, sb, this);
		super.addAuralOperations(loc, sb);
	}

	/**
	 * @param loc
	 *            localization
	 * @param sb
	 *            string builder
	 * @param point
	 *            point
	 */
	public static void addAuralArrows(Localization loc, ScreenReaderBuilder sb,
			GeoPointND point) {
		if (point.isPointOnPath()) {
			sb.append(loc.getMenuDefault("PressPlusMinusToMove",
					"Press plus and minus to move the object"));
		} else if (point.isIndependent() || point.isPointInRegion()) {
			String moveFree = "Press the arrow keys to move the object";
			if (point.isGeoElement3D()) {
				sb.append(loc.getMenuDefault("PressArrowsPageUpToMove",
						moveFree + " in x and y, Page Up, Page Down to move in z"));
			} else {
				sb.append(loc.getMenuDefault("PressArrowsToMove",
						moveFree));
			}
		}
		sb.endSentence();
	}

	@Override
	public void addAuralValue(ScreenReaderBuilder sb) {
		String valueString = toValueString(getApp().getScreenReaderTemplate());
		String converted = ScreenReader.convertToReadable("=" + valueString, app);
		sb.appendDegreeIfNeeded(this, converted);
	}

	@Override
	public String getAuralTextForMove() {
		return pointMovedAural(kernel.getLocalization(), this);
	}

	/**
	 * @param loc
	 *            localization
	 * @param geoPoint
	 *            point
	 * @return description of new position
	 */
	public static String pointMovedAural(Localization loc,
			GeoPointND geoPoint) {

		ScreenReaderBuilder sb = new ScreenReaderBuilder(loc);
		if (!geoPoint.addAuralCaption(sb)) {
			geoPoint.addAuralLabel(sb);
		} else {
			return sb.toString();
		}
		ScreenReaderBuilder sbWithValue = new ScreenReaderBuilder(loc);
		sbWithValue.appendDegreeIfNeeded(geoPoint,
				geoPoint.toValueString(geoPoint.getApp().getScreenReaderTemplate()));
		return loc.getPlainDefault("PointAMovedToB", "Point %0 moved to %1",
				sb.toString(),
				sbWithValue.toString());
	}

	@Override
	public void setRegionChanged(double x, double y) {
		this.x = x;
		this.y = y;
		this.z = 1;
	}

	@Override
	public void pointChanged(GeoPolygon polygon) {
		Coords coords = getCoordsInD2();
		double qx = coords.getX() / coords.getZ();
		double qy = coords.getY() / coords.getZ();

		double minDist = Double.POSITIVE_INFINITY;
		double resx = 0, resy = 0, resz = 0, param = 0;

		// find closest point on each segment
		PathParameter pp = getPathParameter();

		for (int i = 0; i < polygon.getSegmentLength(); i++) {
			setCoords2D(qx, qy, 1);
			updateCoordsFrom2D(false);
			polygon.getSegment(i).pointChanged(this);
			coords = getCoordsInD2();
			double x1 = coords.getX() / coords.getZ() - qx;
			double y1 = coords.getY() / coords.getZ() - qy;
			double dist = x1 * x1 + y1 * y1;
			if (dist < minDist) {
				minDist = dist;
				// remember closest point
				resx = coords.getX();
				resy = coords.getY();
				resz = coords.getZ();
				param = i + pp.t;
			}
		}

		setCoords2D(resx, resy, resz);
		updateCoordsFrom2D(false);
		pp.t = param;
	}

	@Override
	public NumberValue getVerticalIncrement() {
		return this.verticalIncrement;
	}

	@Override
	public void setVerticalIncrement(NumberValue step) {
		this.verticalIncrement = step;
	}

	@Override
	protected boolean isCommandOutput() {
		if (algoParent != null
				&& (algoParent.getClass() == AlgoPointOnPath.class
				|| algoParent.getClass() == AlgoPointInRegion.class)) {
			return false;
		}
		return super.isCommandOutput();
	}
}
