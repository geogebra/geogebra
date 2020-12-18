package org.geogebra.common.geogebra3D.kernel3D.implicit3D;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoIntersect3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoRoots;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.Operation;

/**
 * Find intersection between implicit surface and line
 * 
 * @author GSoCImplicit2015
 *
 */
public class AlgoIntersectImplicitSurface extends AlgoIntersect3D {
	private static final int SAMPLE_SIZE = 100;
	private GeoImplicitSurfaceND surface;
	private GeoElementND eqn;
	private OutputHandler<GeoPoint3D> outputs;
	private String[] labels;

	/**
	 * 
	 * @param c
	 *            construction
	 * @param surface
	 *            implicit surface
	 * @param labels
	 *            labels
	 * @param equation
	 *            equation
	 */
	public AlgoIntersectImplicitSurface(Construction c, String[] labels,
			GeoImplicitSurfaceND surface, GeoElementND equation) {
		super(c);
		this.surface = surface;
		this.eqn = equation;
		this.labels = labels;
		initForNearToRelationship();
		setInputOutput();
		compute();
	}

	@Override
	public GeoPoint3D[] getIntersectionPoints() {
		return outputs.getOutput(new GeoPoint3D[outputs.size()]);
	}

	@Override
	protected GeoPoint3D[] getLastDefinedIntersectionPoints() {
		return getIntersectionPoints();
	}

	@Override
	public void compute() {
		if (surface == null || !surface.isDefined()) {
			outputs.adjustOutputSize(0);
			return;
		}

		if (eqn instanceof GeoLineND) {
			intersectLine((GeoLineND) eqn);
		}
	}

	private void intersectLine(GeoLineND eq) {
		Coords v = eq.getDirectionInD3();
		if (eq.getStartPoint() == null) {
			eq.setStandardStartPoint();
		}
		Coords r = eq.getStartPoint().getCoordsInD3();
		FunctionVariable t = new FunctionVariable(kernel, "x");
		ExpressionNode x = new ExpressionNode(kernel, r.getX());
		ExpressionNode y = new ExpressionNode(kernel, r.getY());
		ExpressionNode z = new ExpressionNode(kernel, r.getZ());
		x = x.plus(new ExpressionNode(kernel, t, Operation.MULTIPLY,
				new MyDouble(kernel, v.getX())));
		y = y.plus(new ExpressionNode(kernel, t, Operation.MULTIPLY,
				new MyDouble(kernel, v.getY())));
		z = z.plus(new ExpressionNode(kernel, t, Operation.MULTIPLY,
				new MyDouble(kernel, v.getZ())));
		intersectParameteric(x, y, z);
	}

	private void intersectParameteric(ExpressionNode x, ExpressionNode y,
			ExpressionNode z) {
		FunctionNVar func = surface.getExpression();
		FunctionVariable[] vars = func.getFunctionVariables();
		ExpressionNode exp = func.getExpression().getCopy(getKernel());
		exp.replace(vars[0], x);
		exp.replace(vars[1], y);
		if (vars.length == 3) {
			exp.replace(vars[2], z);
		}
		Function fn = new Function(kernel, exp);
		fn.initFunction();
		double[] roots = AlgoRoots.findRoots(fn,
				kernel.getViewsXMin(surface), kernel.getViewsYMax(surface),
				SAMPLE_SIZE);
		if (roots == null || roots.length == 0) {
			outputs.adjustOutputSize(0);
			return;
		}
		Function f1 = new Function(kernel, x);
		Function f2 = new Function(kernel, y);
		Function f3 = new Function(kernel, z);
		f1.initFunction();
		f2.initFunction();
		f3.initFunction();
		outputs.adjustOutputSize(roots.length);
		for (int i = 0; i < roots.length; i++) {
			double vx = f1.value(roots[i]);
			double vy = f2.value(roots[i]);
			double vz = f3.value(roots[i]);
			outputs.getElement(i).setCoords(vx, vy, vz, 1.0);
		}
	}

	@Override
	public void initForNearToRelationship() {
		// TODO Ask, what is it?

	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = surface.toGeoElement();
		input[1] = eqn.toGeoElement();

		outputs = new OutputHandler<>(
				new ElementFactory<GeoPoint3D>() {
					@Override
					public GeoPoint3D newElement() {
						GeoPoint3D p = new GeoPoint3D(cons);
						p.setParentAlgorithm(AlgoIntersectImplicitSurface.this);
						return p;
					}
				});

		setDependencies();

	}

	/**
	 * 
	 * @param labels
	 *            set labels
	 */
	public void setLabels(String[] labels) {
		this.labels = labels;
		outputs.setLabels(this.labels);
		update();
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Intersect;
	}

}
