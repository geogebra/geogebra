package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * 3D extension of implicit curves
 *
 */
public class GeoImplicitCurve3D extends GeoImplicitCurve
		implements MirrorableAtPlane {

	/**
	 * Curve type based on plane defining the intersection
	 */
	public enum Type {
		/** general plane */
		DEFAULT,
		/** plane f(x,y)=0 */
		PLANE_XY,
		/** plane x=0 */
		PLANE_X
	}

	private CoordSys transformCoordSys;
	private FunctionNVar functionExpression;
	private double[] planeEquationNumbers;
	private static final String[] VAR_STRING = { "x", "y" };
	private Coords tmpCoords = new Coords(4);
	private Coords tmpCoords3d = new Coords(4);
	private double translateZ = 0;
	private Coords planeEquation = new Coords(4);
	private Type type = Type.DEFAULT;

	/**
	 * @param c
	 *            construction
	 */
	public GeoImplicitCurve3D(Construction c) {
		super(c);
		this.transformCoordSys = new CoordSys(2);
		transformCoordSys.set(CoordSys.XOY);

	}

	/**
	 * Copy constructor
	 * 
	 * @param geo
	 *            curve to copy
	 */
	public GeoImplicitCurve3D(GeoImplicitCurve geo) {
		this(geo.getConstruction());
		set(geo);
	}

	@Override
	public CoordSys getTransformedCoordSys() {
		return transformCoordSys;
	}

	/**
	 * set type for intersect function / plane
	 * 
	 * @param type
	 *            type
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @param sys
	 *            transformed system
	 */
	public void setTransformedCoordSys(CoordSys sys) {
		transformCoordSys.set(sys);
	}

	@Override
	public void set(GeoElementND geo) {
		super.set(geo);
		GeoImplicit implicit = (GeoImplicit) geo;
		transformCoordSys.set(implicit.getTransformedCoordSys());
		setFunctionExpression(implicit.getFunctionDefinition());
		setPlaneEquation(implicit.getPlaneEquation());
		translateZ = implicit.getTranslateZ();
	}

	@Override
	public GeoImplicitCurve3D copy() {
		GeoImplicitCurve3D curve = new GeoImplicitCurve3D(cons);
		curve.set(this);
		return curve;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		if (!isDefined()) {
			return "?";
		}
		StringBuilder valueSb = new StringBuilder(50);
		valueSb.append("(");
		if (functionExpression != null) {
			valueSb.append(
					functionExpression.getExpression().toValueString(tpl));
			valueSb.append(" = ");
			if (DoubleUtil.isEpsilon(planeEquation.getZ(), planeEquation.getY(),
					planeEquation.getX())) {
				// can't replace z by plane equation
				valueSb.append("z");
				kernel.appendConstant(valueSb, -translateZ, tpl);
			} else {
				// replace z by plane equation
				if (planeEquationNumbers == null) {
					planeEquationNumbers = new double[3];
				}
				planeEquationNumbers[0] = -planeEquation.getX()
						/ planeEquation.getZ();
				planeEquationNumbers[1] = -planeEquation.getY()
						/ planeEquation.getZ();
				planeEquationNumbers[2] = -planeEquation.getW()
						/ planeEquation.getZ() - translateZ;
				valueSb.append(kernel.buildLHS(planeEquationNumbers, VAR_STRING,
						false, false, true, tpl));
			}
		} else {
			valueSb.append(getExpression().toValueString(tpl));
			valueSb.append(" = 0");
		}
		valueSb.append(",");
		valueSb.append(
				GeoPlane3D.buildValueString(tpl, kernel, planeEquation, false));
		valueSb.append(")");
		return valueSb.toString();
	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	@Override
	protected void locusPointChanged(GeoPointND PI) {

		Coords willingCoords = null, willingDirection = null;
		GeoPoint3D p3d = (GeoPoint3D) PI;
		if (p3d.hasWillingCoords()) {
			willingCoords = p3d.getWillingCoords();
		} else {
			willingCoords = PI.getInhomCoordsInD3();
		}

		if (p3d.hasWillingDirection()) {
			willingDirection = p3d.getWillingDirection();
		} else {
			willingDirection = transformCoordSys.getVz();
		}

		willingCoords.projectPlaneInPlaneCoords(transformCoordSys.getVx(),
				transformCoordSys.getVy(), willingDirection,
				transformCoordSys.getOrigin(), tmpCoords);
		locus.pointChanged(tmpCoords, PI.getPathParameter());
		transformCoordSys.getPointFromOriginVectors(tmpCoords, tmpCoords3d);
		PI.setCoords(tmpCoords3d, false);

		p3d.setWillingCoordsUndefined();
		p3d.setWillingDirectionUndefined();

	}

	@Override
	protected void locusPathChanged(GeoPointND PI) {
		PI.getInhomCoordsInD3().projectPlaneInPlaneCoords(
				transformCoordSys.getVx(), transformCoordSys.getVy(),
				transformCoordSys.getVz(), transformCoordSys.getOrigin(),
				tmpCoords);
		locus.pathChanged(tmpCoords, PI.getPathParameter());
		transformCoordSys.getPointFromOriginVectors(tmpCoords, tmpCoords3d);
		PI.setCoords(tmpCoords3d, false);
	}

	@Override
	public void translate(Coords v) {
		transformCoordSys.translate(v);
		transformCoordSys.translateEquationVector(v);
		functionExpression.translate(v);
		translateZ += v.getZ();
		CoordSys.translateEquationVector(planeEquation, v);
		euclidianViewUpdate();
	}

	/**
	 * @param expression
	 *            defining expression
	 */
	public void setFunctionExpression(FunctionNVar expression) {
		this.functionExpression = expression.deepCopy(kernel);
	}

	@Override
	public FunctionNVar getFunctionDefinition() {
		return functionExpression;
	}

	/**
	 * @param planeEquation
	 *            normal vector of the plane
	 */
	public void setPlaneEquation(Coords planeEquation) {
		this.planeEquation.set(planeEquation);
	}

	@Override
	public Coords getPlaneEquation() {
		return this.planeEquation;
	}

	@Override
	public double getTranslateZ() {
		return translateZ;
	}

	@Override
	public void mirror(GeoCoordSys2D plane) {
		setUndefined();
		// TODO Auto-generated method stub

	}

	@Override
	protected double[] getViewBounds() {
		if (isVisibleInView3D()
				&& kernel.getApplication().isEuclidianView3Dinited()) {
			// see AlgoIntersectFunctionNVarPlane.compute() where type is set
			switch (type) {
			case PLANE_X:
				EuclidianView3D view = (EuclidianView3D) kernel.getApplication()
						.getEuclidianView3D();
				return new double[] {
						view.getYmin(),
						view.getYmax(),
						view.getZmin(),
						view.getZmax(),
						view.getYscale(), 
						view.getZscale()
				};
			case PLANE_XY:
				view = (EuclidianView3D) kernel.getApplication()
						.getEuclidianView3D();

				double xmin1 = view.getXmin();
				double xmax1 = view.getXmax();
				double scale = view.getXscale();

				// y = (-a/b) X + (-d/b) so we compute X min/max regarding that,
				// and restrict the bounds if needed
				Coords ev = getTransformedCoordSys().getEquationVector();
				double a = ev.getX();
				if (!DoubleUtil.isZero(a)) {
					double b = ev.getY();
					double d = ev.getW();
					double xmin2 = -(b * view.getYmin() + d) / a;
					double xmax2 = -(b * view.getYmax() + d) / a;
					if (xmin2 > xmax2) {
						d = xmin2;
						xmin2 = xmax2;
						xmax2 = d;
					}
					if (xmin1 < xmin2) {
						xmin1 = xmin2;
					}
					if (xmax1 > xmax2) {
						xmax1 = xmax2;
					}
					d = view.getYscale() * Math.abs(a / b);
					if (scale < d) {
						scale = d;
					}
				}

				return new double[] { xmin1, xmax1, view.getZmin(),
						view.getZmax(), scale, view.getZscale() };

			case DEFAULT:
			default:
				return super.getViewBounds();
			}
		}
		return super.getViewBounds();
	}

}
