package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.EuclidianView3D;

import java.awt.Color;

public class GeoPlane3DConstant extends GeoPlane3D {
	
	public static final int XOY_PLANE = 1;

	/** construct the plane xOy, ...
	 * @param c
	 * @param type
	 */
	public GeoPlane3DConstant(Construction c, int type) {
		
		super(c);
		
		
		switch (type) {
		case XOY_PLANE:
			coordsys.addPoint(EuclidianView3D.o);
			coordsys.addVector(EuclidianView3D.vx);
			coordsys.addVector(EuclidianView3D.vy);
			coordsys.makeOrthoMatrix(false,false);
			coordsys.makeEquationVector();
			//setCoord(EuclidianView3D.o,EuclidianView3D.vx,EuclidianView3D.vy);
			label = "xOyPlane";
			setObjColor(new Color(0.5f,0.5f,0.5f));
			setLabelVisible(false);
			break;

		}
		
		setFixed(true);
	}
	
	
	/*
	public GgbVector getPoint(double x2d, double y2d){
		
		if (x2d>getXmax())
			x2d=getXmax();
		else if (x2d<getXmin())
			x2d=getXmin();
		
		if (y2d>getYmax())
			y2d=getYmax();
		else if (y2d<getYmin())
			y2d=getYmin();		
		
		return super.getPoint(x2d,y2d);
	}

*/
	
	protected boolean isAvailableAtConstructionStep(int step) {
		// this method is overwritten
		// in order to make the axes available
		// in empty constructions too (for step == -1)
		return true;
	}
	
	public String toValueString() {
		return label;
	}
	
}
