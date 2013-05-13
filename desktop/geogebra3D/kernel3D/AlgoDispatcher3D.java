package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Manager3DInterface;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.algos.AlgoClosestPoint;
import geogebra.common.kernel.algos.AlgoDispatcher;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.kernelND.GeoConicND;
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
	public GeoPointND IntersectLines(String label, GeoLineND g, GeoLineND h) {

		if (((GeoElement) g).isGeoElement3D()
				|| ((GeoElement) h).isGeoElement3D()){
			return (GeoPointND) getManager3D().Intersect(label, (GeoElement) g,
					(GeoElement) h);
		}
		
		return super.IntersectLines(label, g, h);
		

	}

	@Override
	protected GeoVectorND createVector(String label, GeoPointND P){
		if (P.isGeoElement3D()){
			AlgoVectorPoint3D algo = new AlgoVectorPoint3D(cons, label, P);
			return algo.getVector();
		}
		
		return super.createVector(label, P);
		
	}
	

	@Override
	public GeoPointND[] IntersectConics(String[] labels, GeoConicND a,
			GeoConicND b) {

		if (((GeoElement) a).isGeoElement3D()
				|| ((GeoElement) b).isGeoElement3D())
			return getManager3D().IntersectConics(labels, a, b);
		return super.IntersectConics(labels, a, b);
	}
	
	
	
	
	private Manager3DInterface getManager3D(){
		return cons.getKernel().getManager3D();
	}


}
