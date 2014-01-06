package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoUnitVector;
import geogebra.common.kernel.algos.CmdUnitVector;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.main.MyError;
import geogebra3D.kernel3D.AlgoUnitVector3D;

/**
 * UnitOrthogonalVector[ <GeoPlane3D> ] 
 */
public class CmdUnitVector3D extends CmdUnitVector {
	
	
	
	public CmdUnitVector3D(Kernel kernel) {
		super(kernel);
	}
	
	
	
	@Override
	protected GeoElement[] processNotLineNotVector(Command c, GeoElement arg) throws MyError {
		
		if (arg instanceof GeoDirectionND) {
			AlgoUnitVector3D algo = new AlgoUnitVector3D(cons, c.getLabel(), (GeoDirectionND) arg);
			GeoElement[] ret = { (GeoElement) algo.getVector()};
			return ret;
		}
		
		return super.processNotLineNotVector(c, arg);
	}
	

	@Override
	protected AlgoUnitVector algo(String label, GeoLineND line){
		
		if (line.isGeoElement3D()){
			return new AlgoUnitVector3D(cons, label, line);
		}
		
		return super.algo(label, line);
	}
	
	@Override
	protected AlgoUnitVector algo(String label, GeoVectorND v){
		
		if (v.isGeoElement3D()){
			return new AlgoUnitVector3D(cons, label, v);
		}
		
		return super.algo(label, v);
	}

	
}
