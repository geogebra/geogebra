package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.algos.AlgoClosestPoint;
import geogebra.common.kernel.algos.AlgoDispatcher;
import geogebra.common.kernel.algos.AlgoDistanceLineLine;
import geogebra.common.kernel.algos.AlgoVectorPoint;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;


/**
 * extending 2D AlgoDispatcher
 * @author mathieu
 *
 */
public class AlgoDispatcher3D extends AlgoDispatcher {

	/**
	 * Constructor
	 * @param cons Construction
	 */
	public AlgoDispatcher3D(Construction cons) {
		super(cons);
		
	}
	
	
	@Override
	public AlgoClosestPoint getNewAlgoClosestPoint(Construction cons2, Path path,
			GeoPointND point) {
		
		if (((GeoElement) path).isGeoElement3D() || point.isGeoElement3D())
			return new AlgoClosestPoint3D(cons2, path, point);
		
		return super.getNewAlgoClosestPoint(cons2, path, point);
	}
	
	
	@Override
	public GeoNumeric Distance(String label, GeoLineND g, GeoLineND h) {
		
		if (g.isGeoElement3D() || h.isGeoElement3D()){
			AlgoDistanceLines3D algo = new AlgoDistanceLines3D(cons, label, g, h);
			return algo.getDistance();
		}
		
		return super.Distance(label, g, h);
	}

	@Override
	protected GeoVectorND createVector(String label, GeoPointND P){
		if (P.isGeoElement3D()){
			AlgoVectorPoint3D algo = new AlgoVectorPoint3D(cons, label, P);
			return algo.getVector();
		}
		
		return super.createVector(label, P);
		
	}
	


}
