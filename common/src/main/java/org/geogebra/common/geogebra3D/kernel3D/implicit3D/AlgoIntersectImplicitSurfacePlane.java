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
import org.geogebra.common.kernel.kernelND.GeoPlaneND;

/**
 * Finds intersection path of surface and plane
 * 
 * @author zbynek
 *
 */
public class AlgoIntersectImplicitSurfacePlane extends AlgoElement {

	private GeoImplicitSurface surface;
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
	public AlgoIntersectImplicitSurfacePlane(Construction c,
			GeoImplicitSurface surface, GeoPlaneND plane) {
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
		VariableReplacer vr = VariableReplacer.getReplacer(kernel);
		curve.getCoordSys().set(plane.getCoordSys());
		// a*x+b*y+c*z=d, z=d/c-a/c*x-b/c*y
		Coords norm = plane.getCoordSys().getEquationVector();
		FunctionVariable x = surface.getExpression().getFunctionVariables()[0];
		FunctionVariable y = surface.getExpression().getFunctionVariables()[1];
		if (!Kernel.isZero(norm.getZ())) {
			ExpressionNode substZ = x.wrap()
					.multiply(-norm.getX() / norm.getZ())
					.plus(y.wrap().multiply(-norm.getY() / norm.getZ())
							.plus(-norm.getW() / norm.getZ()));
			VariableReplacer.addVars("z", substZ);
		} else {
			if (Kernel.isZero(norm.getY())) {
				ExpressionNode substX = new ExpressionNode(kernel,
						norm.getW() / norm.getX());
				VariableReplacer.addVars("x", substX);
				VariableReplacer.addVars("y",
						new FunctionVariable(kernel, "x"));
				VariableReplacer.addVars("z",
						new FunctionVariable(kernel, "y"));

			} else {
			ExpressionNode substY = x.wrap()
					.multiply(-norm.getX() / norm.getY())
					.plus(-norm.getW() / norm.getY());
			VariableReplacer.addVars("y", substY);
			VariableReplacer.addVars("z", new FunctionVariable(kernel, "y"));
			}
		}
		ExpressionNode exp = surface.getExpression().getFunctionExpression()
				.getCopy(kernel);
		exp = exp.traverse(vr).wrap();
		curve.fromEquation(new Equation(kernel, exp, new ExpressionNode(kernel,
				0)), null);
		
		// TODO Auto-generated method stub

	}

	@Override
	public GetCommand getClassName() {
		return Commands.IntersectPath;
	}

}
