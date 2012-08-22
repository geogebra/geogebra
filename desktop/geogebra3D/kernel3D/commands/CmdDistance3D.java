package geogebra3D.kernel3D.commands;




import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdDistance;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.main.MyError;


/**
 * Distance[ <GeoLineND>, <GeoLineND> ] 
 */
public class CmdDistance3D extends CmdDistance {
	
	
	
	public CmdDistance3D(Kernel kernel) {
		super(kernel);
		
		
	}	
	
	
public  GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
        	arg = resArgs(c);

        	if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()){


        		if(arg[0].isGeoLine() && arg[1].isGeoLine()){

        			return new GeoElement[] {
        					((Kernel)kernelA).getManager3D().Distance(
        							c.getLabel(),
        							(GeoLineND)arg[0],
        							(GeoLineND)arg[1])        		
        			};
        		}
        	}
        	
        
        	return super.process(c);

        default :
            //return super.process(c);
        	throw argNumErr(app, c.getName(), n);
    }
}
}