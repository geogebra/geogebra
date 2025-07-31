package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
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

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdHeight(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);

			if (arg[0] instanceof HasHeight) {
				return new GeoElement[] { kernel.getManager3D()
						.orientedHeight(c.getLabel(), (HasHeight) arg[0]) };
			}

			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}

	}

}
