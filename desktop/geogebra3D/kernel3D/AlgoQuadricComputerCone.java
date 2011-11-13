package geogebra3D.kernel3D;

import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * for cones
 * @author mathieu
 *
 */
public class AlgoQuadricComputerCone extends AlgoQuadricComputer {

	public String getClassName() {
		return "AlgoConeInfinite";
	}


	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, double number) {
		quadric.setCone(origin, direction.normalize(), number);
		
	}

	public double getNumber(double v){
		double c = Math.cos(v);
		double s = Math.sin(v);

		if (c<0 || s<0) {
			return Double.NaN;
		}else if (Kernel.isZero(c)){//TODO if c=0 then draws a plane
			return Double.NaN;
		}	
		else if (Kernel.isZero(s)){//TODO if s=0 then draws a line
			return Double.NaN;
		}

		return s/c;
	}
}
