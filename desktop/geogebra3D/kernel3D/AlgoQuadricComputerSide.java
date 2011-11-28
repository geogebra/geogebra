package geogebra3D.kernel3D;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;

/**
 * for open cylinders
 * @author mathieu
 *
 */
public class AlgoQuadricComputerSide extends AlgoQuadricComputer {
	
	
	public GeoQuadric3D newQuadric(Construction c){
		return new GeoQuadric3DPart(c);
	}
	

	public String getClassName() {
		return "AlgoQuadricSide";
	}


	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, double number) {
		
		((GeoQuadric3DPart) quadric).set(origin, direction, number);
		
	}


	public double getNumber(double v) {
		return 0;
	}
	


}
