package geogebra3D.kernel3D;

import geogebra.kernel.AlgoCircleThreePoints;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

/**
 * @author ggb3D
 * 
 * Algo that creates a 3D circle joining three 3D points
 *
 */
public class AlgoCircle3DThreePoints extends AlgoCircleThreePoints {

	/** coord sys defined by the three points where the 3D circle lies */
	private CoordSys coordSys;
	
	/** 2D projection of the 3D points in the coord sys */
	private GeoPoint[] points2D;
	
	/** 3D points  */
	private GeoPointND[] points;
	
	
	/**
	 * Basic constructor
	 * @param cons construction
	 * @param label name of the circle
	 * @param A first point
	 * @param B second point
	 * @param C third point
	 */
	public AlgoCircle3DThreePoints(Construction cons, String label,
			GeoPointND A, GeoPointND B, GeoPointND C) {
		super(cons, label, A, B, C);
		
	}

	
	
    protected void setPoints(GeoPointND A, GeoPointND B, GeoPointND C){
    	
    	
    	points = new GeoPointND[3];
    	
    	points[0] = A;
    	points[1] = B;
       	points[2] = C;
            	

    	coordSys = new CoordSys(2);
    	
    	
    	points2D = new GeoPoint[3];
    	for (int i=0;i<3;i++)
    		points2D[i] = new GeoPoint(getConstruction());
    	
    	super.setPoints(points2D[0],points2D[1],points2D[2]);
    	
    	
    	
    }

    
    protected void createCircle(){
    	
        circle = new GeoConic3D(cons,coordSys);
    }
    
    
    protected void setInput() {
    	input = new GeoElement[3];
    	for (int i=0; i<3; i++)
    		input[i] = (GeoElement) points[i];

    }
    
    protected void setOutput() {

    	output = new GeoElement[1];
    	output[0] = circle;	

    }
    
    
  
    public void compute(){
    	

    	coordSys.resetCoordSys();
    	for(int i=0;i<3;i++)
			 coordSys.addPoint(points[i].getInhomCoordsInD(3));
   	
  
    	if (!coordSys.makeOrthoMatrix(false,false)){
    		circle.setUndefined();
    		return;
    	}
    	
    	//Application.debug("coordSys=\n"+coordSys.getMatrixOrthonormal().toString());

   
    	
    	for(int i=0;i<3;i++){
    		//project the point on the coord sys
    		//Coords[] project=points[i].getCoordsInD(3).projectPlane(coordSys.getMatrixOrthonormal());
    		Coords[] project=coordSys.getNormalProjection(points[i].getCoordsInD(3));
			 //set the 2D points
			 points2D[i].setCoords(project[1].getX(), project[1].getY(), project[1].getW());

    	}

    	super.compute();
    	
    }
    
    
    public String toString() {
    	return app.getPlain("CircleThroughABC",points[0].getLabel(),points[1].getLabel(),points[2].getLabel());
    }
}
