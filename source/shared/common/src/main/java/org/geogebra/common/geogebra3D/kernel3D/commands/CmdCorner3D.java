package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicSection;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * 
 * Vertex[ &lt;Conic section&gt; ]
 * 
 * @author mathieu
 *
 */
public class CmdCorner3D extends CmdVertex3D {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdCorner3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		if (n == 1) {
			arg = resArgs(c, info);
			if (arg[0] instanceof GeoConicSection) {

				return kernel.getManager3D().corner(c.getLabels(),
						(GeoConicSection) arg[0]);
			}
		}

		return super.process(c, info);
	}

}
