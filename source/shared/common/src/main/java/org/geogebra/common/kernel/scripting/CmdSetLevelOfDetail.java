package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable.LevelOfDetail;
import org.geogebra.common.main.MyError;

/**
 * Level of detail (for 3D surfaces)
 */
public class CmdSetLevelOfDetail extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLevelOfDetail(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);
			if (arg[1].isNumberValue()) {
				if (arg[0] instanceof SurfaceEvaluable) {
					int lod = (int) arg[1].evaluateDouble();
					SurfaceEvaluable se = (SurfaceEvaluable) arg[0];
					if (lod >= 1) {
						se.setLevelOfDetail(LevelOfDetail.QUALITY);
					} else {
						se.setLevelOfDetail(LevelOfDetail.SPEED);
					}
				}
				return arg;
			}
			throw argErr(c, arg[1]);

		default:
			throw argNumErr(c);
		}
	}
}
