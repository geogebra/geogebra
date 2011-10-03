package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoRayND;

public class GeoRay3D extends GeoLine3D implements GeoRayND{

	public GeoRay3D(Construction c, GeoPointND O, GeoPointND Q) {
		super(c, O, Q);
		setStartPoint(O);
        
		// TODO Auto-generated constructor stub
	}
	
	public GeoRay3D(Construction construction) {
		super(construction);
	}

	public int getGeoClassType(){
		return GEO_CLASS_RAY3D;
		
	}
	
	protected String getTypeString(){
		return "Ray3D";
	}
	
	
	

	protected GeoCoordSys1D create(Construction cons){
		return new GeoRay3D(cons);
	}
	
	//Path3D interface
	public double getMinParameter() {
		return 0;
	}
	
	
	public boolean isValidCoord(double x){
		return (x>=0);
	}

	public boolean isOnPath(Coords p, double eps){
		//first check global line
		if (!super.isOnPath(p, eps))
			return false;

		//then check position on segment
		return respectLimitedPath(p, eps);


	}

	public boolean respectLimitedPath(Coords p, double eps) {    	
		if (Kernel.isEqual(p.getW(),0,eps))//infinite point
			return false;
		double d = p.sub(getStartInhomCoords()).dotproduct(getDirectionInD3());
		if (d<-eps)
			return false;


		return true;	
	} 
}
