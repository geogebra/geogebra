package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.PathMover;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;
import geogebra.common.kernel.kernelND.RotateableND;
import geogebra.common.main.App;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.Unicode;

public class GeoLine3D extends GeoCoordSys1D implements RotateableND {

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
	public boolean isEqual(GeoElement Geo) {
		App.debug("unimplemented");
		return false;
	}

	@Override
	public boolean showInAlgebraView() {
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		return coordsys.isDefined();
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
		
		StringBuilder sbToString = getSbBuildValueString();
		sbToString.setLength(0);
		String parameter = Unicode.lambda+"";
		AlgoElement algo = getParentAlgorithm();
		
		if (algo instanceof AlgoLinePoint) {
			AlgoLinePoint algoLP = (AlgoLinePoint) algo;
			
			GeoElement[] geos = algoLP.getInput();
			
			if (geos[0].isGeoPoint() && geos[1].isGeoVector()) {
				
				// use original coordinates for displaying, not normalized form for Line[ A, u ]
				
				GeoPointND pt = (GeoPointND) geos[0];
				Coords coords1 = pt.getInhomCoords();
				GeoVectorND vec = (GeoVectorND) geos[1];
				
				double[] coords2 = vec.getInhomCoords();
				
				sbToString.append("X = (");
				sbToString.append(kernel.format(coords1.get(1), tpl));
				sbToString.append(", ");
				sbToString.append(kernel.format(coords1.get(2), tpl));
				sbToString.append(", ");
				sbToString.append(kernel.format(coords1.getLength() == 3 ? coords1.get(3) : 0, tpl));
				sbToString.append(") + ");
				sbToString.append(parameter);
				sbToString.append(" (");
				sbToString.append(kernel.format(coords2[0], tpl));
				sbToString.append(", ");
				sbToString.append(kernel.format(coords2[1], tpl));
				sbToString.append(", ");
				sbToString.append(kernel.format(coords2.length == 3 ? coords2[2] : 0, tpl));
				sbToString.append(")");
				
				return sbToString;  
			}
		}
		
		
		Coords O = coordsys.getOrigin();//TODO inhom coords
		Coords V = coordsys.getVx();

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
	final public boolean isGeoLine() {
		return true;
	}	
	
	//Path3D interface
	
	public PathMover createPathMover() {
		App.debug("unimplemented");
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
	
	public boolean respectLimitedPath(double parameter){
		return true;
	}
	
	////////////////////
	// ROTATE
	////////////////////
	



	public void rotate(NumberValue phiValue) {
		
		Coords o = getCoordSys().getOrigin();
		
		double z = o.getZ();
		if (!Kernel.isZero(z)){
			setUndefined();
			return;
		}
	
		Coords v = getCoordSys().getVx();

		double vz = v.getZ();
		if (!Kernel.isZero(vz)){
			setUndefined();
			return;
		}

		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		
		double x = o.getX();
		double y = o.getY();
		double w = o.getW();


		Coords oRot = new Coords(x * cos - y * sin, x * sin + y * cos, 
				z, w);
		
		double vx = v.getX();
		double vy = v.getY();
		double vw = v.getW();

		
		Coords vRot = new Coords(vx * cos - vy * sin, vx * sin + vy * cos,
				vz, vw);
		
		setCoord(oRot, vRot);
				
	}
	
	final public void rotate(NumberValue phiValue, GeoPoint Q) {

		Coords o = getCoordSys().getOrigin();

		double z = o.getZ();
		if (!Kernel.isZero(z)){
			setUndefined();
			return;
		}
	
		Coords v = getCoordSys().getVx();

		double vz = v.getZ();
		if (!Kernel.isZero(vz)){
			setUndefined();
			return;
		}

		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		
		double x = o.getX();
		double y = o.getY();
		double w = o.getW();

		double qx = w * Q.getInhomX();
		double qy = w * Q.getInhomY();

		Coords oRot = new Coords((x - qx) * cos + (qy - y) * sin + qx, (x - qx) * sin
				+ (y - qy) * cos + qy, z, w);
		
		double vx = v.getX();
		double vy = v.getY();
		double vw = v.getW();

		
		Coords vRot = new Coords(vx * cos - vy * sin, vx * sin + vy * cos,
				vz, vw);
		
		setCoord(oRot, vRot);
		
		
	}
	
	final private void rotate(NumberValue phiValue, Coords o1, Coords vn) {

		if (vn.isZero()){
			setUndefined();
			return;
		}
		Coords vn2 = vn.normalized();
		
		
		Coords point = getCoordSys().getOrigin();
		Coords s = point.projectLine(o1, vn)[0]; //point projected on the axis
		
		Coords v1 = point.sub(s); //axis->point of the line

		
		Coords v = getCoordSys().getVx(); //direction of the line
		

		double phi = phiValue.getDouble();
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		
		// new line origin
		Coords v2 = vn2.crossProduct4(v1);
		Coords oRot = s.add(v1.mul(cos)).add(v2.mul(sin));
		
		// new line direction
		v2 = vn2.crossProduct4(v);
		v1 = v2.crossProduct4(vn2);
		Coords vRot = v1.mul(cos).add(v2.mul(sin)).add(vn2.mul(v.dotproduct(vn2)));


		setCoord(oRot, vRot);
	}
	
	

	public void rotate(NumberValue phiValue, GeoPointND S, GeoDirectionND orientation) {
		
		Coords o1 = S.getInhomCoordsInD(3);
		
		Coords vn = orientation.getDirectionInD3();

		rotate(phiValue, o1, vn);
	}

	public void rotate(NumberValue phiValue, GeoLineND line) {
		
		Coords o1 = line.getStartInhomCoords();
		Coords vn = line.getDirectionInD3();
		

		rotate(phiValue, o1, vn);
		
	}

}
