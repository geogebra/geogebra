package geogebra.euclidian.DrawablesFor3D;

import geogebra.euclidian.DrawAngle;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.AlgoAnglePoints;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;


public class DrawAngleFor3D extends DrawAngle {

	public DrawAngleFor3D(EuclidianView view, GeoAngle angle) {
		super(view, angle);
	}
	
	
	protected boolean inView(Coords point){
		return Kernel.isZero(point.getZ());
	}
	
	protected double getRawAngle(){
		if (((AlgoAnglePoints) getGeoElement().getDrawAlgorithm()).getVn().getZ()<0)
			return 2*Math.PI-super.getRawAngle();
		else
			return super.getRawAngle();

	}

}
