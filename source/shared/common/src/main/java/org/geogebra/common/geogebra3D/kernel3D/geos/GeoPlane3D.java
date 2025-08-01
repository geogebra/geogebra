package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.ArrayList;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidianForPlane.EuclidianViewForPlaneCompanionInterface;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.MatrixTransformable;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Functional2Var;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.geos.XMLBuilder;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoCoords4D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.RotatableND;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.kernel.matrix.CoordMatrix;
import org.geogebra.common.kernel.matrix.CoordMatrix4x4;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;

/**
 * Plane
 *
 */
public class GeoPlane3D extends GeoElement3D
		implements Functional2Var, ViewCreator, GeoCoords4D, GeoPlaneND,
		Translateable, Traceable, RotatableND, MirrorableAtPlane,
		Transformable, Dilateable, MatrixTransformable {

	// values for grid and interactions
	private double xmin;
	private double xmax;
	private double ymin;
	private double ymax;
	// values for plate
	private double xPlateMin;
	private double xPlateMax;
	private double yPlateMin;
	private double yPlateMax;

	// grid and plate
	private boolean plateVisible = true;
	private double dx = Double.NaN; // distance between two marks on the grid
									// //TODO use
							// object
	// properties
	private double dy = Double.NaN;

	/** coord sys */
	protected CoordSys coordsys;

	private float fading = 0.10f;
	private EuclidianViewForPlaneCompanionInterface euclidianViewForPlane;

	private Coords tmpCoords1;
	private Coords tmpCoords2;
	private Coords canceledEquation = new Coords(4);
	private boolean trace;

	/** string repre of coordinates */
	private static final String[] VAR_STRING = { "x", "y", "z" };
	protected Form equationForm = Form.USER;

	/**
	 * creates an empty plane
	 * 
	 * @param c
	 *            construction
	 */
	public GeoPlane3D(Construction c) {
		super(c);

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		coordsys = new CoordSys(2);

		this.xmin = -2.5;
		this.xmax = 2.5;
		this.ymin = -2.5;
		this.ymax = 2.5;

		// grid
		// setGridVisible(true);

	}

	/**
	 * @param cons
	 *            construction
	 * @param a
	 *            x-coefficient
	 * @param b
	 *            y-coefficient
	 * @param c
	 *            z-coefficient
	 * @param d
	 *            constant coefficient
	 */
	public GeoPlane3D(Construction cons, double a, double b, double c,
			double d) {
		this(cons);

		setEquation(a, b, c, d);

	}

	/**
	 * @param a
	 *            x-coefficient
	 * @param b
	 *            y-coefficient
	 * @param c
	 *            z-coefficient
	 * @param d
	 *            constant coefficient
	 */
	public void setEquation(double a, double b, double c, double d) {
		setEquation(a, b, c, d, true);
	}

	@Override
	public void setCoords(double x, double y, double z, double w) {
		setEquation(x, y, z, w, false);
	}

	private void setEquation(double a, double b, double c, double d,
			boolean makeCoordSys) {

		if (makeCoordSys || !getCoordSys().isDefined()) {
			setDefinition(null);
			getCoordSys().makeCoordSys(a, b, c, d);
			getCoordSys().makeOrthoMatrix(true, true);
		}
	}

	// /////////////////////////////////
	// REGION INTERFACE

	@Override
	public boolean isRegion() {
		return true;
	}

	@Override
	public Coords[] getNormalProjection(Coords coords) {
		return getCoordSys().getNormalProjection(coords);
	}

	@Override
	public Coords[] getProjection(Coords oldCoords, Coords willingCoords,
			Coords willingDirection) {
		Coords[] result = new Coords[] { new Coords(4), new Coords(4) };
		willingCoords.projectPlaneThruVIfPossible(
				getCoordSys().getMatrixOrthonormal(), oldCoords,
				willingDirection, result[0], result[1]);

		return result;
	}

	@Override
	public boolean isInRegion(GeoPointND P) {
		Coords planeCoords = getNormalProjection(P.getInhomCoordsInD3())[1];
		return DoubleUtil.isEqual(planeCoords.get(3), 0, Kernel.STANDARD_PRECISION);
	}

	@Override
	public boolean isInRegion(double x0, double y0) {
		return true;
	}

	@Override
	public void pointChangedForRegion(GeoPointND P) {
		P.updateCoords2D();
		P.updateCoordsFrom2D(false, null);

	}

	@Override
	public void regionChanged(GeoPointND P) {
		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(P)
				|| P.getRegionParameters().isNaN()) {
			pointChangedForRegion(P);
			return;
		}

		// pointChangedForRegion(P);
		RegionParameters rp = P.getRegionParameters();
		P.setCoords(getPoint(rp.getT1(), rp.getT2(), new Coords(4)), false);

	}

	@Override
	public Coords getPoint(double x2d, double y2d, Coords coords) {
		return getCoordSys().getPoint(x2d, y2d, coords);
	}

	// /////////////////////////////////
	// GRID AND PLATE

	/**
	 * Sets corners of the plate.
	 * 
	 * @param x1
	 *            x-min
	 * @param y1
	 *            y-min
	 * @param x2
	 *            x-max
	 * @param y2
	 *            y-max
	 */
	public void setPlateCorners(double x1, double y1, double x2, double y2) {
		if (x1 < x2) {
			this.xPlateMin = x1;
			this.xPlateMax = x2;
		} else {
			this.xPlateMin = x2;
			this.xPlateMax = x1;
		}
		if (y1 < y2) {
			this.yPlateMin = y1;
			this.yPlateMax = y2;
		} else {
			this.yPlateMin = y2;
			this.yPlateMax = y1;
		}
	}

	/**
	 * sets corners of the grid
	 * 
	 * @param x1
	 *            x-min
	 * @param y1
	 *            y-min
	 * @param x2
	 *            x-max
	 * @param y2
	 *            y-max
	 */
	public void setGridCorners(double x1, double y1, double x2, double y2) {
		if (x1 < x2) {
			this.xmin = x1;
			this.xmax = x2;
		} else {
			this.xmin = x2;
			this.xmax = x1;
		}
		if (y1 < y2) {
			this.ymin = y1;
			this.ymax = y2;
		} else {
			this.ymin = y2;
			this.ymax = y1;
		}
	}

	/**
	 * set grid distances (between two ticks)
	 * 
	 * @param dx
	 *            grid x distance
	 * @param dy
	 *            grid y distance
	 */
	public void setGridDistances(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	/** @return x min */
	public double getXmin() {
		return xmin;
	}

	/** @return y min */
	public double getYmin() {
		return ymin;
	}

	/** @return x max */
	public double getXmax() {
		return xmax;
	}

	/** @return y max */
	public double getYmax() {
		return ymax;
	}

	/** @return plate x min */
	public double getXPlateMin() {
		return xPlateMin;
	}

	/** @return plate y min */
	public double getYPlateMin() {
		return yPlateMin;
	}

	/** @return plate x max */
	public double getXPlateMax() {
		return xPlateMax;
	}

	/** @return plate y max */
	public double getYPlateMax() {
		return yPlateMax;
	}

	/** @return if there is a grid to plot or not */
	public boolean isGridVisible() {
		return getLineThickness() > 0 && isEuclidianVisible();
	}

	/** @return if there is a plate visible */
	public boolean isPlateVisible() {
		return plateVisible && isEuclidianVisible();
	}

	/**
	 * @param flag
	 *            if there is a plate visible
	 */
	public void setPlateVisible(boolean flag) {
		plateVisible = flag;
	}

	/** @return x delta for the grid */
	public double getGridXd() {
		return dx;
	}

	/** @return y delta for the grid */
	public double getGridYd() {
		return dy;
	}

	// /////////////////////////////////
	// GEOELEMENT3D

	@Override
	public Coords getMainDirection() {
		return getCoordSys().getNormal();
	}

	@Override
	public Coords getLabelPosition() {
		return getCoordSys().getPoint(0.5, 0.5);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.PLANE3D;
	}

	@Override
	public GeoPlane3D copy() {
		GeoPlane3D p = new GeoPlane3D(cons);

		p.set(this);

		return p;
	}

	@Override
	public ExtendedBoolean isEqualExtended(GeoElementND geo) {
		if (!geo.isDefined() || !isDefined()) {
			return ExtendedBoolean.FALSE;
		}
		if (geo.isGeoPlane()) {
			Coords ev1 = getCoordSys().getEquationVector();
			Coords ev2 = ((GeoPlane3D) geo).getCoordSys().getEquationVector();
			return ExtendedBoolean.newExtendedBoolean(!ev1.isLinearIndependentAllCoords(ev2));
		}
		return ExtendedBoolean.FALSE;
	}

	/**
	 * Also allow setting from line x+y=1, which may come from user or CAS
	 * instead of x+y+0z=1
	 */
	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoPlane3D) {
			GeoPlane3D plane = (GeoPlane3D) geo;
			getCoordSys().set(plane.getCoordSys());
			reuseDefinition(geo);
			return;
		}
		setDefinition(null);
		if (geo instanceof GeoLine) {
			GeoLine line = (GeoLine) geo;
			setEquation(line.getX(), line.getY(), 0, line.getZ());
		} else {
			setUndefined();
		}
	}

	/**
	 * @param cs
	 *            coordinate system
	 */
	public void setCoordSys(CoordSys cs) {
		getCoordSys().set(cs);
	}

	@Override
	public void setBasicVisualStyle(GeoElement geo) {
		super.setBasicVisualStyle(geo);
		if (geo.isGeoPlane()) {
			setFading(((GeoPlaneND) geo).getFading());
		}
	}

	@Override
	public void setUndefined() {
		coordsys.setUndefined();
		setDefinition(null);
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (!isDefinitionValid()) {
			return "?";
		}
		// we need to keep 0z in equation to be sure that y+0z=1 will be loaded
		// as a plane
		if (getEquationForm() == Form.USER && getDefinition() != null) {
			return getDefinition().toValueString(tpl);
		}
		canceledEquation.set(getCoordSys().getEquationVector());
		return buildValueString(tpl, kernel, canceledEquation,
				!isLabelSet()).toString();
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return label + ": " + toValueString(tpl);
	}

	@Override
	public @CheckForNull Form getEquationForm() {
		return equationForm;
	}

	@Override // EquationLinear
	public void setEquationForm(@CheckForNull Form equationForm) {
		if (equationForm != null) {
			this.equationForm = equationForm;
		}
	}

	@Override
	public void setToParametricForm(String parameter) {
		// if we want parametric form for plane, we will need 2 parameters
	}

	/**
	 * @param tpl
	 *            template
	 * @param kernel
	 *            kernel
	 * @param coords
	 *            coefficients
	 * @param needsZ
	 *            whether to force +0z
	 * @return value as stringbuilder
	 */
	static public StringBuilder buildValueString(StringTemplate tpl,
			Kernel kernel, Coords coords, boolean needsZ) {
		return kernel.buildImplicitEquation(coords.get(), VAR_STRING,
				true, needsZ, tpl, true);
	}

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		return true;
	}

	// ///////////////////////////////////////////
	// 2 VAR FUNCTION INTERFACE
	// //////////////////////////////////////////

	@Override
	public Coords evaluateNormal(double u, double v) {
		return coordsys.getNormal();
	}

	@Override
	public void evaluatePoint(double u, double v, Coords point) {
		coordsys.getPointForDrawing(u, v, point);
	}

	@Override
	public double getMinParameter(int index) {
		return 0; // TODO
	}

	@Override
	public double getMaxParameter(int index) {
		return 0; // TODO
	}

	@Override
	public CoordSys getCoordSys() {
		return coordsys;
	}

	@Override
	public boolean isDefined() {
		return coordsys.isDefined();
	}

	@Override
	public boolean isDefinitionValid() {
		return isDefined() || (getDefinition() != null
				&& bothSidesDefined(getDefinition()));
	}

	/**
	 * @param definition
	 *            plane definition
	 * @return whether this is a (wrapped) equation with both sides defined, ie
	 *         x=? is undefined, x=x defined
	 */
	private static boolean bothSidesDefined(ExpressionNode definition) {
		ExpressionValue ev = definition.unwrap();
		if (ev instanceof Equation) {
			return Double.isFinite(((Equation) ev).getLHS().evaluateDouble())
					&& Double.isFinite(
							((Equation) ev).getRHS().evaluateDouble());
		}
		return false;
	}

	@Override
	public boolean isMoveable() {
		return false;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		Coords equation = getCoordSys().getEquationVector();
		// equation
		sb.append("\t<coords x=\"");
		sb.append(equation.getX());
		sb.append("\" y=\"");
		sb.append(equation.getY());
		sb.append("\" z=\"");
		sb.append(equation.getZ());
		sb.append("\" w=\"");
		sb.append(equation.getW());
		sb.append("\"/>\n");
	}

	@Override
	protected void getStyleXML(StringBuilder sb) {
		super.getStyleXML(sb);
		// fading
		sb.append("\t<fading val=\"");
		sb.append(getFading());
		sb.append("\"/>\n");

		// grid line style
		getLineStyleXML(sb);
		XMLBuilder.appendEquationTypeLine(sb, getEquationForm(), null);
	}

	@Override
	public boolean isGeoPlane() {
		return true;
	}

	// ////////////////////////////////
	// FADING

	@Override
	public void setFading(float fading) {
		this.fading = fading;
	}

	@Override
	public float getFading() {
		return fading;
	}

	// ////////////////////////////////
	// 2D VIEW

	@Override
	public int getViewID() {
		return euclidianViewForPlane.getId();
	}

	@Override
	public void createView2D() {
		euclidianViewForPlane = kernel.getApplication().getCompanion()
				.createEuclidianViewForPlane(this, true);
		euclidianViewForPlane.setTransformRegardingView();
	}

	@Override
	public void removeView2D() {
		euclidianViewForPlane.doRemove();
	}

	@Override
	public void doRemove() {
		if (euclidianViewForPlane != null) {
			removeView2D();
		}
		super.doRemove();
	}

	@Override
	public boolean hasView2DVisible() {
		return euclidianViewForPlane != null && kernel.getApplication()
				.getGuiManager().showView(euclidianViewForPlane.getId());
	}

	@Override
	public void setView2DVisible(boolean flag) {
		if (euclidianViewForPlane == null) {
			if (flag) {
				createView2D();
			}
			return;
		}

		kernel.getApplication().getGuiManager().setShowView(flag,
				euclidianViewForPlane.getId());

	}

	@Override
	public void update(boolean drag) {
		super.update(drag);
		if (euclidianViewForPlane != null) {
			euclidianViewForPlane.updateForPlane();
		}
	}

	@Override
	public void setEuclidianViewForPlane(
			EuclidianViewForPlaneCompanionInterface view) {
		euclidianViewForPlane = view;
	}

	@Override
	public Coords getDirectionInD3() {
		return getCoordSys().getNormal();
	}

	@Override
	public double distance(GeoPointND P) {
		return Math.abs(distanceWithSign(P));

	}

	/**
	 * 
	 * @param P
	 *            point
	 * @return distance from point P to this plane, with sign
	 */
	public double distanceWithSign(GeoPointND P) {
		if (tmpCoords1 == null) {
			tmpCoords1 = new Coords(3);
		}
		if (tmpCoords2 == null) {
			tmpCoords2 = new Coords(3);
		}

		tmpCoords1.setSub(P.getInhomCoordsInD3(), getCoordSys().getOrigin());
		tmpCoords2.setValues(getDirectionInD3(), 3);
		tmpCoords2.normalize();

		return tmpCoords1.dotproduct(tmpCoords2);

	}

	@Override
	public double distanceWithSign(GeoPlaneND P) {
		if (tmpCoords1 == null) {
			tmpCoords1 = new Coords(3);
		}
		if (tmpCoords2 == null) {
			tmpCoords2 = new Coords(3);
		}
		tmpCoords2.setValues(getDirectionInD3(), 3);
		tmpCoords2.normalize();

		tmpCoords1.setValues(P.getDirectionInD3(), 3);
		tmpCoords1.normalize();

		if (!DoubleUtil.isEqual(1, Math.abs(tmpCoords1.dotproduct(tmpCoords2)))) {
			return 0;
		}

		tmpCoords1.setSub(P.getCoordSys().getOrigin(),
				getCoordSys().getOrigin());

		return tmpCoords1.dotproduct(tmpCoords2);

	}

	// ///////////////////////////////////
	// TRANSLATE
	// ///////////////////////////////////

	@Override
	public void translate(Coords v) {
		getCoordSys().translate(v);
		getCoordSys().translateEquationVector(v);
	}

	@Override
	public boolean isTranslateable() {
		return true;
	}

	// ////////////////
	// TRACE
	// ////////////////

	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public boolean getTrace() {
		return trace;
	}

	// //////////////////////
	// ROTATIONS
	// //////////////////////

	/**
	 * rotate the plane
	 * 
	 * @param rot
	 *            rotation matrix
	 * @param center
	 *            rotation center
	 */
	final public void rotate(CoordMatrix rot, Coords center) {
		coordsys.rotate(rot, center);
		coordsys.makeEquationVector();
	}

	@Override
	final public void rotate(NumberValue phiVal) {
		coordsys.rotate(phiVal.getDouble(), Coords.O);
		coordsys.makeEquationVector();
	}

	@Override
	final public void rotate(NumberValue phiVal, GeoPointND Q) {
		coordsys.rotate(phiVal.getDouble(), Q.getInhomCoordsInD3());
		coordsys.makeEquationVector();
	}

	private void rotate(NumberValue phiVal, Coords center,
			Coords direction) {
		coordsys.rotate(phiVal.getDouble(), center, direction.normalized());
		coordsys.makeEquationVector();
	}

	@Override
	public void rotate(NumberValue phiVal, Coords Q,
			GeoDirectionND orientation) {

		rotate(phiVal, Q, orientation.getDirectionInD3());

	}

	@Override
	public void mirror(Coords Q) {
		coordsys.mirror(Q);
		coordsys.mirrorEquationVector(Q);

	}

	@Override
	public void mirror(GeoLineND line) {
		Coords point = line.getStartInhomCoords();
		Coords direction = line.getDirectionInD3().normalized();

		coordsys.mirror(point, direction);
		coordsys.makeEquationVector();

	}

	@Override
	public void mirror(GeoCoordSys2D plane) {
		coordsys.mirror(plane.getCoordSys());
		coordsys.makeEquationVector();

	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue rval, Coords S) {
		double r = rval.getDouble();

		coordsys.dilate(r, S);
		coordsys.dilateEquationVector(r, S);
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public boolean is6dofMoveable() {
		return true;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.EQUATION;
	}

	@Override
	protected void getXMLanimationTags(final StringBuilder sb) {
		// no need for planes
	}

	@Override
	public char getLabelDelimiter() {
		return ':';
	}

	@Override
	public boolean showLineProperties() {
		return true;
	}

	@Override
	public int getMinimumLineThickness() {
		return 0;
	}

	@Override
	public Equation getEquation() {
		return kernel.getAlgebraProcessor().parseEquation(this);
	}

	@Override
	public boolean isRegion3D() {
		return true;
	}

	@Override
	public String[] getEquationVariables() {
		ArrayList<String> usedVars = new ArrayList<>();
		if (!MyDouble.exactEqual(getCoordSys().getEquationVector().getX(), 0)) {
			usedVars.add("x");
		}
		if (!MyDouble.exactEqual(getCoordSys().getEquationVector().getY(), 0)) {
			usedVars.add("y");
		}
		if (!MyDouble.exactEqual(getCoordSys().getEquationVector().getZ(), 0)) {
			usedVars.add("z");
		}
		GeoLine.addUsedVars(usedVars, getDefinition());
		return usedVars.toArray(new String[0]);
	}

	@Override
	public boolean isLaTeXDrawableGeo() {
		return getEquationForm() == Form.USER;
	}

	@Override
	public boolean setEquationFormFromXML(String style, String parameter) {
		if ("implicit".equals(style)) {
			setToImplicitForm();
		} else if ("user".equals(style)) {
			setToUserForm();
		} else {
			return false;
		}
		return true;
	}

	@Override
	public void matrixTransform(double a00, double a01, double a10, double a11) {
		matrixTransform(a00, a01, 0, a10, a11, 0, 0, 0, 1);
	}

	@Override
	public void matrixTransform(double a00, double a01, double a02, double a10, double a11,
			double a12, double a20, double a21, double a22) {
		CoordMatrix4x4 tmpMatrix4x4 = CoordMatrix4x4.identity();

		tmpMatrix4x4.set(1, 1, a00);
		tmpMatrix4x4.set(1, 2, a01);
		tmpMatrix4x4.set(1, 3, a02);

		tmpMatrix4x4.set(2, 1, a10);
		tmpMatrix4x4.set(2, 2, a11);
		tmpMatrix4x4.set(2, 3, a12);

		tmpMatrix4x4.set(3, 1, a20);
		tmpMatrix4x4.set(3, 2, a21);
		tmpMatrix4x4.set(3, 3, a22);
		coordsys.matrixTransform(tmpMatrix4x4);
		coordsys.makeEquationVector();
	}

	@Override
	public boolean isMatrixTransformable() {
		return true;
	}

	@Override
	public void applyToStringModeFrom(GeoElement other) {
		if (other instanceof LinearEquationRepresentable) {
			equationForm = ((LinearEquationRepresentable) other).getEquationForm();
		}
	}

}
