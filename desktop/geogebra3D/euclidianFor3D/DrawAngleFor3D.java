package geogebra3D.euclidianFor3D;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.draw.DrawAngle;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoAnglePoints;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * @author mathieu
 *
 */
public class DrawAngleFor3D extends DrawAngle {

	/**
	 * @param view view where the drawable is created
	 * @param angle angle
	 */
	public DrawAngleFor3D(EuclidianView view, GeoAngle angle) {
		super(view, angle);
	}
	
	
	@Override
	public boolean inView(Coords point){
		//Coords p = view.getCoordsForView(point);
		return Kernel.isZero(point.getZ());
	}
	
	@Override
	public Coords getCoordsInView(GeoPointND point){
		return view.getCoordsForView(point.getInhomCoordsInD(3));
	}
	
	
	
	@Override
	protected double getAngleStart(double start, double extent) {
		
		if (view.getCoordsForView(((AlgoAnglePoints) getGeoElement().getDrawAlgorithm()).getVn()).getZ()>0) {
			return super.getAngleStart(start, extent);
		}
		
		// reverse orientation
		return start - extent;
		
	}

}
