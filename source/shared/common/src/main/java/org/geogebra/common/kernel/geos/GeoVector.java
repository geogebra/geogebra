/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoVector.java
 *
 * The vector (x,y) has homogeneous coordinates (x,y,0)
 *
 * Created on 30. August 2001, 17:39
 */

package org.geogebra.common.kernel.geos;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.SymbolicParameters;
import org.geogebra.common.kernel.algos.SymbolicParametersAlgo;
import org.geogebra.common.kernel.algos.SymbolicParametersBotanaAlgo;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.kernel.prover.AbstractProverReciosMethod;
import org.geogebra.common.kernel.prover.NoSymbolicParametersException;
import org.geogebra.common.kernel.prover.polynomial.PPolynomial;
import org.geogebra.common.kernel.prover.polynomial.PVariable;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 *
 * @author Markus
 */
final public class GeoVector extends GeoVec3D implements Path, VectorValue,
		Translateable, Mirrorable, Dilateable,
		MatrixTransformable, Transformable, GeoVectorND, SpreadsheetTraceable,
		SymbolicParametersAlgo, SymbolicParametersBotanaAlgo, HasHeadStyle {

	private GeoPointND startPoint;

	// for path interface we use a segment
	private GeoSegment pathSegment;
	private GeoPoint pathStartPoint;
	private GeoPoint pathEndPoint;

	private VectorHeadStyle headStyle = VectorHeadStyle.DEFAULT;
	private boolean waitingForStartPoint = false;
	private HashSet<GeoPointND> waitingPointSet;

	private final StringBuilder sbToString = new StringBuilder(50);
	private final StringBuilder sbBuildValueString = new StringBuilder(50);
	private StringBuilder sb;
	private @CheckForNull VectorToMatrix converter;

	/**
	 * Creates new GeoVector
	 * 
	 * @param c
	 *            construction
	 */
	public GeoVector(Construction c) {
		super(c);
		setConstructionDefaults();
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.VECTOR;
	}

	@Override
	public boolean isCasEvaluableObject() {
		return true;
	}

	/**
	 * Creates new GeoVector
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public GeoVector(Construction c, String label, double x, double y,
			double z) {
		super(c, x, y, z); // GeoVec3D constructor
		setConstructionDefaults();
		setLabel(label);
	}

	/**
	 * Copy constructor
	 * 
	 * @param vector
	 *            vector to copy
	 */
	public GeoVector(GeoVector vector) {
		this(vector.cons);
		set(vector);
	}

	@Override
	public void setCoords(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		setDefinition(null);
	}

	@Override
	public void setCoords(double[] c) {
		setCoords(c[0], c[1], c[2]);
	}

	@Override
	public void setCoords(GeoVec3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
		setDefinition(null);
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo.isGeoPoint()) {
			GeoPointND p = (GeoPointND) geo;
			double[] coords = p.getCoordsInD3().get();
			if (DoubleUtil.isZero(coords[2])) {
				setCoords(coords);
			} else {
				setUndefined();
			}
		} else {
			super.set(geo);
		}

		if (!geo.isGeoVector()) {
			return;
		}

		GeoVector vec = (GeoVector) geo;

		// don't set start point for macro output
		// see AlgoMacro.initRay()
		if (geo.getConstruction() != cons && isAlgoMacroOutput()) {
			return;
		}

		try {
			if (vec.startPoint != null) {
				if (vec.hasStaticLocation()) {
					// create new location point
					setStartPoint(vec.startPoint.copy());
				} else {
					// take existing location point
					setStartPoint(vec.startPoint);
				}
			}
		} catch (CircularDefinitionException e) {
			Log.debug("set GeoVector: CircularDefinitionException");
		}
	}

	@Override
	public GeoElement copy() {
		return new GeoVector(this);
	}

	/**
	 * @param r
	 *            radius
	 * @param phi
	 *            phase
	 */
	public void setPolarCoords(double r, double phi) {
		// convert angle to radiant
		x = r * Math.cos(phi);
		y = r * Math.sin(phi);
		z = 0.0d;
	}

	/**
	 * Sets coords to (x,y,0)
	 * 
	 * @param v
	 *            vector (x,y)
	 */
	public void setCoords(GeoVec2D v) {
		x = v.getX();
		y = v.getY();
		z = 0.0d;
	}

	/**
	 * Converts the homogeneous coordinates (x,y,z) of this GeoVec3D to the
	 * inhomogeneous coordinates (x/z, y/z) of a new GeoVec2D.
	 * 
	 * @return vector containing inhomogeneous coords
	 */
	public GeoVec2D getInhomVec() {
		return new GeoVec2D(kernel, x, y);
	}

	/**
	 * Returns starting point of this vector or null.
	 */
	@Override
	public GeoPointND getStartPoint() {
		return startPoint;
	}

	@Override
	public boolean hasStaticLocation() {
		return startPoint == null || startPoint.isAbsoluteStartPoint();
	}

	@Override
	public void setStartPoint(GeoPointND p, int number)
			throws CircularDefinitionException {
		setStartPoint(p);
	}

	/**
	 * Sets the startpoint without performing any checks. This is needed for
	 * macros.
	 */
	@Override
	public void initStartPoint(GeoPointND p, int number) {
		startPoint = p;
	}

	@Override
	public void removeStartPoint(GeoPointND p) {
		if (startPoint == p) {
			try {
				setStartPoint(null);
			} catch (Exception e) {
				// ignore circular definition here
			}
		}
	}

	@Override
	public void setStartPoint(GeoPointND p) throws CircularDefinitionException {

		if (startPoint == p) {
			return;
		}

		// macro output uses initStartPoint() only
		if (isAlgoMacroOutput()) {
			return;
		}

		// check for circular definition
		if (isParentOf(p)) {
			Log.debug(this + " startpoint " + p);
			// throw new CircularDefinitionException();
		}

		// remove old dependencies
		if (startPoint != null) {
			startPoint.getLocateableList().unregisterLocateable(this);
		}

		// set new location
		startPoint = p;

		// add new dependencies
		if (startPoint != null) {
			startPoint.getLocateableList().registerLocateable(this);
		}

		// reinit path
		if (pathSegment != null) {
			initPathSegment();
		}

		// update the waiting points
		if (waitingForStartPoint) {
			waitingForStartPoint = false;

			if (waitingPointSet != null) {
				updatePathSegment();

				GeoPoint P;
				for (GeoPointND geoPointND : waitingPointSet) {
					P = (GeoPoint) geoPointND;
					pathSegment.pointChanged(P);
					P.updateCoords();
				}
			}
			waitingPointSet = null;
		}
	}

	@Override
	public void setWaitForStartPoint() {
		// the startpoint should not be used as long
		// as waitingForStartPoint is true
		// This is important for points on this vector:
		// their coords should not be changed until
		// the startPoint was finally set
		waitingForStartPoint = true;
	}

	@Override
	public void doRemove() {
		super.doRemove();
		// tell startPoint
		if (startPoint != null) {
			startPoint.getLocateableList().unregisterLocateable(this);
		}
	}

	@Override
	public boolean isFinite() {
		return !isInfinite();
	}

	@Override
	public boolean isInfinite() {
		return Double.isInfinite(x) || Double.isInfinite(y);
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined() && !isInfinite();
	}

	/**
	 * Yields true if the coordinates of this vector are equal to those of
	 * vector v. Infinite points are checked for linear dependency.
	 */
	// Michael Borcherds 2008-05-01
	@Override
	public ExtendedBoolean isEqualExtended(GeoElementND geo) {

		if (!geo.isGeoVector()) {
			return ExtendedBoolean.FALSE;
		}

		GeoVector v = (GeoVector) geo;

		if (!(isFinite() && v.isFinite())) {
			return ExtendedBoolean.FALSE;
		}
		return ExtendedBoolean.newExtendedBoolean(DoubleUtil.isEqual(x, v.x)
				&& DoubleUtil.isEqual(y, v.y));
	}

	/**
	 * rotate this vector by angle phi around (0,0)
	 */
	@Override
	public void rotate(NumberValue phi) {
		rotateXY(phi);
	}

	/**
	 * Called when transforming Ray[point,direction] -- doesn't do anything.
	 */
	@Override
	public void translate(Coords v) {
		// do nothing
	}

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		rotateXY(r);
	}

	@Override
	public void mirror(Coords Q) {
		setCoords(-x, -y, z);
	}

	@Override
	public void mirror(GeoLineND g1) {
		GeoLine g = (GeoLine) g1;
		mirrorXY(2.0 * Math.atan2(-g.getX(), g.getY()));

	}

	@Override
	public void dilate(NumberValue rval, Coords S) {
		double r = rval.getDouble();
		setCoords(r * x, r * y, z);
	}

	@Override
	public void matrixTransform(double a, double b, double c, double d) {
		double x1 = a * x + b * y;
		double y1 = c * x + d * y;
		setCoords(x1, y1, z);
	}

	/*********************************************************************/

	@Override
	public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(label);

		switch (tpl.getCoordStyle(kernel.getCoordStyle())) {
		case Kernel.COORD_STYLE_FRENCH:
			// no equal sign
			sbToString.append(": ");
			break;

		case Kernel.COORD_STYLE_AUSTRIAN:
			// no equal sign
			break;

		default:
			sbToString.append(" = ");
		}

		// Without toString, there was an InvocationTargetException here
		String str = buildValueString(tpl).toString();
		sbToString.append(str);

		return sbToString.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (tpl.isDisplayStyle()) {
			return toLaTeXString(false, tpl);
		}
		return buildValueString(tpl).toString();
	}

	@Override
	public String toValueStringAsColumnVector(StringTemplate tpl) {
		return buildColumnVectorValueString(tpl);
	}

	private StringBuilder buildValueString(StringTemplate tpl) {
		sbBuildValueString.setLength(0);

		if (tpl.getStringType() == ExpressionNodeConstants.StringType.GIAC) {
			sbBuildValueString.append("ggbvect[");
			sbBuildValueString.append(kernel.format(getInhomVec().getX(), tpl));
			sbBuildValueString.append(',');
			sbBuildValueString.append(kernel.format(getInhomVec().getY(), tpl));
			sbBuildValueString.append("]");
			return sbBuildValueString;
		}
		switch (getToStringMode()) {
		case Kernel.COORD_POLAR:
			sbBuildValueString.append("(");
			sbBuildValueString.append(kernel.format(MyMath.length(x, y), tpl));
			sbBuildValueString.append("; ");
			sbBuildValueString
					.append(kernel.formatAngle(Math.atan2(y, x), tpl, false));
			sbBuildValueString.append(")");
			break;

		case Kernel.COORD_COMPLEX:
			sbBuildValueString.append(kernel.format(x, tpl));
			sbBuildValueString.append(" ");
			kernel.formatSigned(y, sbBuildValueString, tpl);
			sbBuildValueString.append(Unicode.IMAGINARY);
			break;

		case Kernel.COORD_CARTESIAN_3D:
			GeoPoint.buildValueStringCoordCartesian3D(kernel, tpl, x, y, 0,
					sbBuildValueString);
			break;

		case Kernel.COORD_SPHERICAL:
			GeoPoint.buildValueStringCoordSpherical(kernel, tpl, x, y, 0,
					sbBuildValueString);
			break;

		default: // CARTESIAN
			sbBuildValueString.append("(");
			sbBuildValueString.append(kernel.format(x, tpl));
			if (tpl.getCoordStyle(kernel.getCoordStyle()) == Kernel.COORD_STYLE_AUSTRIAN) {
				sbBuildValueString.append(" | ");
			} else {
				sbBuildValueString.append(", ");
			}
			sbBuildValueString.append(kernel.format(y, tpl));
			sbBuildValueString.append(")");
			break;
		}
		return sbBuildValueString;
	}

	private String buildColumnVectorValueString(StringTemplate tpl) {
		if (getToStringMode() != Kernel.COORD_CARTESIAN) {
			return buildValueString(tpl).toString();
		}

		return getConverter().build(tpl, getDefinition(), getX(), getY());
	}

	private VectorToMatrix getConverter() {
		if (converter == null) {
			converter = new VectorToMatrix(kernel);
		}
		return converter;
	}

	/**
	 * interface VectorValue implementation
	 */
	@Override
	public GeoVec2D getVector() {
		GeoVec2D ret = new GeoVec2D(kernel, x, y);
		ret.setMode(getToStringMode());
		return ret;
	}

	@Override
	public double[] getPointAsDouble() {
		return new double[] { x, y, 0 };
	}

	/**
	 * returns class-specific style xml tags for saveXML
	 */
	@Override
	protected void getStyleXML(StringBuilder xmlsb) {
		super.getStyleXML(xmlsb);
		// line thickness and type
		getLineStyleXML(xmlsb);

		// polar or cartesian coords
		switch (getToStringMode()) {
		case Kernel.COORD_POLAR:
			xmlsb.append("\t<coordStyle style=\"polar\"/>\n");
			break;

		case Kernel.COORD_COMPLEX:
			xmlsb.append("\t<coordStyle style=\"complex\"/>\n");
			break;

		case Kernel.COORD_CARTESIAN_3D:
			xmlsb.append("\t<coordStyle style=\"cartesian3d\"/>\n");
			break;

		case Kernel.COORD_SPHERICAL:
			xmlsb.append("\t<coordStyle style=\"spherical\"/>\n");
			break;

		default:
			xmlsb.append("\t<coordStyle style=\"cartesian\"/>\n");
		}

		if (getHeadStyle() != VectorHeadStyle.DEFAULT) {
			getHeadStyleXML(xmlsb);
		}

		// startPoint of vector
		if (startPoint != null) {
			startPoint.appendStartPointXML(xmlsb, false);
		}
	}

	private void getHeadStyleXML(StringBuilder xmlsb) {
		xmlsb.append("\t<headStyle val=\"");
		xmlsb.append(getHeadStyle().ordinal());
		xmlsb.append("\"/>");
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public boolean evaluatesToNonComplex2DVector() {
		return this.getToStringMode() != Kernel.COORD_COMPLEX;
	}

	@Override
	public boolean evaluatesToVectorNotPoint() {
		return this.getToStringMode() != Kernel.COORD_COMPLEX;
	}

	/*
	 * Path interface
	 */

	@Override
	public boolean isClosedPath() {
		return false;
	}

	@Override
	public void pointChanged(GeoPointND P) {
		if (startPoint == null && waitingForStartPoint) {
			// remember waiting points
			if (waitingPointSet == null) {
				waitingPointSet = new HashSet<>();
			}
			waitingPointSet.add(P);
			return;
		}

		if (pathSegment == null) {
			updatePathSegment();
		}
		pathSegment.pointChanged(P);
	}

	@Override
	public void pathChanged(GeoPointND P) {

		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)) {
			pointChanged(P);
			return;
		}

		updatePathSegment();
		pathSegment.pathChanged(P);
	}

	@Override
	public boolean isOnPath(GeoPointND P, double eps) {
		updatePathSegment(); // Michael Borcherds 2008-06-10 bugfix
		return pathSegment.isOnPath(P, eps);
	}

	@Override
	public boolean isPath() {
		return true;
	}

	@Override
	public double getMinParameter() {
		return 0;
	}

	@Override
	public double getMaxParameter() {
		return 1;
	}

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	private void initPathSegment() {
		if (startPoint != null && !startPoint.isGeoElement3D()) { // TODO 3D
																	// case
			pathStartPoint = (GeoPoint) startPoint;
		} else {
			pathStartPoint = new GeoPoint(cons);
			pathStartPoint.setCoords(0, 0, 1);
		}

		pathEndPoint = new GeoPoint(cons);
		pathSegment = new GeoSegment(cons, pathStartPoint, pathEndPoint);
	}

	private void updatePathSegment() {
		if (pathSegment == null) {
			initPathSegment();
		}

		// update segment
		pathEndPoint.setCoords(pathStartPoint.inhomX + x,
				pathStartPoint.inhomY + y, 1.0);

		GeoVec3D.lineThroughPoints(pathStartPoint, pathEndPoint, pathSegment);
		// length is used in GeoSegment.pointChanged() and
		// GeoSegment.pathChanged()
		pathSegment.calcLength();
	}

	@Override
	public boolean isGeoVector() {
		return true;
	}

	@Override
	public boolean isAlwaysFixed() {
		return false;
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
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
	 * @param vector
	 *            the vector
	 * @param symbolic
	 *            if symbolic
	 */
	public static void buildLatexValueStringCoordCartesian3D(
			Kernel kernel, StringTemplate tpl, double x, double y, double z,
			StringBuilder sb, GeoVectorND vector, boolean symbolic) {
		if (symbolic && vector.getDefinition() != null) {
			sb.append(getColumnLaTeXfromExpression(vector.getDefinition(), tpl));
		} else {
			String[] inputs = new String[3];
			inputs[0] = kernel.format(x, tpl);
			inputs[1] = kernel.format(y, tpl);
			inputs[2] = kernel.format(z, tpl);
			buildTabular(inputs, sb);
		}
	}

	private static String buildTabular(String[] inputs, StringBuilder sb) {
		boolean alignOnDecimalPoint = true;
		for (String s : inputs) {
			if (s.indexOf('.') == -1) {
				alignOnDecimalPoint = false;
				break;
			}
		}

		sb.append("\\left( \\begin{align}");
		if (alignOnDecimalPoint) {
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = inputs[i].replace(".", "\\hspace{-0.2em} &.");
			}
		}

		for (String input : inputs) {
			sb.append(input);
			sb.append(" \\\\ ");
		}

		sb.append("\\end{align} \\right)");
		return sb.toString();
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		resetStringBuilder();
		return buildLatexString(kernel, sb, symbolic, tpl, getToStringMode(), x,
				y,
				this);
	}

	private void resetStringBuilder() {
		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param sb
	 *            builder
	 * @param symbolic
	 *            whether to replace variables
	 * @param tpl
	 *            template
	 * @param toStringMode
	 *            COORD_POLAR / COORD_CARTESIAN etc.
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param vector
	 *            vector corresponding to x,y (result depends on parent algo)
	 * @return content of string builder
	 */
	static public String buildLatexString(Kernel kernel, StringBuilder sb,
			boolean symbolic, StringTemplate tpl, int toStringMode, double x,
			double y, GeoVectorND vector) {
		if (!symbolic && !vector.isDefined()) {
			sb.append("?");
			return sb.toString();
		}
		switch (toStringMode) {
		case Kernel.COORD_POLAR:
			sb.append("(");
			sb.append(kernel.format(MyMath.length(x, y), tpl));
			sb.append("; ");
			sb.append(kernel.formatAngle(Math.atan2(y, x), tpl, false));
			sb.append(")");
			break;

		case Kernel.COORD_COMPLEX:
			sb.append(kernel.format(x, tpl));
			sb.append(" ");
			kernel.formatSigned(y, sb, tpl);
			sb.append(Unicode.IMAGINARY);
			break;

		case Kernel.COORD_CARTESIAN_3D:
			buildLatexValueStringCoordCartesian3D(kernel, tpl, x, y, 0, sb,
					vector, symbolic);
			break;

		case Kernel.COORD_SPHERICAL:
			GeoPoint.buildValueStringCoordSpherical(kernel, tpl, x, y, 0, sb);
			break;

		default: // CARTESIAN

			ExpressionNode definition = vector.getDefinition();
			if (symbolic && definition != null) {
				return getColumnLaTeXfromExpression(definition, tpl);
			}
			String[] inputs = new String[2];
			inputs[0] = kernel.format(x, tpl);
			inputs[1] = kernel.format(y, tpl);
			return buildTabular(inputs, sb);
		}

		return sb.toString();
	}

	private static String getColumnLaTeXfromExpression(ExpressionNode definition,
			StringTemplate tpl) {
		ExpressionValue ev = definition.unwrap();
		// need to do something different for (xx,yy) and a (1,2) + c
		if (ev instanceof MyVecNDNode) {
			MyVecNDNode vn = (MyVecNDNode) ev;
			String[] inputs = new String[vn.getDimension()];
			inputs[0] = vn.getX().toString(tpl);
			inputs[1] = vn.getY().toString(tpl);
			if (vn.getDimension() > 2) {
				inputs[2] = vn.getZ().toString(tpl);
			}
			return buildTabular(inputs, new StringBuilder());
		}
		return definition.toString(tpl);
	}

	@Override
	public Coords getCoordsInD2() {
		Coords ret = new Coords(3);
		ret.setX(getX());
		ret.setY(getY());
		ret.setZ(getZ());
		return ret;
	}

	@Override
	public Coords getCoordsInD3() {
		Coords ret = new Coords(4);
		ret.setX(getX());
		ret.setY(getY());
		ret.setZ(getZ());
		return ret;

	}

	@Override
	public boolean hasDrawable3D() {
		return true;
	}

	// only used for 3D
	@Override
	public void updateStartPointPosition() {
		// 3D only
	}

	@Override
	public Coords getDirectionInD3() {
		return getCoordsInD3();
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {
		double x1 = a00 * x + a01 * y + a02 * 1;
		double y1 = a10 * x + a11 * y + a12 * 1;
		double z1 = a20 * x + a21 * y + a22 * 1;
		setCoords(x1 / z1, y1 / z1, 0);

	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return true;
	}

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

		return "x(" + name + "), y(" + name + ")";
	}

	@Override
	public void addToSpreadsheetTraceList(
			ArrayList<GeoNumeric> spreadsheetTraceList) {
		GeoNumeric xx = new GeoNumeric(cons, getInhomVec().getX());
		spreadsheetTraceList.add(xx);
		GeoNumeric yy = new GeoNumeric(cons, getInhomVec().getY());
		spreadsheetTraceList.add(yy);
	}

	@Override
	public SymbolicParameters getSymbolicParameters() {
		if (algoParent instanceof SymbolicParametersAlgo) {
			return new SymbolicParameters((SymbolicParametersAlgo) algoParent);
		}
		return null;
	}

	@Override
	public void getFreeVariables(HashSet<PVariable> variables)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersAlgo) {
			((SymbolicParametersAlgo) algoParent).getFreeVariables(variables);
			return;
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public int[] getDegrees(AbstractProverReciosMethod a)
			throws NoSymbolicParametersException {
		if (algoParent != null
				&& (algoParent instanceof SymbolicParametersAlgo)) {
			return ((SymbolicParametersAlgo) algoParent).getDegrees(a);
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public BigInteger[] getExactCoordinates(
			final HashMap<PVariable, BigInteger> values)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersAlgo) {
			return ((SymbolicParametersAlgo) algoParent)
					.getExactCoordinates(values);
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public PPolynomial[] getPolynomials() throws NoSymbolicParametersException {
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
		return null;
	}

	@Override
	public PPolynomial[] getBotanaPolynomials(GeoElementND geo)
			throws NoSymbolicParametersException {
		if (algoParent instanceof SymbolicParametersBotanaAlgo) {
			return ((SymbolicParametersBotanaAlgo) algoParent)
					.getBotanaPolynomials(this);
		}
		throw new NoSymbolicParametersException();
	}

	@Override
	public double[] getInhomCoords() {
		double[] ret = new double[2];
		ret[0] = getX();
		ret[1] = getY();
		return ret;
	}

	@Override
	public void updateLocation() {
		updateGeo(false);
		kernel.notifyUpdateLocation(this);
	}

	@Override
	public ValueType getValueType() {
		return getToStringMode() == Kernel.COORD_COMPLEX ? ValueType.COMPLEX
				: ValueType.NONCOMPLEX2D;
	}

	@Override
	public boolean hasLineOpacity() {
		return true;
	}

	@Override
	public int getDimension() {
		return 2;
	}

	@Override
	public void setCoords(double x, double y, double z, double w) {
		setCoords(x, y, w);
	}

	@Override
	public ValidExpression toValidExpression() {
		return getVector();
	}

	@Override
	public boolean moveVector(final Coords rwTransVec,
			final Coords endPosition) {

		boolean movedGeo = false;

		final GeoVector vector = this;
		if (endPosition != null) {
			vector.setCoords(endPosition.getX(), endPosition.getY(), 0);
			movedGeo = true;
		}

		// translate point
		else {
			double x = vector.getX() + rwTransVec.getX();
			double y = vector.getY() + rwTransVec.getY();

			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			if (Math.abs(rwTransVec.getX()) > Kernel.MIN_PRECISION) {
				x = DoubleUtil.checkDecimalFraction(x);
			}
			if (Math.abs(rwTransVec.getY()) > Kernel.MIN_PRECISION) {
				y = DoubleUtil.checkDecimalFraction(y);
			}

			// set translated point coords
			vector.setCoords(x, y, 0);
			movedGeo = true;
		}

		return movedGeo;
	}

	@Override
	public VectorHeadStyle getHeadStyle() {
		return headStyle;
	}

	@Override
	public void setHeadStyle(VectorHeadStyle headStyle) {
		this.headStyle = headStyle;
	}
}
