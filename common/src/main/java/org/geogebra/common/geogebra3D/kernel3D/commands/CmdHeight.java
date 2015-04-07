package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.HasHeight;
import org.geogebra.common.main.MyError;

/**
 * OrientedHeight[ Cone ], etc.
 * 
 * @author mathieu
 *
 */
public class CmdHeight extends CommandProcessor {

	public CmdHeight(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			if (arg[0] instanceof HasHeight) {
				return new GeoElement[] { kernelA.getManager3D()
						.OrientedHeight(c.getLabel(), (HasHeight) arg[0]) };
			}

			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}

	}

}
