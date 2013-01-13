package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;



/*
 * Cube[ <GeoPoint3D>, <GeoPoint3D>, <GeoDirectionND> ] 
 */
public class CmdArchimedeanSolid extends CommandProcessor {
	
	private Commands name;
	
	public CmdArchimedeanSolid(Kernel kernel, Commands name) {
		super(kernel);
		this.name = name;
		
	}
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
	    case 3 :
	    	arg = resArgs(c);
	    	if ((ok[0] = arg[0].isGeoPoint())
	    			&& (ok[1] = arg[1].isGeoPoint())
	    			&& (ok[2] = (arg[2] instanceof GeoDirectionND))){
	    		
	    			GeoElement[] ret = kernelA.getManager3D().ArchimedeanSolid(c.getLabels(), 
	    					(GeoPointND) arg[0], (GeoPointND) arg[1], (GeoDirectionND) arg[2],
	    					name) ;
					return ret;
	    		
	    	}else{
	    		for (int i=0;i<3;i++){
	    			if (!ok[i])
	    				throw argErr(app, c.getName(), arg[i]);
	    		}
	    	}
	    	break;	    
	    }
		
		throw argNumErr(app, c.getName(), n);

		

	}
	
	
	

}
