package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoSurfaceCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.ParametricProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;

public class ParametricProcessor3D extends ParametricProcessor {

	public ParametricProcessor3D(Kernel kernel, AlgebraProcessor ap) {
		super(kernel, ap);
	}

	@Override
	protected GeoElement[] processParametricFunction(ExpressionNode exp,
			ExpressionValue ev, FunctionVariable[] fv, String label) {
		Construction cons = kernel.getConstruction();
		if (ev instanceof Vector3DValue) {
			if (fv.length == 2) {
				return processSurface(exp, fv, label);
			}
			GeoNumeric loc = getLocalVar(exp, fv[0]);

			ExpressionNode cx = ap.computeCoord(exp, 0);
			ExpressionNode cy = ap.computeCoord(exp, 1);
			ExpressionNode cz = ap.computeCoord(exp, 2);

			ExpressionValue[] coefX = new ExpressionValue[5];
			ExpressionValue[] coefY = new ExpressionValue[5];
			ExpressionValue[] coefZ = new ExpressionValue[5];

			if (kernel.getApplication().has(Feature.FREE_3DCONICS)
					&& ap.getTrigCoeffs(cx, coefX,
							new ExpressionNode(kernel, 1.0), loc)
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
				constant = true;
				CoordSys cs = new CoordSys(2);

				double mx = expr(coefX[0]).evaluateDouble(), my = expr(coefY[0])
						.evaluateDouble(), mz = expr(coefZ[0]).evaluateDouble();
				App.debug(coefX[2] + "," + coefX[1]);

				double xx = 0, xy = 0, yy = 0, det = 0;
				if (constant && (coefX[1] != null || coefX[2] != null)) {
					double vx = expr(coefX[1]).evaluateDouble(), vy = expr(
							coefY[1]).evaluateDouble(), vz = expr(coefZ[1])
							.evaluateDouble();

					double wx = expr(coefX[2]).evaluateDouble(), wy = expr(
							coefY[2]).evaluateDouble(), wz = expr(coefZ[2])
							.evaluateDouble();
					cs.resetCoordSys();
					cs.addPoint(new Coords(mx, my, mz, 1));
					cs.addVector(new Coords(vx, vy, vz));
					cs.addVector(new Coords(wx, wy, wz));
					cs.makeOrthoMatrix(false, false);

					Coords v = cs
							.getNormalProjection(new Coords(vx, vy, vz, 0))[1];
					Coords w = cs
							.getNormalProjection(new Coords(wx, wy, wz, 0))[1];
					yy = v.getX() * v.getX() + w.getX() * w.getX();
					xx = v.getY() * v.getY() + w.getY() * w.getY();
					xy = v.getX() * v.getY() + w.getX() * w.getY();
					det = v.getX() * w.getY() - w.getX() * v.getY();

				} else if (constant && (coefX[3] != null || coefX[4] != null)) {
					double vx = expr(coefX[3]).evaluateDouble(), vy = expr(
							coefY[3]).evaluateDouble(), vz = expr(coefZ[3])
							.evaluateDouble();

					double wx = expr(coefX[4]).evaluateDouble(), wy = expr(
							coefY[4]).evaluateDouble(), wz = expr(coefZ[4])
							.evaluateDouble();
					cs.resetCoordSys();
					cs.addPoint(new Coords(mx, my, mz, 1));
					cs.addVector(new Coords(vx, vy, vz));
					cs.addVector(new Coords(wx, wy, wz));
					cs.makeOrthoMatrix(false, false);

					Coords v = cs
							.getNormalProjection(new Coords(vx, vy, vz, 0))[1];
					Coords w = cs
							.getNormalProjection(new Coords(wx, wy, wz, 0))[1];
					yy = v.getX() * v.getX() - w.getX() * w.getX();
					xx = v.getY() * v.getY() - w.getY() * w.getY();
					xy = v.getX() * v.getY() - w.getX() * w.getY();
					det = v.getX() * w.getY() - w.getX() * v.getY();

				}
				GeoConic3D conic = new GeoConic3D(kernel.getConstruction());
				conic.setCoordSys(cs);
				conic.setMatrix(new double[] { xx, yy, -det * det, -xy, 0, 0 });
				conic.setLabel(label);

				return new GeoElement[] { conic };
			}
			for (int i = 0; i < coefX.length; i++) {
				coefX[i] = new ExpressionNode(kernel, 0);
				coefY[i] = new ExpressionNode(kernel, 0);
				coefZ[i] = new ExpressionNode(kernel, 0);
			}
			int degX = ap.getPolyCoeffs(cx, coefX,
					new ExpressionNode(kernel, 1),
					loc);
			int degY = ap.getPolyCoeffs(cy, coefY,
					new ExpressionNode(kernel, 1),
					loc);
			int degZ = ap.getPolyCoeffs(cz, coefZ,
					new ExpressionNode(kernel, 1),
					loc);

			if ((degX >= 0 && degY >= 0 && degZ >= 0)
					&& (degX < 2 && degY < 2 && degZ < 2)) {
				/*
				 * if (P.isGeoElement3D() || v.isGeoElement3D()) { if
				 * (isConstant) { line = new GeoLine3D(cons); ((GeoLine3D)
				 * line).setCoord(P.getCoordsInD3(),v.getCoordsInD3());
				 * line.setLabel(par.getLabel()); }else{ line =
				 * kernel.getManager3D().Line3D(par.getLabel(), P, v); } } else
				 * { line = Line(par, (GeoPoint) P, (GeoVector) v, isConstant);
				 * }
				 */
				GeoLine3D line = new GeoLine3D(cons);
				if (coefX[0].isConstant() && coefY[0].isConstant()
						&& coefZ[0].isConstant() && coefX[1].isConstant()
						&& coefY[1].isConstant() && coefZ[1].isConstant()) {

					Coords start = new Coords(new double[] {
							coefX[0].evaluateDouble(),
							coefY[0].evaluateDouble(),
							coefZ[0].evaluateDouble() });
					Coords v = new Coords(new double[] {
							coefX[1].evaluateDouble(),
							coefY[1].evaluateDouble(),
							coefZ[1].evaluateDouble() });
					line.setCoord(start, v);
					line.setToParametric(fv[0].getSetVarString());
					line.setLabel(label);
				} else {
					line = (GeoLine3D) kernel.getManager3D().Line3D(label,
							coefX, coefY, coefZ);

				}
				line.setToParametric(fv[0].getSetVarString());
				return new GeoElement[] { line };

			}
			AlgoDependentNumber nx = new AlgoDependentNumber(cons, cx, false);
			cons.removeFromConstructionList(nx);
			AlgoDependentNumber ny = new AlgoDependentNumber(cons, cy, false);
			cons.removeFromConstructionList(ny);
			AlgoDependentNumber nz = new AlgoDependentNumber(cons, cz, false);
			cons.removeFromConstructionList(nz);
			GeoNumeric from = new GeoNumeric(cons, -10);
			GeoNumeric to = new GeoNumeric(cons, 10);
			AlgoCurveCartesian ac = new AlgoCurveCartesian3D(cons, exp,
					new NumberValue[] { nx.getNumber(), ny.getNumber(),
							nz.getNumber() }, loc, from, to);
			ac.getCurve().setLabel(label);
			return ac.getOutput();
		}
		return super.processParametricFunction(exp, ev, fv, label);

	}

	private GeoElement[] processSurface(ExpressionNode exp,
			FunctionVariable[] fv, String label) {
		GeoNumeric loc0 = getLocalVar(exp, fv[0]);
		GeoNumeric loc1 = getLocalVar(exp, fv[1]);
		Construction cons = kernel.getConstruction();
		ExpressionNode cx = ap.computeCoord(exp, 0);
		ExpressionNode cy = ap.computeCoord(exp, 1);
		ExpressionNode cz = ap.computeCoord(exp, 2);
		AlgoDependentNumber nx = new AlgoDependentNumber(cons, cx, false);
		cons.removeFromConstructionList(nx);
		AlgoDependentNumber ny = new AlgoDependentNumber(cons, cy, false);
		cons.removeFromConstructionList(ny);
		AlgoDependentNumber nz = new AlgoDependentNumber(cons, cz, false);
		cons.removeFromConstructionList(nz);
		Log.debug("VAR" + loc1);
		Log.debug("VAR" + loc0);
		AlgoSurfaceCartesian3D algo = new AlgoSurfaceCartesian3D(cons, label,
				exp, new NumberValue[] { nx.getNumber(), ny.getNumber(),
						nz.getNumber() }, new GeoNumeric[] { loc0,
						loc1 }, new NumberValue[] {
						num(-10), num(-10) }, new NumberValue[] { num(10),
						num(10) });
		return algo.getOutput();
	}

	private NumberValue num(double d) {
		return new GeoNumeric(kernel.getConstruction(), d);
	}

}
