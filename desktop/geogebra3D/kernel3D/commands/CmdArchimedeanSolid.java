package geogebra3D.kernel3D.commands;

import com.quantimegroup.solutions.archimedean.common.SolidDefinition;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.commands.CommandProcessorDesktop;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoVector3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Cube[ <GeoPoint3D>, <GeoPoint3D>, <GeoDirectionND> ] 
 */
public class CmdArchimedeanSolid extends CommandProcessorDesktop {
	
	private String name;
	
	public CmdArchimedeanSolid(Kernel kernel, String name) {
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
	    		
	    			GeoElement[] ret = { kernel.getManager3D().ArchimedeanSolid(c.getLabels(), 
	    					(GeoPointND) arg[0], (GeoPointND) arg[1], (GeoDirectionND) arg[2],
	    					name) };
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
