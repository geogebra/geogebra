package geogebra3D.kernel3D;

import java.util.TreeMap;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.HasSegments;
import geogebra.common.main.App;




public class AlgoIntersectPlanePolygon extends AlgoIntersectLinePolygon3D {
	
	private GeoPlane3D plane;


	
	public AlgoIntersectPlanePolygon(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolygon p) {		
		super(c, labels, plane, p);
		
        
	}

	@Override
	protected void setFirstInput(GeoElement geo){
		this.plane = (GeoPlane3D) geo;
		
	}
	
    @Override
	protected GeoElement getFirstInput(){
    	return (GeoElement) plane;
    }


	

	@Override
	protected void setIntersectionLine(){
	
    	Coords[] intersection = CoordMatrixUtil.intersectPlanes(
    			plane.getCoordSys().getMatrixOrthonormal(),
    			((GeoPolygon) p).getCoordSys().getMatrixOrthonormal());

		o1 = intersection[0];
		d1 = intersection[1];
		
	}

	
    @Override
	protected void intersectionsCoords(HasSegments p, TreeMap<Double, Coords> newCoords){

    	//intersection line is contained in polygon plane by definition
    	intersectionsCoordsContained(p, newCoords);
    }
	
  
	
    @Override
	protected boolean checkParameter(double t1){
    	return true;
    }

	@Override
	public Algos getClassName() {
		return Algos.AlgoIntersectPlanePolygon;
	}
	

	
}

