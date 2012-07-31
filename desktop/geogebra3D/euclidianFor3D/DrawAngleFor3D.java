package geogebra3D.euclidianFor3D;

import geogebra.common.euclidian.DrawAngle;
import geogebra.common.euclidian.EuclidianView;
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
	protected boolean inView(Coords point){
		//Coords p = view.getCoordsForView(point);
		return Kernel.isZero(point.getZ());
	}
	
	@Override
	protected Coords getCoordsInView(GeoPointND point){
		return view.getCoordsForView(point.getInhomCoordsInD(3));
	}
	
	
	@Override
	protected double getRawAngle(){
		if (view.getCoordsForView(((AlgoAnglePoints) getGeoElement().getDrawAlgorithm()).getVn()).getZ()<0) {
			if (getGeoElement().isGeoElement3D())
				//3D angle: raw angle > PI
				return 2*Math.PI-super.getRawAngle();
			//2D angle: reverse angle
			return -super.getRawAngle();
		}
		return super.getRawAngle();
	}

}
