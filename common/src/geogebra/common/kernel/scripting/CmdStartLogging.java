package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.common.plugin.SensorLogger;

public class CmdStartLogging extends CmdScripting {
	public CmdStartLogging(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void perform(Command c) {

		int n = c.getArgumentNumber();

		// ignore last parameter if odd
		// n = (n / 2) * 2;

		arg = resArgs(c);

		boolean success = false;

		SensorLogger logger = app.getSensorLogger();
		if (logger != null) {

			logger.stopLogging();

			GeoElement text;
			GeoElement argument = null;
			GeoElement limit = null;

			for (int i = 0; i <= n - 2; i += 2) {
				argument = arg[i + 1];
				if ((text = arg[i]) instanceof GeoText
						&& (argument instanceof GeoNumeric || argument instanceof GeoText)) {
					logger.registerGeo(((GeoText) text).getTextString(),
							argument);
				} else if ((text = arg[i]) instanceof GeoText
						&& argument instanceof GeoList) {
					// it should be possible to add an optional third parameter
					// to lists - size limit of logging
					if ((i < n - 2)
							&& (limit = arg[i + 2]) instanceof GeoNumeric) {
						logger.registerGeoList(
								((GeoText) text).getTextString(),
								(GeoList) argument,
								((GeoNumeric) limit).getValue());
						i++;
					} else {
						logger.registerGeoList(
								((GeoText) text).getTextString(),
								(GeoList) argument);
					}
				} else {
					throw argErr(app, c.getName(),
							(text instanceof GeoText) ? argument : text);
				}
			}

			success = logger.startLogging();
		}

		if (!success) {
			throw new MyError(loc, loc.getError("NoLogging"));
		}
	}
}
