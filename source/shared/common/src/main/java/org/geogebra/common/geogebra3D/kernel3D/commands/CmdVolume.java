package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.HasVolume;
import org.geogebra.common.main.MyError;

/**
 * Volume[ Sphere ], etc.
 * 
 * @author mathieu
 *
 */
public class CmdVolume extends CommandProcessor {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdVolume(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);

			if (arg[0] instanceof HasVolume) {
				return new GeoElement[] { kernel.getManager3D()
						.volume(c.getLabel(), (HasVolume) arg[0]) };
			}

			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}

	}

}
