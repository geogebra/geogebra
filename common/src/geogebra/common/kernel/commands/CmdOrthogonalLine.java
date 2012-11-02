package geogebra.common.kernel.commands;


import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoOrthoLinePointConic;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.main.MyError;




/**
 * Orthogonal[ <GeoPoint>, <GeoVector> ] Orthogonal[ <GeoPoint>, <GeoLine> ]
 */
public class CmdOrthogonalLine extends CommandProcessor {

	/**
	* Create new command processor
	* @param kernel kernel
	*/
	public CmdOrthogonalLine(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2 :
			arg = resArgs(c);

			// line through point orthogonal to vector
			if ((ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoVector()))) {
				GeoElement[] ret =
				{
						getAlgoDispatcher().OrthogonalLine(
								c.getLabel(),
								(GeoPoint) arg[0],
								(GeoVector) arg[1])};
				return ret;
			}

			// line through point orthogonal to another line
			else if (
					(ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoLine()))) {
				if (//check if at least one arg is 3D (else use super method)
						(arg[0].isGeoElement3D() || arg[1].isGeoElement3D())){
					
					//ensure backward compatibility
					if (GeoGebraConstants.IS_PRE_RELEASE && app.fileVersionBefore(App.getSubValues("4.9.68.0"))){
						GeoElement[] ret =
				    		{
				    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
				    						c.getLabel(),
				    						(GeoPointND) arg[0],
				    						(GeoLineND) arg[1])};
				    		return ret;
					}
					
					//new command behavior : shortcut to "parallel to xOy plane"
					GeoElement[] ret =
			    		{
			    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
			    						c.getLabel(),
			    						(GeoPointND) arg[0],
			    						(GeoLineND) arg[1],
			    						(GeoDirectionND) kernelA.getXOYPlane())};
			    		return ret;
				}//else: 2D geos
				
				GeoElement[] ret =
				{
						getAlgoDispatcher().OrthogonalLine(
								c.getLabel(),
								(GeoPoint) arg[0],
								(GeoLine) arg[1])};
				return ret;
				
			}
			
			// line through point orthogonal to conic
			else if (
					(ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoConic()))) {
				
				AlgoOrthoLinePointConic algo = new AlgoOrthoLinePointConic(cons, c.getLabel(),
						(GeoPoint) arg[0],
						(GeoConic) arg[1]);

				return algo.getOutput();
			}
			
			// line through point orthogonal to plane
			else if (
	    			(ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] instanceof GeoCoordSys2D ))
	    	) {
	    		GeoElement[] ret =
	    		{
	    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(GeoCoordSys2D) arg[1])};
	    		return ret;
	    	}
			
			// line orthogonal to two lines
			else if (
	    			((ok[0] = (arg[0] instanceof GeoLineND ) )
	    			&& (ok[1] = (arg[1] instanceof GeoLineND )))
	    	) {
	    		GeoElement[] ret =
	    		{
	    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    						c.getLabel(),
	    						(GeoLineND) arg[0],
	    						(GeoLineND) arg[1])};
	    		return ret;
	    	}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}
			

		case 3 :
			arg = resArgs(c);
			if (
					(ok[0] = (arg[0] .isGeoPoint() ) )
					&& (ok[1] = (arg[1] instanceof GeoLineND ))
					&& (ok[2] = (arg[2] instanceof GeoDirectionND ))
					) {
				GeoElement[] ret =
					{
						(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
								c.getLabel(),
								(GeoPointND) arg[0],
								(GeoLineND) arg[1],
								(GeoDirectionND) arg[2])};
				return ret;
			}

			// syntax error
			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			throw argErr(app, c.getName(), arg[2]);

		default :
			throw argNumErr(app, c.getName(), n);
		}
	}
}


