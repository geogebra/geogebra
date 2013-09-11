package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdPolygon;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;



/**
 * Polygon[ <GeoPoint3D>, <GeoPoint3D>, ... ] or CmdPolygon
 */
public class CmdPolygon3D extends CmdPolygon {
	

	/**
	 * constructor
	 * @param kernel kernel
	 */
	public CmdPolygon3D(Kernel kernel) {
		super(kernel);
				
	}
	
	
	
	@Override
	protected GeoElement[] polygon(String[] labels, GeoPointND[] points){
		
		// if one point is 3D, use 3D algo
		for (int i = 0 ; i < points.length ; i++){
			if (points[i].isGeoElement3D()){
				return kernelA.getManager3D().Polygon3D(labels, points);
			}
		}
		
		// else use 2D algo
		return super.polygon(labels, points);
	}
	
	/*
	@Override
	protected GeoElement[] regularPolygon(String[] labels, GeoPointND A, GeoPointND B, GeoNumberValue n){
		
		if (A.isGeoElement3D() || B.isGeoElement3D()){
			return kernelA.getManager3D().RegularPolygon(labels, A, B, n, kernelA.getXOYPlane());
		}
		
		return super.regularPolygon(labels, A, B, n);
	}
	*/

}
