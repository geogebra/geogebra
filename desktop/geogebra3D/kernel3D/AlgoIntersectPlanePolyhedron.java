package geogebra3D.kernel3D;

import java.util.TreeMap;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.HasSegments;
import geogebra.common.main.App;




public class AlgoIntersectPlanePolyhedron extends AlgoIntersectLinePolygon3D {
	
	private GeoPlane3D plane;


	
	public AlgoIntersectPlanePolyhedron(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolyhedron p) {		
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
	
    	//no intersection line
		
	}

	
    @Override
	protected void intersectionsCoords(HasSegments p, TreeMap<Double, Coords> newCoords){

    	for(int i=0; i<p.getSegments().length; i++){
    		GeoSegmentND seg = p.getSegments()[i];

    		Coords o = seg.getPointInD(3, 0);
    		Coords d = seg.getPointInD(3, 1).sub(o);

    		Coords[] project = 
    				o.projectPlaneThruV(plane.getCoordSys().getMatrixOrthonormal(), d);

    		
    		//check if projection is intersection point
    		if (!Kernel.isZero(project[0].getW()) && seg.respectLimitedPath(-project[1].get(3)))
    			newCoords.put((double) i, project[0]);


    	}
    }
	
  
	
    @Override
	protected boolean checkParameter(double t1){
    	return true;
    }

	@Override
	public Algos getClassName() {
		return Algos.AlgoIntersectPlanePolyhedron;
	}
	

	
}

