package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.CoordSys;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoPolygon;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * AlgoElement creating a GeoPolygon3D
 * 
 * @author ggb3D
 *
 */
public class AlgoPolygon3DDirection extends AlgoPolygon {
	
	
	
	/**
	 * Constructor with an 2D coord sys and points
	 * @param cons the construction
	 * @param labels names of the polygon and segments
	 * @param points vertices of the polygon
	 * @param direction normal direction
	 */    
	public AlgoPolygon3DDirection(Construction cons, String[] labels, 
			GeoPointND[] points, GeoDirectionND direction) {
		super(cons,labels,points,null,null,true,null,direction);

	}
	

    @Override
	protected GeoElement [] createEfficientInput(){

    	GeoElement [] efficientInput;

    	if (geoList != null) {
    		// list as input
    		efficientInput = new GeoElement[2];
    		efficientInput[0] = geoList;
    		efficientInput[1] = (GeoElement) direction;
    	} else {    	
    		// points as input
    		efficientInput = new GeoElement[points.length+1];
    		for(int i = 0; i < points.length; i++)
    			efficientInput[i]=(GeoElement) points[i];
    		efficientInput[points.length] = (GeoElement) direction;
    	}    

    	return efficientInput;
    }
	
	
	
    /**
     * create the polygon
     * @param createSegments says if the polygon has to creates its edges (3D only)
     */
    @Override
	protected void createPolygon(boolean createSegments){
    	poly = new GeoPolygon3D(cons, points, cs2D, createSegments);
    }
	

    @Override
	public void compute() { 
    	
    	CoordSys coordsys = poly.getCoordSys();
    	
    	//recompute the coord sys
    	coordsys.resetCoordSys();
		
    	coordsys.addPoint(points[0].getInhomCoordsInD(3));
    	Coords[] v = direction.getDirectionInD3().completeOrthonormal();
		coordsys.addVector(v[0]);
		coordsys.addVector(v[1]);
		
		coordsys.makeOrthoMatrix(false,false);
    	
    	//check if a coord sys is possible
    	if (((GeoPolygon3D) poly).checkPointsAreOnCoordSys())
    		super.compute();
    	
    }

    @Override
	protected void createStringBuilder(StringTemplate tpl){
    	super.createStringBuilder(tpl);
        sb.append(' ');
        sb.append(app.getPlain("parallelTo"));
        sb.append(' ');
        sb.append(((GeoElement) direction).getLabel(tpl));
    }
    
    

}
