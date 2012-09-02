package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 *SlowPlot
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
				StringTemplate tpl = StringTemplate.maxPrecision;
				StringBuilder sb = new StringBuilder();
				sb.append("Function[");
				sb.append(arg[0].getLabel(tpl));
				sb.append(",x(Corner[1]), x(Corner[1]) (1-");
				sb.append(var.getLabel(tpl));
				sb.append(") + x(Corner(2)) ");
				sb.append(var.getLabel(tpl));
				sb.append("]");

				kernelA.getAnimatonManager().startAnimation();
				try {
					return kernelA.getAlgebraProcessor()
							.processAlgebraCommandNoExceptionHandling(
									sb.toString(), true, false, true);
				} catch (Exception e) {
					e.printStackTrace();
					throw argErr(app, c.getName(), arg[0]);
				} catch (MyError e) {
					e.printStackTrace();
					throw argErr(app, c.getName(), arg[0]);
				}
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
