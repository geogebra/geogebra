package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.algos.AlgoClosestPoint;
import geogebra.common.kernel.algos.AlgoDispatcher;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;


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

}
