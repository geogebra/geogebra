package org.geogebra.common.geogebra3D.kernel3D.scripting;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * ZoomIn
 */
public class CmdSetViewDirection extends CmdScripting {
	/**
	 * Creates new ZooomOut command
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetViewDirection(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof GeoDirectionND) {
				GeoDirectionND d = (GeoDirectionND) arg[0];

				EuclidianView3DInterface view3D = app.getEuclidianView3D();

				Coords v = d.getDirectionInD3();
				if (v != null) {
					view3D.setClosestRotAnimation(v);
				}

				return;

			}

			if (arg[0].isGeoPoint()) {
				GeoPointND p = (GeoPointND) arg[0];

				if (p.isDefined()) {
					Coords v = p.getInhomCoordsInD3();
					v.setW(0);
					EuclidianView3DInterface view3D = app.getEuclidianView3D();
					view3D.setClosestRotAnimation(v);

				}

				return;

			}

			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
