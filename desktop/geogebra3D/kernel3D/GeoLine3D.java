package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.GeoClass;

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
    	
	@Override
	protected GeoCoordSys1D create(Construction cons){
		return new GeoLine3D(cons);
	}	

	@Override
	public GeoClass getGeoClassType() {		
		return GeoClass.LINE3D;
	}

	@Override
	public String getTypeString() {
		// TODO Raccord de méthode auto-généré
		return "Line3D";
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Raccord de méthode auto-généré
		return false;
	}

	@Override
	public boolean showInAlgebraView() {
		// TODO Raccord de méthode auto-généré
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return true;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return buildValueString(tpl).toString();
	}
	
	@Override
	final public String toString(StringTemplate tpl) {

    	StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(": ");  //TODO use kernel property
		sbToString.append(buildValueString(tpl));
		return sbToString.toString();   
	}

	private StringBuilder buildValueString(StringTemplate tpl) {	
		String parameter = "\u03bb";
		Coords O = coordsys.getOrigin();//TODO inhom coords
		Coords V = coordsys.getVx();

		StringBuilder sbToString = getSbBuildValueString();
		sbToString.setLength(0);
		sbToString.append("X = (");
		sbToString.append(kernel.format(O.get(1),tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(O.get(2),tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(O.get(3),tpl));
		sbToString.append(") + ");
		sbToString.append(parameter);
		sbToString.append(" (");
		sbToString.append(kernel.format(V.get(1),tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(V.get(2),tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(V.get(3),tpl));
		sbToString.append(")");
		
		return sbToString;  
	}

	@Override
	public String getClassName() {
		// TODO Raccord de méthode auto-généré
		return "GeoLine3D";
	}
	
	@Override
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
		
	@Override
	public boolean isValidCoord(double x){
		return true;
	}

	
	public final void removePointOnLine(GeoPointND p) {
		//TODO
	}
}
