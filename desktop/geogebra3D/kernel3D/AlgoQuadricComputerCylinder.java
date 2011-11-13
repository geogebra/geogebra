package geogebra3D.kernel3D;

import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * for cylinders
 * @author mathieu
 *
 */
public class AlgoQuadricComputerCylinder extends AlgoQuadricComputer {

	public String getClassName() {
		return "AlgoCylinderInfinite";
	}


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
