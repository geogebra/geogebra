package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDrawingPadCorner;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * SlowPlot
 */
public class CmdSlowPlot extends CmdScripting {
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
	public GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		arg = resArgs(c);

		// true
		int repeat = 1;

		switch (n) {
		case 2:
			// true -> 1
			// false -> 0
			// infinity -> 2147483647
			// undefined -> 0
			repeat = (int) arg[1].evaluateDouble();
			Log.debug("repeat = " + repeat);
			//$FALL-THROUGH$
		case 1:
			if (arg[0].isRealValuedFunction()) {

				GeoNumeric var = new GeoNumeric(cons, 0.0);

				arg[0].setEuclidianVisible(false);
				arg[0].update();

				var.setLabel(null); // set label to next available
				var.setEuclidianVisible(true);
				var.setIntervalMin(0.0);
				var.setIntervalMax(1.0);
				var.setAnimating(true);
				var.setAnimationStep(0.01);
				var.setAnimationType(
						repeat == 1 ? GeoElementND.ANIMATION_INCREASING
								: GeoElementND.ANIMATION_INCREASING_ONCE);
				var.update();
				FunctionVariable x = new FunctionVariable(cons.getKernel());
				GeoElement corner1 = new AlgoDrawingPadCorner(cons,
						new GeoNumeric(cons, 1), null, 5).getOutput(0);
				GeoElement corner2 = new AlgoDrawingPadCorner(cons,
						new GeoNumeric(cons, 2), null, 5).getOutput(0);
				ExpressionNode exp = x.wrap()
						.lessThan(var.wrap()
								.multiply(corner2.wrap().subtract(corner1)
										.apply(Operation.XCOORD))
								.plus(corner1.wrap().apply(Operation.XCOORD)))
						.apply(Operation.IF,
								arg[0].wrap().apply(Operation.FUNCTION, x));
				GeoFunction g = cons.getKernel().getAlgoDispatcher()
						.dependentFunction(new Function(exp, x),
								new EvalInfo(true));
				String label = c.getLabel();
				if (g.validate(label == null)) {
					g.setLabel(label);
				} else {
					var.remove();
					throw new MyError(loc, Errors.InvalidFunction);
				}

				kernel.getAnimatonManager().startAnimation();
				return new GeoElement[] { g };
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}

}
