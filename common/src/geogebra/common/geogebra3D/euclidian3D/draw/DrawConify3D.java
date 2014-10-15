package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoForExtrusion;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoPolyhedronPointsPyramidForExtrusion;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedConicHeightConeForExtrusion;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoConicND;

import java.util.ArrayList;

/**
 * Class for extrusions
 * 
 * @author matthieu
 *
 */
public class DrawConify3D extends DrawExtrusionOrConify3D {

	/**
	 * constructor
	 * @param a_view3D view
	 * @param selectedPolygons polygons
	 * @param selectedConics conics
	 */
	public DrawConify3D(EuclidianView3D a_view3D,
			ArrayList<GeoPolygon> selectedPolygons,
			ArrayList<GeoConicND> selectedConics) {
		super(a_view3D, selectedPolygons, selectedConics);
	}

	@Override
	protected AlgoForExtrusion getAlgo(GeoPolygon basis, GeoNumeric height) {
		return new AlgoPolyhedronPointsPyramidForExtrusion(
				getView3D().getKernel().getConstruction(),
				null, 
				basis, 
				height);
	}

	@Override
	protected AlgoForExtrusion getAlgo(GeoConicND basis, GeoNumeric height) {
		return new AlgoQuadricLimitedConicHeightConeForExtrusion(
				getView3D().getKernel().getConstruction(),
				null, 
				basis, 
				height);
	}
	
	public void extrude(NumberValue val){
		if (basis instanceof GeoConic){
										
			basis.getKernel().getManager3D().ConeLimited(null, (GeoConic)basis, (GeoNumeric)val);
			
		}else {
			basis.getKernel().getManager3D().Pyramid(null, (GeoPolygon)basis, (GeoNumeric)val);
		}
		

		//remove the algo
		extrusionComputer.getAlgo().remove();	
		extrusionComputer=null;
		
	}
	

}
