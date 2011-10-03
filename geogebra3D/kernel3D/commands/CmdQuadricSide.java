package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoQuadric3DLimited;
import geogebra3D.kernel3D.GeoVector3D;
import geogebra3D.kernel3D.Kernel3D;

public class CmdQuadricSide extends CommandProcessor {
	
	
	
	public CmdQuadricSide(Kernel kernel) {
		super(kernel);
	}

	

	public GeoElement[] process(Command c) throws MyError {
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {	
	    
	    
	    case 1 :
	    	arg = resArgs(c);
	    	if (
	    			(ok[0] = (arg[0] instanceof GeoQuadric3DLimited ) )
	    			 
	    	) {
	    		return new GeoElement[] { 
	    				kernel.getManager3D().QuadricSide(c.getLabel(),
    					(GeoQuadric3DLimited) arg[0])};
	    	}else{
	    			throw argErr(arg[0]);
	    	}

	    default :
	    	throw argNumErr(n);
	    }
	    

	}
	
	
	
	protected MyError argErr(GeoElement geo){
		return argErr(app,"QuadricSide",geo);
	}
	
	protected MyError argNumErr(int n){
		return argNumErr(app,"QuadricSide",n);
	}
	
}
