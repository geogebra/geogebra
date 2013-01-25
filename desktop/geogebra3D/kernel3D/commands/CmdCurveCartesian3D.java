package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CmdCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;



/**
 * Curve[ x(t),y(t),z(t),t,from,to]
 */
public class CmdCurveCartesian3D extends CmdCurveCartesian {
	
	/**
	 * @param kernel kernel
	 */
	public CmdCurveCartesian3D(Kernel kernel) {
		super(kernel);
		
	}
	

	@Override
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	 
	    if (n==6){
	    	// Curve[ <x-coord expression>,  <y-coord expression>,  <z-coord expression>, <number-var>, <from>, <to> ] 
	    	// Note: x and y and z coords are numbers dependent on number-var

	    	// create local variable at position 3 and resolve arguments
	    	GeoElement[] arg = resArgsLocalNumVar(c, 3, 4);      

	    	if ((ok[0] = arg[0].isNumberValue())
	    			&& (ok[1] = arg[1].isNumberValue())
	    			&& (ok[2] = arg[2].isNumberValue())
	    			&& (ok[3] = arg[3].isGeoNumeric())
	    			&& (ok[4] = arg[4].isNumberValue())
	    			&& (ok[5] = arg[5].isNumberValue()))
	    	{
	    		GeoElement [] ret = new GeoElement[1];
	    		ret[0] = kernelA.getManager3D().CurveCartesian3D(
	    				c.getLabel(),
	    				(NumberValue) arg[0],
	    				(NumberValue) arg[1],
	    				(NumberValue) arg[2],
	    				(GeoNumeric) arg[3],
	    				(NumberValue) arg[4],
	    				(NumberValue) arg[5]);
	    		return ret;
	    	}
			for (int i=0; i < n; i++) {
				if (!ok[i]) throw argErr(app, c.getName(), arg[i]);	
			}                   	  

	    }

		return super.process(c);
	}

}
