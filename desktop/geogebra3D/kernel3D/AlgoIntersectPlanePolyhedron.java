package geogebra3D.kernel3D;


import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.kernelND.HasSegments;

import java.util.TreeMap;




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

		/*
		Coords[] intersection = CoordMatrixUtil.intersectPlanes(
    			plane.getCoordSys().getMatrixOrthonormal(),
    			currentFace.getCoordSys().getMatrixOrthonormal());

		o1 = intersection[0];
		d1 = intersection[1];
		*/
		
	}
	
	//private GeoPolygon currentFace;

	
    @Override
	protected void intersectionsCoords(HasSegments p, TreeMap<Double, Coords> newCoords){
    	
    	/*
    	GeoPolyhedron polyh = (GeoPolyhedron) p;
    	TreeSet<GeoPolygon> polygons = new TreeSet<GeoPolygon>();
    	polygons.addAll(polyh.getPolygonsLinked());
    	polygons.addAll(polyh.getPolygons());
    	currentFace = polygons.first();
    	polygons.remove(currentFace);
    	App.debug(currentFace);
    	TreeMap<Double, Coords> currentFaceCoords = new TreeMap<Double, Coords>();
    	intersectionsCoordsContained(currentFace, currentFaceCoords);
    	if (currentFaceCoords.size()>0){
    		Object[] points = currentFaceCoords.values().toArray();
    		Coords b = (Coords) points[0];
    		for (int i=1; i<points.length; i++){
    			Coords a = b;
    			b = (Coords) points[i];
    			App.debug("\na=\n"+a);
    			App.debug("\nb=\n"+b);
    			Coords c2D = currentFace.getNormalProjection(a.add(b).mul(0.5))[1];
    			App.debug(currentFace.isInRegion(c2D.getX(), c2D.getY()));
    		}
    	}
    	*/

    	for(int i=0; i<p.getSegments().length; i++){
    		GeoSegmentND seg = p.getSegments()[i];

    		Coords coords = intersectionCoords(seg);
    		if (coords!=null)
    			newCoords.put((double) i, coords);


    	}
    }
	
    private Coords intersectionCoords(GeoSegmentND seg){
    	Coords o = seg.getPointInD(3, 0);
		Coords d = seg.getPointInD(3, 1).sub(o);

		Coords[] project = 
				o.projectPlaneThruV(plane.getCoordSys().getMatrixOrthonormal(), d);

		
		//check if projection is intersection point
		if (!Kernel.isZero(project[0].getW()) && seg.respectLimitedPath(-project[1].get(3)))
			return project[0];
		
		return null;
    }
  
	
    @Override
	protected boolean checkParameter(double t1){
    	return true;
    }

	@Override
	public Commands getClassName() {
		return Commands.Intersect;
	}
	

	
}

