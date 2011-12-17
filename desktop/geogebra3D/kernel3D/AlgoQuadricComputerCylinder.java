package geogebra3D.kernel3D;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.kernel.Kernel;

/**
 * for cylinders
 * @author mathieu
 *
 */
public class AlgoQuadricComputerCylinder extends AlgoQuadricComputer {



	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, double number) {
		quadric.setCylinder(origin, direction.normalize(), number);
		
	}
	

	public double getNumber(double v){

		if (Kernel.isZero(v)) {
			return 0;
		}else if (v < 0) {
			return Double.NaN;
		}	
		
		return v;
	}

}
