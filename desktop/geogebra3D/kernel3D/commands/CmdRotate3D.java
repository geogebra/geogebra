package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdRotate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

/**
 * @author mathieu
 * 
 * Extends rotation for 3D objects
 * 
 * Rotate[ <GeoPoint>, <NumberValue> ]
 *
 */
public class CmdRotate3D extends CmdRotate{

	/**
	 * default constructor
	 * @param kernel kernel
	 */
	public CmdRotate3D(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		switch (n) {
		case 2:
			// ROTATE AS IN 2D
			arg = resArgs(c);			
			return super.process2(c,arg,ok);

		case 3:
			// ROTATION AROUND POINT (AND XOY PLANE)
			arg = resArgs(c);

			//first check if rotated geo is 3D element
			if ((arg[2].isGeoElement3D())
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {

				return kernelA.getManager3D().Rotate3D(c.getLabel(), 
						arg[0], (GeoNumberValue) arg[1], (GeoPointND) arg[2], (GeoDirectionND) kernelA.getXOYPlane());


			}

			// ROTATION AROUND LINE
			if ((ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2] instanceof GeoLineND))) {

				return kernelA.getManager3D().Rotate3D(c.getLabel(), 
						arg[0], (GeoNumberValue) arg[1], (GeoLineND) arg[2]);


			}
			

			
			
			return super.process3(c, arg, ok);


		case 4:
			// ROTATION AROUND POINT AND DIRECTION
			arg = resArgs(c);

			// rotate point
			if ((ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoPoint())) && (ok[3] = (arg[3] instanceof GeoDirectionND))) {

				return kernelA.getManager3D().Rotate3D(c.getLabel(), 
						arg[0], (GeoNumberValue) arg[1], (GeoPointND) arg[2], (GeoDirectionND) arg[3]);
			}

			throw argErr(app, c.getName(), getBadArg(ok,arg));


			


		}
		
		return super.process(c);

	}

}
