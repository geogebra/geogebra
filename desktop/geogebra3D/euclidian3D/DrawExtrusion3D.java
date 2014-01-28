package geogebra3D.euclidian3D;

import geogebra.common.geogebra3D.kernel3D.algos.AlgoForExtrusion;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoPolyhedronPointsPrismForExtrusion;
import geogebra.common.geogebra3D.kernel3D.algos.AlgoQuadricLimitedConicHeightCylinderForExtrusion;
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
public class DrawExtrusion3D extends DrawExtrusionOrConify3D {

	/**
	 * constructor
	 * @param a_view3D view
	 * @param selectedPolygons polygons
	 * @param selectedConics conics
	 */
	public DrawExtrusion3D(EuclidianView3D a_view3D,
			ArrayList<GeoPolygon> selectedPolygons,
			ArrayList<GeoConicND> selectedConics) {
		super(a_view3D, selectedPolygons, selectedConics);
	}

	@Override
	protected AlgoForExtrusion getAlgo(GeoPolygon basis, GeoNumeric height) {
		return new AlgoPolyhedronPointsPrismForExtrusion(
				getView3D().getKernel().getConstruction(),
				null, 
				basis, 
				height);
	}

	@Override
	protected AlgoForExtrusion getAlgo(GeoConicND basis, GeoNumeric height) {
		return new AlgoQuadricLimitedConicHeightCylinderForExtrusion(
				getView3D().getKernel().getConstruction(),
				null, 
				basis, 
				height);
	}
	
	

	
	

}
