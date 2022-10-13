package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.HasShortSyntax;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Finds intersection path of surface and plane
 * 
 * @author zbynek
 *
 */
public class AlgoIntersectImplicitSurfacePlane extends AlgoElement
		implements HasShortSyntax {

	private GeoImplicitSurface surface;
	private GeoPlaneND plane;
	private GeoImplicitCurve3D curve;
	private boolean shortSyntax;

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
		VariableReplacer vr = kernel.getVariableReplacer();
		// a*x+b*y+c*z=d, z=d/c-a/c*x-b/c*y
		Coords norm = plane.getCoordSys().getEquationVector();
		curve.setPlaneEquation(norm);
		FunctionVariable x = surface.getExpression().getFunctionVariables()[0];
		FunctionVariable y = surface.getExpression().getFunctionVariables()[1];
		if (!DoubleUtil.isZero(norm.getZ())) {
			double a = norm.getX() / norm.getZ();
			double b = norm.getY() / norm.getZ();
			double d = norm.getW() / norm.getZ();
			ExpressionNode substZ = x.wrap().multiply(a)
					.plus(y.wrap().multiply(b).plus(d));
			vr.addVars("z", substZ);
			curve.getTransformedCoordSys().setZequal(a, b, 1, d);
		} else {
			if (DoubleUtil.isZero(norm.getY())) {
				double a = -norm.getW() / norm.getX();
				ExpressionNode substX = new ExpressionNode(kernel, a);
				vr.addVars("x", substX);
				vr.addVars("y",
						new FunctionVariable(kernel, "x"));
				vr.addVars("z",
						new FunctionVariable(kernel, "y"));
				curve.getTransformedCoordSys().setXequal(a);

			} else {
				double a = norm.getX() / norm.getY();
				double b = norm.getW() / norm.getY();
				ExpressionNode substY = x.wrap().multiply(a).plus(b);
				vr.addVars("y", substY);
				vr.addVars("z",
						new FunctionVariable(kernel, "y"));
				curve.getTransformedCoordSys().setYequal(a, 1, b);
			}
		}
		ExpressionNode exp = surface.getExpression().getFunctionExpression()
				.getCopy(kernel);
		exp = exp.traverse(vr).wrap();
		curve.fromEquation(
				new Equation(kernel, exp, new ExpressionNode(kernel, 0)), null);

		// TODO Auto-generated method stub

	}

	@Override
	public GetCommand getClassName() {
		return Commands.IntersectPath;
	}

	@Override
	final public String getDefinition(StringTemplate tpl) {
		if (shortSyntax) {
			return "(" + surface.getLabel(tpl) + "," + plane.getLabel(tpl)
					+ ")";
		}
		return super.getDefinition(tpl);
	}

	@Override
	public void setShortSyntax(boolean b) {
		this.shortSyntax = b;
	}

	@Override
	protected boolean hasExpXML(String cmdName) {
		return shortSyntax;
	}

	@Override
	final public String toExpString(StringTemplate tpl) {
		return getDefinition(tpl);
	}

}
