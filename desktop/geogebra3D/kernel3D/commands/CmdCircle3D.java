package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdCircle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;
import geogebra3D.kernel3D.Kernel3D;

public class CmdCircle3D extends CmdCircle {
	
	
	
	
	public CmdCircle3D(Kernel kernel) {
		super(kernel);
	}

	
	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {
	    case 2 :
	    	arg = resArgs(c);
	    	if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D() ){
	    		if ((ok[0] = (arg[0] instanceof GeoLineND))
	    				&& (ok[1] = (arg[1] .isGeoPoint()))) {
	    			GeoElement[] ret =
	    			{
	    					kernelA.getManager3D().Circle3D(
	    							c.getLabel(),
	    							(GeoLineND) arg[0],
	    							(GeoPointND) arg[1])};
	    			return ret;
	    		}else if (ok[0] = (arg[0] .isGeoPoint())){
	    			if(ok[1] = (arg[1] .isNumberValue())){
		    			GeoElement[] ret =
		    				{
		    					kernelA.getManager3D().Circle3D(
		    							c.getLabel(),
		    							(GeoPointND) arg[0],
		    							(NumberValue) arg[1],
		    							(GeoDirectionND) ((Kernel3D) kernelA).getXOYPlane())};
		    			return ret;

		    		}else if(ok[1] = (arg[1] .isGeoPoint())){
		    			GeoElement[] ret =
		    				{
		    					kernelA.getManager3D().Circle3D(
		    							c.getLabel(),
		    							(GeoPointND) arg[0],
		    							(GeoPointND) arg[1],
		    							(GeoDirectionND) ((Kernel3D) kernelA).getXOYPlane())};
		    			return ret;

		    		}
	    		}
	    	
	    	}
	    	break;
	    
	    
	    case 3 :
	    	arg = resArgs(c);
	    	if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D() || arg[2].isGeoElement3D()){
	    		if ((ok[0] = (arg[0] .isGeoPoint()))
	    				&& (ok[1] = (arg[1] .isGeoPoint()))
	    				&& (ok[2] = (arg[2] .isGeoPoint()))) {
	    			GeoElement[] ret =
	    			{
	    					kernelA.getManager3D().Circle3D(
	    							c.getLabel(),
	    							(GeoPointND) arg[0],
	    							(GeoPointND) arg[1],
	    							(GeoPointND) arg[2])};
	    			return ret;
	    		}
	    	}

	    	if ((ok[0] = (arg[0] .isGeoPoint())) && (ok[2] = (arg[2] instanceof GeoDirectionND))){
	    		if (ok[1] = (arg[1] .isNumberValue())){
	    			GeoElement[] ret =
	    			{
	    					kernelA.getManager3D().Circle3D(
	    							c.getLabel(),
	    							(GeoPointND) arg[0],
	    							(NumberValue) arg[1],
	    							(GeoDirectionND) arg[2])};
	    			return ret;
	    		}else if ((ok[1] = (arg[1] .isGeoPoint()))){
	    			GeoElement[] ret =
	    			{
	    					kernelA.getManager3D().Circle3D(
	    							c.getLabel(),
	    							(GeoPointND) arg[0],
	    							(GeoPointND) arg[1],
	    							(GeoDirectionND) arg[2])};
	    			return ret;
	    		}
	    	}
	    	
	    	
	    	break;
	    }
	    
	    return super.process(c);
	}
	
}
