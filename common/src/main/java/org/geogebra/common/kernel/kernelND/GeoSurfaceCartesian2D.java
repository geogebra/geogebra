package org.geogebra.common.kernel.kernelND;

import java.util.TreeMap;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.matrix.Coords3;
import org.geogebra.common.plugin.GeoClass;

/**
 * 2D surface f(u,v)=(u+v,u-v)
 * 
 * @author Zbynek
 *
 */
public class GeoSurfaceCartesian2D extends GeoSurfaceCartesianND {

	/**
	 * @param c
	 *            construction
	 */
	public GeoSurfaceCartesian2D(Construction c) {
		super(c);
	}

	/**
	 * @param cons
	 *            construction
	 * @param point
	 *            expression as point
	 * @param fun
	 *            expression as list of function
	 */
	public GeoSurfaceCartesian2D(Construction cons, ExpressionNode point,
			FunctionNVar[] fun) {
		super(cons, point, fun);
		setEuclidianVisible(false);
	}

	@Override
	public void evaluatePoint(double u, double v, Coords3 point) {
		double[] tmp = { u, v };
		point.set(fun[0].evaluate(tmp), fun[1].evaluate(tmp), 0);
	}

	@Override
	public boolean evaluateNormal(Coords3 p, double u, double v,
			Coords3 normal) {
		p.set(0, 0, 1);
		return true;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.PARAMETRIC2D;
	}

	@Override
	public void printCASEvalMapXML(StringBuilder sb) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateCASEvalMap(TreeMap<String, String> casMap) {
		// TODO Auto-generated method stub
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.SURFACECARTESIAN;
	}

	@Override
	public GeoElement copy() {
		GeoSurfaceCartesian2D ret = new GeoSurfaceCartesian2D(cons);
		ret.set(this);
		ret.setEuclidianVisible(isSetEuclidianVisible());
		return ret;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public HitType getLastHitType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExpressionValue evaluateSurface(double u, double v) {
		double[] tmp = { u, v };
		if (fun == null) {
			return new GeoVec2D(kernel, Double.NaN, Double.NaN);
		}
		GeoVec2D ret = new GeoVec2D(kernel, fun[0].evaluate(tmp), fun[1].evaluate(tmp));
		if (complexVariable != null) {
			ret.setMode(Kernel.COORD_COMPLEX);
		}
		return ret;
	}

	@Override
	public boolean hasDrawable3D() {
		return true;
	}

}
