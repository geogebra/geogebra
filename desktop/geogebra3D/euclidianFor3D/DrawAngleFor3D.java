package geogebra3D.euclidianFor3D;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.euclidian.DrawAngle;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Kernel;
import geogebra.kernel.algos.AlgoAnglePoints;
import geogebra.kernel.geos.GeoAngle;

public class DrawAngleFor3D extends DrawAngle {

	public DrawAngleFor3D(EuclidianView view, GeoAngle angle) {
		super(view, angle);
	}
	
	
	@Override
	protected boolean inView(Coords point){
		return Kernel.isZero(point.getZ());
	}
	
	@Override
	protected double getRawAngle(){
		if (((AlgoAnglePoints) getGeoElement().getDrawAlgorithm()).getVn().getZ()<0) {
			return 2*Math.PI-super.getRawAngle();
		} else {
			return super.getRawAngle();
		}
	}

}
