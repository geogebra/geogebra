package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

/**
 * @author ggb3D
 *
 */
public class AlgoPlaneThreePoints extends AlgoElement3D {
	
	/** the 2D coord sys created */
	protected GeoCoordSys2D cs;
	
	
	/** 3D points */
	private GeoPointND A,B,C;
	
	/**
	 * create a plane joining points, with label.
	 * @param c construction
	 * @param label label of the polygon
	 * @param A first point
	 * @param B second point
	 * @param C third point
	 */
	public AlgoPlaneThreePoints(Construction c, String label, GeoPointND A, GeoPointND B, GeoPointND C) {
		super(c);
		 
		this.A = A;
		this.B = B;
		this.C = C;

		cs = new GeoPlane3D(c);
		
		//set input and output		
		setInputOutput(new GeoElement[]{(GeoElement) A, (GeoElement) B, (GeoElement) C}, new GeoElement[]{(GeoElement) cs});
		
		((GeoElement) cs).setLabel(label);
		
	}
	
	
	protected void compute() {
		
		CoordSys coordsys = cs.getCoordSys();
		

		if ((!A.isDefined()) || (!B.isDefined()) || (!C.isDefined())){
			coordsys.setUndefined();
			return;
		}
		
		
		//recompute the coord sys
		coordsys.resetCoordSys();
		
		coordsys.addPoint(A.getInhomCoordsInD(3));
		coordsys.addPoint(B.getInhomCoordsInD(3));
		coordsys.addPoint(C.getInhomCoordsInD(3));
		
		if (coordsys.makeOrthoMatrix(false,false)){
			if (coordsys.isDefined())
				coordsys.makeEquationVector();
		}
		
		//Application.debug(cs.getCoordSys().getMatrixOrthonormal().toString());
		
	}

	
	/**
	 * return the cs
	 * @return the cs
	 */
	public GeoCoordSys2D getCoordSys() {		
		return cs;
	}
	
	
	
	public String getClassName() {
		return "AlgoPlane";
	}
	
    final public String toString() {
    	return app.getPlain("PlaneThroughABC",A.getLabel(),B.getLabel(),C.getLabel());

    }

}
