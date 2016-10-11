package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 3D extension of implicit curves
 *
 */
public class GeoImplicitCurve3D extends GeoImplicitCurve {

	private CoordSys transformCoordSys;
	private GeoElement parentGeo;

	/**
	 * @param c
	 *            construction
	 */
	public GeoImplicitCurve3D(Construction c) {
		super(c);
		this.transformCoordSys = new CoordSys(2);
		transformCoordSys.set(CoordSys.Identity3D);

	}

	public GeoImplicitCurve3D(GeoImplicitCurve geo) {
		this(geo.getConstruction());
		set(geo);
	}

	@Override
	public CoordSys getTransformedCoordSys() {
		return transformCoordSys;
	}

	public void setTransformedCoordSys(CoordSys sys) {
		transformCoordSys.set(sys);
	}


	@Override
	public void set(GeoElementND geo) {
		super.set(geo);
		GeoImplicit implicit = (GeoImplicit) geo;
		transformCoordSys.set(implicit.getTransformedCoordSys());
	}

	@Override
	public GeoImplicitCurve3D copy() {
		GeoImplicitCurve3D curve = new GeoImplicitCurve3D(cons);
		curve.set(this);
		return curve;
	}

	private double[] planeEquationNumbers;
	private static final String[] VAR_STRING = { "x", "y" };

	@Override
	public String toValueString(StringTemplate tpl) {
		AlgoElement algo = getParentAlgorithm();
		if (algo != null) {
			if (algo instanceof AlgoIntersectFunctionNVarPlane) {
				AlgoIntersectFunctionNVarPlane algoInter = (AlgoIntersectFunctionNVarPlane) algo;
				GeoFunctionNVar f = algoInter.getFunction();
				GeoPlaneND plane = algoInter.getPlane();
				Coords eq = plane.getCoordSys().getEquationVector();
				StringBuilder valueSb = new StringBuilder(50);
				valueSb.append("(");
				valueSb.append(f.getFunctionExpression().toValueString(tpl));
				valueSb.append(" = ");
				if (Kernel.isEpsilon(eq.getZ(), eq.getY(), eq.getX())) {
					// can't replace z by plane equation
					valueSb.append("z");
				} else {
					// replace z by plane equation
					if (planeEquationNumbers == null) {
						planeEquationNumbers = new double[3];
					}
					planeEquationNumbers[0] = -eq.getX() / eq.getZ();
					planeEquationNumbers[1] = -eq.getY() / eq.getZ();
					planeEquationNumbers[2] = -eq.getW() / eq.getZ();
					valueSb.append(kernel.buildLHS(planeEquationNumbers,
							VAR_STRING, true, false, false,
							tpl));
				}
				valueSb.append(",");
				valueSb.append(
						GeoPlane3D.buildValueString(tpl, kernel, eq, false));
				valueSb.append(")");
				return valueSb.toString();
			}
		}

		return "";

	}

	@Override
	public boolean isGeoElement3D() {
		return true;
	}

	private Coords tmpCoords = new Coords(4);
	private Coords tmpCoords3d = new Coords(4);

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
				transformCoordSys.getOrigin(),
				tmpCoords);
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
		euclidianViewUpdate();
	}

}
