package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.AlgoParseToNumberOrFunction;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * ParseToNumber
 */
public class CmdParseToNumber extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParseToNumber(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		if (!info.isScripting() && n == 2) {
			return new GeoElement[0];
		}
		GeoElement[] arg = resArgs(c);
		boolean ok;
		switch (n) {
		case 1:
			if (arg[0] instanceof GeoText) {
				AlgoParseToNumberOrFunction
						algo = new AlgoParseToNumberOrFunction(cons, (GeoText) arg[0], null,
						Commands.ParseToNumber);
				algo.getOutput(0).setLabel(c.getLabel());
				return algo.getOutput();
			}
			throw argErr(arg[0], c);
		case 2:
			if ((ok = arg[0].isGeoNumeric()) && arg[1].isGeoText()) {

				GeoNumeric num = (GeoNumeric) arg[0];
				String str = ((GeoText) arg[1]).getTextStringSafe();

				try {
					kernel.getAlgebraProcessor().evaluateToDouble(str, true,
							num);
					num.updateCascade();
				} catch (Exception e) {
					num.setUndefined();
					num.updateCascade();
				}

				return num.asArray();
			} else if (!ok) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
