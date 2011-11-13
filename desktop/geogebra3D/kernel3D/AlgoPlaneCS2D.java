package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

/**
 * Create a plane containing a 2D coord sys
 * @author ggb3D
 *
 */
public class AlgoPlaneCS2D extends AlgoElement3D {
	
	/** the 2D coord sys created */
	protected GeoCoordSys2D cs;
	
	
	/** polygon */
	private GeoCoordSys2D csInput;
	
	/**
	 * create a plane joining points, with label.
	 * @param c construction
	 * @param label label of the polygon
	 * @param csInput 
	 */
	public AlgoPlaneCS2D(Construction c, String label, GeoCoordSys2D csInput) {

		this(c, csInput);
		
		((GeoElement) cs).setLabel(label);
		
	}
	
	public AlgoPlaneCS2D(Construction c, GeoCoordSys2D csInput) {
		super(c);
		 
		this.csInput = csInput;

		cs = new GeoPlane3D(c);
		
		//set input and output		
		setInputOutput(new GeoElement[]{(GeoElement) csInput}, new GeoElement[]{(GeoElement) cs});
		
		
	}
	
	
	protected void compute() {
		
		CoordSys coordsys = cs.getCoordSys();
		

		if (!((GeoElement) csInput).isDefined()){
			coordsys.setUndefined();
			return;
		}
		
		
		//copy the coord sys
		coordsys.set(csInput.getCoordSys());
		
		//recalc equation vector (not existing for polygons, ...)
		if (coordsys.isDefined()) 
			coordsys.makeEquationVector();
		
		
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
    	return app.getPlain("PlaneContainingA",((GeoElement) csInput).getLabel());

    }

}
