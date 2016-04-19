package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;

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

		curve.getCoordSys().set(plane.getCoordSys());
		// a*x+b*y+c*z=d, z=d/c-a/c*x-b/c*y
		Coords norm = plane.getCoordSys().getEquationVector();
		FunctionVariable x = surface.getFunctionVariables()[0];
		FunctionVariable y = surface.getFunctionVariables()[1];
		ExpressionNode exp;
		if (!Kernel.isZero(norm.getZ())) {
			exp = x.wrap()
					.multiply(norm.getX() / norm.getZ())
					.plus(y.wrap().multiply(norm.getY() / norm.getZ())
							.plus(surface.getFunctionExpression())
							.plus(norm.getW() / norm.getZ()));
		} else {
			VariableReplacer vr = VariableReplacer.getReplacer(kernel);
			exp = surface.getFunctionExpression().getCopy(kernel);
			if (!Kernel.isZero(norm.getY())) {

				ExpressionNode substY = x.wrap()
						.multiply(-norm.getX() / norm.getY())
						.plus(-norm.getW() / norm.getY());
				VariableReplacer.addVars("y", substY);

			} else {
				ExpressionNode substY = new ExpressionNode(kernel,
						-norm.getW() / norm.getX());
				VariableReplacer.addVars("x", substY);
				VariableReplacer.addVars("y",
						new FunctionVariable(kernel, "x"));
			}
			exp = exp.traverse(vr).wrap()
					.subtract(new FunctionVariable(kernel, "y"));
		}
		curve.fromEquation(
				new Equation(kernel, exp, new ExpressionNode(kernel, 0)), null);
		
		// TODO Auto-generated method stub

	}

	@Override
	public GetCommand getClassName() {
		return Commands.IntersectPath;
	}

}
