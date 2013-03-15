package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoPolygon;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * AlgoElement creating a GeoPolygon3D
 * 
 * @author ggb3D
 *
 */
public class AlgoPolygon3D extends AlgoPolygon {
	
	
   
	/** says if the polygon has to creates its edges */
	boolean createSegments = true;

	
	
	
	
	/**
	 * Constructor with points
	 * @param cons the construction
	 * @param label names of the polygon and segments
	 * @param points vertices of the polygon
	 * @param polyhedron polyhedron (when segment is part of)
	 */   
	public AlgoPolygon3D(Construction cons, String[] label, GeoPointND[] points, GeoElement polyhedron) {
		this(cons, label, points, true, polyhedron);

	}
	
	
    /**
     * @param cons the construction
     * @param labels names of the polygon and the segments
     * @param points vertices of the polygon
     * @param createSegments  says if the polygon has to creates its edges (3D only) 
     * @param polyhedron polyhedron (when segment is part of)
     */
	public AlgoPolygon3D(Construction cons, String[] labels, 
			GeoPointND[] points, boolean createSegments, GeoElement polyhedron) {
		super(cons, labels, points, null,null,createSegments, polyhedron, null);
		
	}
	
	
    /**
     * create the polygon
     * @param createSegments says if the polygon has to creates its edges (3D only)
     */
    @Override
	protected void createPolygon(boolean createSegments){
    	poly = new GeoPolygon3D(cons, points, cs2D, createSegments);
    	if (polyhedron!=null){
    		((GeoPolygon3D) poly).setIsPartOfClosedSurface(true);
    		((GeoPolygon3D) poly).setFromMeta(polyhedron);
    	}
    }
	

    @Override
	public void compute() { 
    	
    	//check if a coord sys is possible
    	if (((GeoPolygon3D) poly).updateCoordSys())
    		super.compute();
    	else
    		poly.setUndefined();
    	
    }

    
    
    

}
