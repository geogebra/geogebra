package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.PathMover;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;

public class GeoLine3D extends GeoCoordSys1D {

	GeoPointND startPoint;
	
	/** creates a line joining O and I */
	public GeoLine3D(Construction c, GeoPointND O, GeoPointND I) {
		super(c, O, I);
	}

    public GeoLine3D(Construction c) {
		super(c);
	}

	protected GeoLine3D(Construction c, Coords o, Coords v) {
		super(c,o,v);
	}

	final void setStartPoint(GeoPointND P) {        	
    	startPoint = P;	    	
    }
    
	
	protected GeoCoordSys1D create(Construction cons){
		return new GeoLine3D(cons);
	}
	

	public int getGeoClassType() {
		
		return GEO_CLASS_LINE3D;
	}

	protected String getTypeString() {
		// TODO Raccord de méthode auto-généré
		return "Line3D";
	}



	public boolean isEqual(GeoElement Geo) {
		// TODO Raccord de méthode auto-généré
		return false;
	}




	public boolean showInAlgebraView() {
		// TODO Raccord de méthode auto-généré
		return true;
	}

	protected boolean showInEuclidianView() {
		return true;
	}


	public String toValueString() {
		return buildValueString().toString();
	}
	

	
	final public String toString() {

    	StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(": ");  //TODO use kernel property
		sbToString.append(buildValueString());
		return sbToString.toString();   
	}


	private StringBuilder buildValueString() {	
		String parameter = "\u03bb";
		Coords O = coordsys.getOrigin();//TODO inhom coords
		Coords V = coordsys.getVx();

		StringBuilder sbToString = getSbBuildValueString();
		sbToString.setLength(0);
		sbToString.append("X = (");
		sbToString.append(kernel.format(O.get(1)));
		sbToString.append(", ");
		sbToString.append(kernel.format(O.get(2)));
		sbToString.append(", ");
		sbToString.append(kernel.format(O.get(3)));
		sbToString.append(") + ");
		sbToString.append(parameter);
		sbToString.append(" (");
		sbToString.append(kernel.format(V.get(1)));
		sbToString.append(", ");
		sbToString.append(kernel.format(V.get(2)));
		sbToString.append(", ");
		sbToString.append(kernel.format(V.get(3)));
		sbToString.append(")");
		
		return sbToString;  
	}
	
	
	

	public String getClassName() {
		// TODO Raccord de méthode auto-généré
		return "GeoLine3D";
	}
	
	final public boolean isGeoLine() {
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	//Path3D interface
	
	



	public PathMover createPathMover() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getMaxParameter() {
		return Double.POSITIVE_INFINITY;
	}

	public double getMinParameter() {
		return Double.NEGATIVE_INFINITY;
	}

	public boolean isClosedPath() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
	
	public boolean isValidCoord(double x){
		return true;
	}

	
	
	
	
	
	
	
	
}
