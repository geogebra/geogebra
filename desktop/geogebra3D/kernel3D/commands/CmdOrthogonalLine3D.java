package geogebra3D.kernel3D.commands;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdOrthogonalLine;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;

/*
 * Orthogonal[ <GeoPoint3D>, <GeoCoordSys> ]
 */
public class CmdOrthogonalLine3D extends CmdOrthogonalLine {
	
	
	
	public CmdOrthogonalLine3D(Kernel kernel) {
		super(kernel);
	}
	
	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {
	    case 2 :
	    	arg = resArgs(c);
	    	if (
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
	    	}else if (((ok[0] = (arg[0] .isGeoPoint() ) )
	    			&& (ok[1] = (arg[1] instanceof GeoLineND )))){
	    		
	    		//first check if it's an input line call, with 2D/3D view active
	    		EuclidianViewInterfaceCommon view = app.getActiveEuclidianView();
	    		if (!kernelA.getLoadingMode() && view!=null){
	    			if (app.getActiveEuclidianView().isDefault2D()){
	    				//xOy view is active : force parallel to xOy plane
	    				GeoElement[] ret =
	    	    			{
	    	    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    	    						c.getLabel(),
	    	    						(GeoPointND) arg[0],
	    	    						(GeoLineND) arg[1],
	    	    						(GeoDirectionND) kernelA.getXOYPlane())};
	    	    		return ret;
	    			}
	    			
	    			if (view instanceof EuclidianViewForPlane){
	    				//plane view is active : force parallel to the plane
	    				GeoElement[] ret =
	    	    			{
	    	    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    	    						c.getLabel(),
	    	    						(GeoPointND) arg[0],
	    	    						(GeoLineND) arg[1],
	    	    						((EuclidianViewForPlane) view).getPlane())};
	    	    		return ret;
	    			}
	    			
	    			//3D view is active : force "in space"
	    			GeoElement[] ret =
	    				{
	    					(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    							c.getLabel(),
	    							(GeoPointND) arg[0],
	    							(GeoLineND) arg[1])};
	    			return ret;
	    			
	    		}
	    		
	    		
	    		//check if there is a 3D geo: then use 3D algo
	    		if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()){
	    			GeoElement[] ret =
	    				{
	    					(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    							c.getLabel(),
	    							(GeoPointND) arg[0],
	    							(GeoLineND) arg[1])};
	    			return ret;
	    		}

	    		//else use 3D algo, parallel to xOyPlane
	    		GeoElement[] ret =
	    			{
	    				(GeoElement) kernelA.getManager3D().OrthogonalLine3D(
	    						c.getLabel(),
	    						(GeoPointND) arg[0],
	    						(GeoLineND) arg[1],
	    						(GeoDirectionND) kernelA.getXOYPlane())};
	    		return ret;

	    	}else if (
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
	    	break;
	    
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
	    	break;
	    }
	    

	    return super.process(c);
	}
	
}
