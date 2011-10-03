package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.commands.CmdCurveCartesian;
import geogebra.kernel.commands.CmdLine;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Line[ <GeoPoint3D>, <GeoPoint3D> ] or CmdLine
 */
public class CmdSurfaceCartesian3D extends CmdCurveCartesian {
	
	public CmdSurfaceCartesian3D(Kernel kernel) {
		super(kernel);
		
	}
	

	public GeoElement[] process(Command c) throws MyError {	
		

	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];


		switch (n) {
		case 9 :
			GeoElement[] arg;
    		// create local variables and resolve arguments		
	    	arg = resArgsLocalNumVar(c, new int[] {3,6}, new int[] {4,7});
    	    if ((ok[0] = arg[0].isNumberValue())
            	 && (ok[1] = arg[1].isNumberValue())
            	 && (ok[2] = arg[2].isNumberValue())
               	 && (ok[3] = arg[3].isGeoNumeric())
               	 && (ok[4] = arg[4].isNumberValue())
               	 && (ok[5] = arg[5].isNumberValue())
              	 && (ok[6] = arg[6].isGeoNumeric())
               	 && (ok[7] = arg[7].isNumberValue())
               	 && (ok[8] = arg[8].isNumberValue())              	 
    	    )
               {
            	   GeoElement [] ret = new GeoElement[1];
                   ret[0] = kernel.getManager3D().SurfaceCartesian3D(
                		   c.getLabel(),
                		   (NumberValue) arg[0],
                		   (NumberValue) arg[1],
                		   (NumberValue) arg[2],
                		   (GeoNumeric) arg[3],
                		   (NumberValue) arg[4],
                		   (NumberValue) arg[5],
                		   (GeoNumeric) arg[6],
                		   (NumberValue) arg[7],
                		   (NumberValue) arg[8]                                             
                   );
                   return ret;
               } else {          
               	for (int i=0; i < n; i++) {
               		if (!ok[i]) throw argErr(app, "SurfaceCartesian", arg[i]);	
               	}            	
               }                   	  

        default :
            throw argNumErr(app, "SurfaceCartesian", n);
    }
	}

}
