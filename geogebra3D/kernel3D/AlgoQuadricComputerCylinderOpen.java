package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * for open cylinders
 * @author mathieu
 *
 */
public class AlgoQuadricComputerCylinderOpen extends AlgoQuadricComputerCylinder {
	
	
	public GeoQuadric3D newQuadric(Construction c){
		return new GeoQuadric3DPart(c);
	}
	

	public String getClassName() {
		return "AlgoCylinderOpen";
	}


	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, double number) {
		
		direction.calcNorm();
		double altitude = direction.getNorm();
		
		quadric.setCylinder(origin, direction.mul(1/altitude), number);
		
		((GeoQuadric3DPart) quadric).setLimits(0, altitude);
	}
	


}
