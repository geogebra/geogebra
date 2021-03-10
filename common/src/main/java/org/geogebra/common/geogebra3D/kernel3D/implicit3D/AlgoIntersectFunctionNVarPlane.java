package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitCurve3D.Type;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Finds intersection path of surface and plane
 * 
 * @author zbynek
 *
 */
public class AlgoIntersectFunctionNVarPlane extends AlgoElement {

	private GeoFunctionNVar surface;
	private GeoPlaneND plane;
	private GeoImplicitCurve3D curve;

	/**
	 * @param c
	 *            construction
	 * @param surface
	 *            surface
	 * @param plane
	 *            plane
	 */
	public AlgoIntersectFunctionNVarPlane(Construction c,
			GeoFunctionNVar surface, GeoPlaneND plane) {
		super(c);
		this.surface = surface;
		this.plane = plane;
		this.curve = new GeoImplicitCurve3D(c);

		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		this.input = new GeoElement[] { (GeoElement) plane, surface };
		setOnlyOutput(curve);
		setDependencies();

	}

	@Override
	public void compute() {

		if (!plane.isDefined()) {
			curve.setUndefined();
			return;
		}

		FunctionNVar function = surface.getFunction();
		if (!surface.isDefined() || function == null) {
			curve.setUndefined();
			return;
		}

		curve.setDefined();
		curve.setFunctionExpression(function);

		// a*x+b*y+c*z=d, z=d/c-a/c*x-b/c*y
		Coords norm = plane.getCoordSys().getEquationVector();
		curve.setPlaneEquation(norm);

		FunctionVariable x = surface.getFunctionVariables()[0];
		FunctionVariable y = surface.getFunctionVariables()[1];
		ExpressionNode exp;
		if (!DoubleUtil.isZero(norm.getZ())) {
			double a = norm.getX();
			double b = norm.getY();
			double c = norm.getZ();
			double d = norm.getW();
			exp = x.wrap().multiply(a / c)
					.plus(y.wrap()
							.multiply(b / c).plus(surface
									.getFunctionExpression().deepCopy(kernel))
							.plus(d / c));

			curve.getTransformedCoordSys().setZequal(a, b, c, d);
			curve.setType(Type.DEFAULT);
		} else {
			VariableReplacer vr = VariableReplacer.getReplacer(kernel);
			exp = surface.getFunctionExpression().getCopy(kernel);
			if (!DoubleUtil.isZero(norm.getY())) {
				double a = norm.getX();
				double b = norm.getY();
				double d = norm.getW();
				ExpressionNode substY = x.wrap().multiply(-a / b).plus(-d / b);
				VariableReplacer.addVars("y", substY);

				curve.getTransformedCoordSys().setYequal(a, b, d);
				curve.setType(Type.PLANE_XY);
			} else {
				double v = -norm.getW() / norm.getX();
				ExpressionNode substY = new ExpressionNode(kernel, v);
				VariableReplacer.addVars("x", substY);
				VariableReplacer.addVars("y",
						new FunctionVariable(kernel, "x"));

				// set transformed coord sys to x=value
				curve.getTransformedCoordSys().setXequal(v);
				curve.setType(Type.PLANE_X);
			}
			exp = exp.traverse(vr).wrap()
					.subtract(new FunctionVariable(kernel, "y"));
		}
		Equation eqn = new Equation(kernel, exp, new ExpressionNode(kernel, 0));
		eqn.initEquation();
		curve.fromEquation(eqn, null);
	}

	@Override
	public GetCommand getClassName() {
		return Commands.IntersectPath;
	}

}
