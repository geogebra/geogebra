package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDrawingPadCorner;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;

/**
 * SlowPlot
 */
public class CmdSlowPlot extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSlowPlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (arg[0].isGeoFunctionable()) {

				GeoNumeric var = new GeoNumeric(cons, 0.0);

				arg[0].setEuclidianVisible(false);
				arg[0].update();

				var.setLabel(null); // set label to next available
				var.setEuclidianVisible(true);
				var.setIntervalMin(0.0);
				var.setIntervalMax(1.0);
				var.setAnimating(true);
				var.setAnimationStep(0.01);
				var.setAnimationType(GeoElement.ANIMATION_INCREASING);
				var.update();
				FunctionVariable x = new FunctionVariable(cons.getKernel());
				GeoElement corner1 = new AlgoDrawingPadCorner(cons,
						new GeoNumeric(cons, 1), null,
 5).getOutput(0);
				GeoElement corner2 = new AlgoDrawingPadCorner(cons,
						new GeoNumeric(cons, 2), null, 5).getOutput(0);
				ExpressionNode exp = x
						.wrap()
						.lessThan(
								var.wrap()
										.multiply(
												corner2.wrap()
														.subtract(corner1)
														.apply(Operation.XCOORD))
										.plus(corner1.wrap().apply(
												Operation.XCOORD)))
						.apply(Operation.IF,
								arg[0].wrap().apply(Operation.FUNCTION, x));
				GeoFunction g = cons.getKernel().getAlgoDispatcher()
						.DependentFunction(new Function(exp, x),
								new EvalInfo(true));
				String label = c.getLabel();
				if (g.validate(label == null)) {
					g.setLabel(label);
				} else {
					var.remove();
					throw new MyError(loc, "InvalidFunction");
				}


				kernelA.getAnimatonManager().startAnimation();
				return new GeoElement[] { g };
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
