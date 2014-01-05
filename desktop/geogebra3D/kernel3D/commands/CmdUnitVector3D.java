package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoUnitVector;
import geogebra.common.kernel.algos.CmdUnitVector;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra3D.kernel3D.AlgoUnitVectorLine3D;
import geogebra3D.kernel3D.AlgoUnitVectorVector3D;

/**
 * UnitOrthogonalVector[ <GeoPlane3D> ] 
 */
public class CmdUnitVector3D extends CmdUnitVector {
	
	
	
	public CmdUnitVector3D(Kernel kernel) {
		super(kernel);
	}
	

	@Override
	protected AlgoUnitVector algo(String label, GeoLineND line){
		
		if (line.isGeoElement3D()){
			return new AlgoUnitVectorLine3D(cons, label, line);
		}
		
		return super.algo(label, line);
	}
	
	@Override
	protected AlgoUnitVector algo(String label, GeoVectorND v){
		
		if (v.isGeoElement3D()){
			return new AlgoUnitVectorVector3D(cons, label, v);
		}
		
		return super.algo(label, v);
	}

	
}
