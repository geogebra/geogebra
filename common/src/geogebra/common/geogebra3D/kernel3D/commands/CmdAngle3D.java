package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.geogebra3D.kernel3D.algos.AlgoAngleElement3D;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoAnglePoint3D;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoAngleVector3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;

public class CmdAngle3D extends CmdAngle {
	
	
	
	
	public CmdAngle3D(Kernel kernel) {
		super(kernel);
	}

	
	


	@Override
	protected GeoElement[] angle(String label, GeoPointND p1, GeoPointND p2, GeoPointND p3){
		if (p1.isGeoElement3D() || p2.isGeoElement3D() || p3.isGeoElement3D()){
			GeoElement[] ret = { kernelA.getManager3D().Angle3D(label, p1, p2, p3) };
			return ret;
		}

		return super.angle(label, p1, p2, p3);
	}
	
	@Override
	protected GeoElement[] angle(String label, GeoLineND g, GeoLineND h){
		
		if (g.isGeoElement3D() || h.isGeoElement3D()){
			GeoElement[] ret = { kernelA.getManager3D().Angle3D(label, g, h) };
			return ret;
		}

		return super.angle(label, g, h);
	}
	
	
	@Override
	protected GeoElement[] angle(String label, GeoVectorND v, GeoVectorND w){
		
		if (v.isGeoElement3D() || w.isGeoElement3D()){
			GeoElement[] ret = { kernelA.getManager3D().Angle3D(label, v, w) };
			return ret;
		}
		
		return super.angle(label, v, w);
		
	}
	
	
	@Override
	protected GeoElement[] anglePointOrVector(String label, GeoElement v){
		
		if (v.isGeoElement3D()){
			AlgoAngleElement3D algo;
			if (v.isGeoVector()){
				algo = new AlgoAngleVector3D(cons, label, (GeoVector3D) v);
			}else{
				algo = new AlgoAnglePoint3D(cons, label, (GeoPoint3D) v);
			}
			
			GeoElement[] ret = { algo.getAngle() };
			return ret;
		}

		return super.anglePointOrVector(label, v);
	}
}
