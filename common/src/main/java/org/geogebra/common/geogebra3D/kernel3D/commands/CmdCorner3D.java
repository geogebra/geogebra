package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicSection;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * 
 * Corner[ <Conic section> ]
 * 
 * @author mathieu
 *
 */
public class CmdCorner3D extends CmdVertex3D {

	public CmdCorner3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		if (n == 1) {
			arg = resArgs(c);
			if (arg[0] instanceof GeoConicSection) {

				return kernelA.getManager3D().Corner(c.getLabels(),
						(GeoConicSection) arg[0]);
			}
		}

		return super.process(c);
	}

}
