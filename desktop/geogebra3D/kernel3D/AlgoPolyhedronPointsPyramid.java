package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;

import java.util.Collection;

/**
 * @author ggb3D
 * 
 * Creates a new Pyramid
 *
 */
public class AlgoPolyhedronPointsPyramid extends AlgoPolyhedronPoints{
	


	
	/** creates a pyramid regarding vertices
	 * @param c construction 
	 * @param labels labels
	 * @param points vertices
	 */
	public AlgoPolyhedronPointsPyramid(Construction c, String[] labels, GeoPointND[] points) {
		super(c,labels,points);

	}
	
	/**
	 * 
	 * @param c construction
	 * @param labels labels
	 * @param polygon bottom face
	 * @param point top vertex
	 */
	public AlgoPolyhedronPointsPyramid(Construction c, String[] labels, GeoPolygon polygon, GeoPointND point) {
		super(c, labels, polygon, point);

	}
	
	
	/**
	 * pyramid with top point over center of bottom face
	 * @param c
	 * @param labels
	 * @param polygon
	 * @param height
	 */
	public AlgoPolyhedronPointsPyramid(Construction c, String[] labels, GeoPolygon polygon, NumberValue height) {
		super(c, labels, polygon, height);

	}
	
	@Override
	protected void createPolyhedron(){

		GeoPointND[] bottomPoints = getBottomPoints();
		GeoPointND topPoint = getTopPoint();
		
		bottomPointsLength = bottomPoints.length;
		

		///////////
		//vertices
		///////////

		points = new GeoPointND[bottomPointsLength+1];
		for(int i=0;i<bottomPointsLength;i++)
			points[i] = bottomPoints[i];
		points[bottomPointsLength] = topPoint;

		///////////
		//faces
		///////////

		//base of the pyramid
		setBottom(polyhedron);

		//sides of the pyramid
		for (int i=0; i<bottomPointsLength; i++){
			polyhedron.startNewFace();
			polyhedron.addPointToCurrentFace(bottomPoints[i]);
			polyhedron.addPointToCurrentFace(bottomPoints[(i+1)%(bottomPointsLength)]);
			polyhedron.addPointToCurrentFace(topPoint);//apex
			polyhedron.endCurrentFace();
		}


		polyhedron.setType(GeoPolyhedron.TYPE_PYRAMID);
		
	}
	
	
	@Override
	protected void updateOutput(int n, GeoPointND[] bottomPoints) {
		
	}
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	
	
	
	@Override
	public void compute() {
		
		updateInteriorPoint();

		if (!preCompute()){
			for (int i=0; i<bottomPointsLength-getShift(); i++)
				outputPoints.getElement(i).setUndefined();
			//bottomPointsLength=getBottom().getPointsLength();
			return;
		}
		
		
		polyhedron.setInteriorPoint(interiorPoint);
		//Application.debug("interior\n"+interiorPoint);

	}

	
	private Coords interiorPoint = new Coords(4);
	
	private void updateInteriorPoint(){
		GeoPointND[] bottomPoints = getBottomPoints();
		
		interiorPoint = new Coords(4);
		//interiorPoint.set(0);
		for (int i=0;i<bottomPoints.length;i++){
			interiorPoint = interiorPoint.add(bottomPoints[i].getCoordsInD(3));
		}
		interiorPoint = interiorPoint.add(getTopPoint().getCoordsInD(3));
		
		interiorPoint = interiorPoint.mul((double) 1/(bottomPoints.length+1));
	}
	

	@Override
	protected void updateBottomToTop(){
		//recompute the translation from bottom to top
		if (height!=null){
			Coords v = bottom.getMainDirection().normalized().mul(height.getDouble());
			getTopPoint().setCoords(interiorPoint.add(v),true);
		}
		
	}
	
	@Override
	public void update() {

		// compute and polyhedron
		super.update();
		
		//top point
		if (height!=null)
			((GeoPoint3D) getTopPoint()).update();

	}

    @Override
	public Algos getClassName() {

    	return Algos.AlgoPyramid;

    }
    
    

	@Override
	protected void updateOutput(){
		if (isOldFileVersion()){
			//add polyhedron's segments and polygons, without setting this algo as algoparent		
			int index = 0;
			if (!bottomAsInput){ //check bottom
				outputPolygonsBottom.addOutput(polyhedron.getFace(index), false);
				index++;
				for (int i=0; i<bottomPointsLength; i++)
					outputSegmentsBottom.addOutput((GeoSegment3D) polyhedron.getSegment(points[i], points[(i+1) % bottomPointsLength]),false);
			}
			
			//sides
			for (int i=0; i<bottomPointsLength; i++){
				outputPolygonsSide.addOutput(polyhedron.getFace(index), false);
				index++;
				outputSegmentsSide.addOutput((GeoSegment3D) polyhedron.getSegment(points[i], points[bottomPointsLength]),false);
			}

		}else{
			Collection<GeoPolygon3D> faces = polyhedron.getFacesCollection();
			int step = 1;
			for (GeoPolygon polygon : faces){
				GeoSegmentND[] segments = polygon.getSegments();
				if(step==1 && !bottomAsInput){//bottom
					outputPolygonsBottom.addOutput((GeoPolygon3D) polygon, false);
					for (int i=0; i<segments.length; i++)
						outputSegmentsBottom.addOutput((GeoSegment3D) segments[i],false);	
					step++;
				}else{//sides
					outputPolygonsSide.addOutput((GeoPolygon3D) polygon, false);
					outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[2],false);		
					step++;
				}
			}
		}
		

		
		refreshOutput();
		
	}

	@Override
	protected void augmentOutputSize(int length){
		
	}
}
