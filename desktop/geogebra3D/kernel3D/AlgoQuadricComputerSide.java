package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;

/**
 * for open cylinders
 * @author mathieu
 *
 */
public class AlgoQuadricComputerSide extends AlgoQuadricComputer {
	
	
	public GeoQuadric3D newQuadric(Construction c){
		return new GeoQuadric3DPart(c);
	}
	



	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, double number) {
		
		((GeoQuadric3DPart) quadric).set(origin, direction, number);
		
	}


	public double getNumber(double v) {
		return 0;
	}
	


}
