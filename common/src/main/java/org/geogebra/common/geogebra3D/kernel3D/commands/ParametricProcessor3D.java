package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDependentConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.VectorArithmetic;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.commands.ParametricProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Processor for 3D parametric curves
 *
 */
public class ParametricProcessor3D extends ParametricProcessor {

	/**
	 * @param kernel
	 *            kernel
	 * @param ap
	 *            algebra processor
	 */
	public ParametricProcessor3D(Kernel kernel, AlgebraProcessor ap) {
		super(kernel, ap);
	}

	@Override
	protected GeoElement[] processParametricFunction(ExpressionNode exp,
			ExpressionValue ev, FunctionVariable[] fv, String label,
			EvalInfo info) {
		Construction cons = kernel.getConstruction();
		if (ev instanceof VectorValue) {
			if (fv.length == 2) {
				GeoElement[] geoElements = processSurface(exp, fv, 2, false);
				geoElements[0].setLabel(label);
				return geoElements;
			}
		}
		if (ev instanceof Vector3DValue && fv.length < 3) {
			if (fv.length == 2) {
				GeoElement[] geoElements = processSurface(exp, fv, 3, false);
				geoElements[0].setLabel(label);
				return geoElements;
			}
			GeoNumeric loc = getLocalVar(exp, fv[0]);
			if (exp.getOperation().isIf()) {
				ExpressionNode exp1 = exp.getRightTree();
				ExpressionNode cx = VectorArithmetic.computeCoord(exp1, 0);
				ExpressionNode cy = VectorArithmetic.computeCoord(exp1, 1);
				ExpressionNode cz = VectorArithmetic.computeCoord(exp1, 2);
				return cartesianCurve(cons, label, exp1, loc,
						new ExpressionNode[] { cx, cy, cz }, exp.getLeftTree());
			}
			ExpressionNode cx = VectorArithmetic.computeCoord(exp, 0);
			ExpressionNode cy = VectorArithmetic.computeCoord(exp, 1);
			ExpressionNode cz = VectorArithmetic.computeCoord(exp, 2);

			ExpressionValue[] coefX = new ExpressionValue[5];
			ExpressionValue[] coefY = new ExpressionValue[5];
			ExpressionValue[] coefZ = new ExpressionValue[5];

			if (ap.getTrigCoeffs(cx, coefX, new ExpressionNode(kernel, 1.0),
					loc)
					&& ap.getTrigCoeffs(cy, coefY,
							new ExpressionNode(kernel, 1.0), loc)
					&& ap.getTrigCoeffs(cz, coefZ,
							new ExpressionNode(kernel, 1.0), loc)) {
				boolean constant = true;
				for (int i = 0; i < coefX.length; i++) {
					// coefX[i] = expr(coefX[i]);
					// coefY[i] = expr(coefY[i]);
					// coefZ[i] = expr(coefZ[i]);
					constant = constant && expr(coefX[i]).isConstant()
							&& expr(coefY[i]).isConstant()
							&& expr(coefZ[i]).isConstant();
				}
				if (constant) {
					GeoConic3D conic = new GeoConic3D(kernel.getConstruction());
					updateTrigConic(conic, coefX, coefY, coefZ);
					conic.toParametric(fv[0].getSetVarString());
					conic.setDefinition(buildParamEq(exp));
					conic.setLabel(label);

					return new GeoElement[] { conic };
				}
				return dependentConic(cons, exp, coefX, coefY, coefZ, label,
						fv[0], true);
			}
			for (int i = 0; i < coefX.length; i++) {
				coefX[i] = new ExpressionNode(kernel, 0);
				coefY[i] = new ExpressionNode(kernel, 0);
				coefZ[i] = new ExpressionNode(kernel, 0);
			}
			int degX = ap.getPolyCoeffs(cx, coefX,
					new ExpressionNode(kernel, 1), loc);
			int degY = ap.getPolyCoeffs(cy, coefY,
					new ExpressionNode(kernel, 1), loc);
			int degZ = ap.getPolyCoeffs(cz, coefZ,
					new ExpressionNode(kernel, 1), loc);

			if ((degX >= 0 && degY >= 0 && degZ >= 0)
					&& (degX < 2 && degY < 2 && degZ < 2)) {
				GeoLine3D line;
				if (coefX[0].isConstant() && coefY[0].isConstant()
						&& coefZ[0].isConstant() && coefX[1].isConstant()
						&& coefY[1].isConstant() && coefZ[1].isConstant()) {
					line = new GeoLine3D(cons);
					line.showUndefinedInAlgebraView(true);
					Coords start = new Coords(
							new double[] { coefX[0].evaluateDouble(),
									coefY[0].evaluateDouble(),
									coefZ[0].evaluateDouble() });
					Coords v = new Coords(
							new double[] { coefX[1].evaluateDouble(),
									coefY[1].evaluateDouble(),
									coefZ[1].evaluateDouble() });
					line.setCoord(start, v);
					line.setToParametric(fv[0].getSetVarString());
					if (info.isLabelOutput()) {
						line.setLabel(label);
					}
				} else {
					line = (GeoLine3D) kernel.getManager3D().line3D(label,
							coefX, coefY, coefZ);

				}
				line.setToParametric(Unicode.lambda_STRING);
				return new GeoElement[] { line };

			}
			if ((degX >= 0 && degY >= 0 && degZ >= 0)
					&& (degX < 3 && degY < 3 && degZ < 3)) {

				boolean constant = true;
				for (int i = 0; i < coefX.length; i++) {
					// coefX[i] = expr(coefX[i]);
					// coefY[i] = expr(coefY[i]);
					// coefZ[i] = expr(coefZ[i]);
					constant = constant && expr(coefX[i]).isConstant()
							&& expr(coefY[i]).isConstant()
							&& expr(coefZ[i]).isConstant();
				}
				if (constant) {
					GeoConic3D conic = new GeoConic3D(kernel.getConstruction());
					updateParabola(conic, coefX, coefY, coefZ);
					conic.toParametric(fv[0].getSetVarString());
					conic.setDefinition(buildParamEq(exp));
					conic.setLabel(label);
					return new GeoElement[] { conic };
				}
				//
				return dependentConic(cons, exp, coefX, coefY, coefZ, label,
						fv[0], false);

			}
			return cartesianCurve(cons, label, exp, loc,
					new ExpressionNode[] { cx, cy, cz }, null);
		}
		return super.processParametricFunction(exp, ev, fv, label, info);

	}

	@Override
	protected AlgoCurveCartesian makeCurveAlgo(Construction cons,
			ExpressionNode wrap, GeoNumberValue[] coords, GeoNumeric locVar,
			GeoNumberValue from, GeoNumberValue to) {
		if (coords.length == 2) {
			return super.makeCurveAlgo(cons, wrap, coords, locVar, from, to);
		}
		return new AlgoCurveCartesian3D(cons, wrap, coords, locVar, from, to);
	}

	private GeoElement[] dependentConic(Construction cons, ExpressionNode exp,
			ExpressionValue[] coefX, ExpressionValue[] coefY,
			ExpressionValue[] coefZ, String label, FunctionVariable fv0,
			boolean trig) {
		AlgoDependentConic3D ellipseHyperbolaAlgo = new AlgoDependentConic3D(
				cons, buildParamEq(exp), coefX, coefY, coefZ, trig);
		ellipseHyperbolaAlgo.getConic3D().setLabel(label);
		ellipseHyperbolaAlgo.getConic3D().toParametric(fv0.getSetVarString());
		return new GeoElement[] { ellipseHyperbolaAlgo.getConic3D() };
	}

	/**
	 * @param conic
	 *            conic
	 * @param coefX
	 *            coefficients for (sin,cos,1) of x coord
	 * @param coefY
	 *            coefficients for (sin,cos,1) of y coord
	 * @param coefZ
	 *            coefficients for (sin,cos,1) of z coord
	 */
	public static void updateTrigConic(GeoConic3D conic,
			ExpressionValue[] coefX, ExpressionValue[] coefY,
			ExpressionValue[] coefZ) {
		CoordSys cs = new CoordSys(2);

		double mx = eval(coefX[0]), my = eval(coefY[0]), mz = eval(coefZ[0]);

		double xx = 0, xy = 0, yy = 0, det = 0;

		if (coefX[1] != null || coefX[2] != null) {
			double vx = eval(coefX[1]), vy = eval(coefY[1]),
					vz = eval(coefZ[1]);

			double wx = eval(coefX[2]), wy = eval(coefY[2]),
					wz = eval(coefZ[2]);
			cs.resetCoordSys();
			cs.addPoint(new Coords(mx, my, mz, 1));
			cs.addVector(new Coords(vx, vy, vz));
			cs.addVector(new Coords(wx, wy, wz));
			cs.makeOrthoMatrix(false, false);

			Coords v = cs.getNormalProjection(new Coords(vx, vy, vz, 0))[1];
			Coords w = cs.getNormalProjection(new Coords(wx, wy, wz, 0))[1];
			yy = v.getX() * v.getX() + w.getX() * w.getX();
			xx = v.getY() * v.getY() + w.getY() * w.getY();
			xy = v.getX() * v.getY() + w.getX() * w.getY();
			det = v.getX() * w.getY() - w.getX() * v.getY();

		} else if (coefX[3] != null || coefX[4] != null) {
			double vx = eval(coefX[3]), vy = eval(coefY[3]),
					vz = eval(coefZ[3]);

			double wx = eval(coefX[4]), wy = eval(coefY[4]),
					wz = eval(coefZ[4]);
			cs.resetCoordSys();
			cs.addPoint(new Coords(mx, my, mz, 1));
			cs.addVector(new Coords(vx, vy, vz));
			cs.addVector(new Coords(wx, wy, wz));
			cs.makeOrthoMatrix(false, false);

			Coords v = cs.getNormalProjection(new Coords(vx, vy, vz, 0))[1];
			Coords w = cs.getNormalProjection(new Coords(wx, wy, wz, 0))[1];
			yy = v.getX() * v.getX() - w.getX() * w.getX();
			xx = v.getY() * v.getY() - w.getY() * w.getY();
			xy = v.getX() * v.getY() - w.getX() * w.getY();
			det = v.getX() * w.getY() - w.getX() * v.getY();

		}

		conic.setCoordSys(cs);
		conic.setMatrix(new double[] { xx, yy, -det * det, -xy, 0, 0 });

	}

	private static double eval(ExpressionValue ev) {
		if (ev == null) {
			return 0;
		}
		return ev.evaluateDouble();

	}

	/**
	 * @param conic
	 *            conic
	 * @param coefX
	 *            coefficients of x as t-polynomial
	 * @param coefY
	 *            coefficients of y as t-polynomial
	 * @param coefZ
	 *            coefficients of z as t-polynomial
	 */
	public static void updateParabola(GeoConic3D conic, ExpressionValue[] coefX,
			ExpressionValue[] coefY, ExpressionValue[] coefZ) {
		Kernel kernel = conic.getKernel();
		double mx = eval(coefX[0]), my = eval(coefY[0]), mz = eval(coefZ[0]);
		double vx = eval(coefX[1]), vy = eval(coefY[1]), vz = eval(coefZ[1]);

		double wx = eval(coefX[2]), wy = eval(coefY[2]), wz = eval(coefZ[2]);
		CoordSys cs = new CoordSys(2);
		cs.resetCoordSys();
		cs.addPoint(new Coords(mx, my, mz, 1));
		cs.addVector(new Coords(vx, vy, vz));
		cs.addVector(new Coords(wx, wy, wz));
		cs.makeOrthoMatrix(false, false);
		Coords v = cs.getNormalProjection(new Coords(vx, vy, vz, 0))[1];
		Coords w = cs.getNormalProjection(new Coords(wx, wy, wz, 0))[1];

		FunctionVariable px = new FunctionVariable(kernel, "x");
		FunctionVariable py = new FunctionVariable(kernel, "y");
		ExpressionNode t = px.wrap().multiply(w.getY())
				.subtract(py.wrap().multiply(w.getX()));

		double d = w.getY() * v.getX() - w.getX() * v.getY();

		Equation eq;

		// Numerically unstable
		eq = new Equation(kernel,
				px.wrap().multiply(d * d * w.getX())
						.plus(py.wrap().multiply(d * d * w.getY())),
				t.power(2).multiply(w.getX() * w.getX() + w.getY() * w.getY())
						.plus(t.multiply(

								w.getY() * v.getY() + v.getX() * w.getX())
								.multiply(d))

		);
		eq.setForceConic();
		Log.debug("3D proc");

		eq.initEquation();
		Polynomial lhs = eq.getNormalForm();
		double xx = lhs.getCoeffValue("xx");
		double xy = lhs.getCoeffValue("xy");
		double yy = lhs.getCoeffValue("yy");
		double x = lhs.getCoeffValue("x");
		double y = lhs.getCoeffValue("y");
		double cst = lhs.getCoeffValue("");

		Log.debug("3D proc done");

		conic.setCoordSys(cs);
		// conic.setMatrix(new double[] { xx, yy, -det * det, -xy, 0,
		// 0
		// });
		conic.setMatrix(new double[] { xx, yy, cst, xy / 2, x / 2, y / 2 });
	}

}
