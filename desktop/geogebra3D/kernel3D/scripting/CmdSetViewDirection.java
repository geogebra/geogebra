package geogebra3D.kernel3D.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.main.MyError;
import geogebra3D.App3D;
import geogebra3D.euclidian3D.EuclidianView3D;


/**
 *ZoomIn
 */
public class CmdSetViewDirection extends CmdScripting {
	/**
	 * Creates new ZooomOut command
	 * @param kernel kernel
	 */
	public CmdSetViewDirection(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof GeoDirectionND) {
				GeoDirectionND d = (GeoDirectionND) arg[0];
				
				EuclidianView3D view3D = ((App3D) app).getEuclidianView3D();
				

				Coords v = d.getDirectionInD3();
				if (v!=null){					
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
