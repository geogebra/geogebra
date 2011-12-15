package geogebra3D.kernel3D;

import java.util.Collection;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoElement.OutputHandler;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.main.Application;

/**
 * @author ggb3D
 * 
 * Creates a new Pyramid
 *
 */
public class AlgoPolyhedronPointsPrism extends AlgoPolyhedronPoints{
	


	/**
	 * @param c
	 * @param labels
	 * @param polygon
	 * @param point
	 */
	public AlgoPolyhedronPointsPrism(Construction c, String[] labels, GeoPolygon polygon, GeoPointND point) {
		super(c, labels, polygon, point);

	}
	
	/**
	 * @param c
	 * @param labels
	 * @param points
	 */
	public AlgoPolyhedronPointsPrism(Construction c, String[] labels, GeoPointND[] points) {
		super(c,labels,points);

	}
	
	
	/**
	 * @param c
	 * @param labels
	 * @param polygon
	 * @param height
	 */
	public AlgoPolyhedronPointsPrism(Construction c, String[] labels, GeoPolygon polygon, NumberValue height) {
		super(c, labels, polygon, height);

	}
	
	
	
	
	
	
	protected void createPolyhedron(){

		GeoPointND[] bottomPoints = getBottomPoints();
		GeoPointND topPoint = getTopPoint();
		
		bottomPointsLength = bottomPoints.length;
		
		///////////
		//vertices
		///////////
		

		outputPoints.augmentOutputSize(bottomPointsLength-1);

		points = new GeoPointND[bottomPointsLength*2];
		for(int i=0;i<bottomPointsLength;i++)
			points[i] = bottomPoints[i];
		points[bottomPointsLength] = topPoint;		
		for(int i=0;i<bottomPointsLength-1;i++){
			GeoPoint3D point = outputPoints.getElement(i+1-getShift());
			points[bottomPointsLength+1+i] = point;
			polyhedron.addPointCreated(point);
		}
		
		
		///////////
		//faces
		///////////
		
		//bottom
		setBottom(polyhedron);

		
		//sides of the prism
		for (int i=0; i<bottomPointsLength; i++){
			polyhedron.startNewFace();
			polyhedron.addPointToCurrentFace(points[i]);
			polyhedron.addPointToCurrentFace(points[(i+1)%(bottomPointsLength)]);
			polyhedron.addPointToCurrentFace(points[bottomPointsLength + ((i+1)%(bottomPointsLength))]);
			polyhedron.addPointToCurrentFace(points[bottomPointsLength + i]);
			polyhedron.endCurrentFace();
		}
		

		
		//top of the prism
		polyhedron.startNewFace();
		for (int i=0; i<bottomPointsLength; i++)
			polyhedron.addPointToCurrentFace(points[bottomPointsLength+i]);
		
		polyhedron.endCurrentFace();
		
		//for (int i=0; i<faces.length; i++) Application.debug(faces[i]);


		polyhedron.setType(GeoPolyhedron.TYPE_PRISM);
		
	}
	
	
	

	protected void updateOutput(int n) {
		
		//current length of top points
		int nOld = outputPoints.size()+getShift();
		
		if (nOld==n)
    		return;
		
		if (n>nOld){
			int length=n-nOld;
			outputPoints.augmentOutputSize(length);
			outputPoints.setLabels(null);

			//new sides of the prism
			GeoPointND[] bottomPoints = getBottomPoints();
			int l = bottomPointsLength+length;
			for (int i=bottomPointsLength; i<l; i++){
				polyhedron.startNewFace();
				polyhedron.addPointToCurrentFace(bottomPoints[i]);
				polyhedron.addPointToCurrentFace(bottomPoints[(i+1)%l]);
				int index = ((i+1)%l)-getShift();
				GeoPointND point;
				if (index==-1)
					point = getTopPoint();
				else
					point = outputPoints.getElement(index);
				polyhedron.addPointToCurrentFace(point);
				polyhedron.addPointToCurrentFace(outputPoints.getElement(i-getShift()));
				polyhedron.endCurrentFace();
				GeoPolygon3D polygon = polyhedron.createPolygon(i+1);
				outputPolygonsSide.addOutput(polygon, false);
				outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[3],false);	
				outputSegmentsTop.addOutput((GeoSegment3D) polygon.getSegments()[2],false);				
			}
			outputSegmentsSide.setLabels(null);
			outputSegmentsTop.setLabels(null);
			
			//update top side
			outputSegmentsTop.getElement(bottomPointsLength-1).modifyInputPoints(outputPoints.getElement(bottomPointsLength-1),outputPoints.getElement(bottomPointsLength));			
			GeoPolygon polygon = getTopFace();
			GeoPointND[] p = new GeoPointND[n];
			p[0]=getTopPoint();
			for(int i=0;i<n-1;i++)
				p[1+i] = outputPoints.getElement(i+1-getShift());				
			polygon.setPoints(p,null,false); //don't create segments
			polygon.setSegments(outputSegmentsTop.getOutput(new GeoSegment3D[n]));
			polygon.calcArea();  
			
			//update last side
			polygon = outputPolygonsSide.getElement(bottomPointsLength-1);
			p = new GeoPointND[4];
			p[0] = bottomPoints[bottomPointsLength-1];
			p[1] = bottomPoints[bottomPointsLength];
			p[2] = outputPoints.getElement(bottomPointsLength);
			p[3] = outputPoints.getElement(bottomPointsLength-1);
			polygon.setPoints(p,null,false); //don't create segments
			GeoSegmentND[] s = new GeoSegmentND[4];
			s[0] = getBottom().getSegments()[bottomPointsLength];
			s[1] = outputSegmentsSide.getElement(bottomPointsLength);
			s[2] = outputSegmentsTop.getElement(bottomPointsLength);
			s[3] = outputSegmentsSide.getElement(bottomPointsLength-1);
			polygon.setSegments(s);
			polygon.calcArea();  
			
			refreshOutput();
			bottomPointsLength=n;
		}
	}
	
	
	protected void removeBottomPoints(int length){
		for(int i=bottomPointsLength; i<bottomPointsLength+length; i++)
			outputPoints.getElement(i-getShift()).setUndefined();
		
	}
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	
	
	
	public void compute() {

		super.compute();

		GeoPointND[] bottomPoints = getBottomPoints();

		//translation from bottom to top
		Coords v = getUpTranslation();
		
		//translate all output points
		for (int i=0;i<bottomPointsLength-getShift();i++)
			outputPoints.getElement(i).setCoords(bottomPoints[i+getShift()].getInhomCoordsInD(3).add(v),true);

		//TODO remove this and replace with tesselation
		Coords interiorPoint = new Coords(4);
		for (int i=0;i<bottomPoints.length;i++){
			interiorPoint = (Coords) interiorPoint.add(bottomPoints[i].getInhomCoordsInD(3));
		}
		interiorPoint = (Coords) interiorPoint.mul((double) 1/(bottomPoints.length));
		polyhedron.setInteriorPoint((Coords) interiorPoint.add(v.mul(0.5)));


	}

	public void update() {

		// compute and polyhedron
		super.update();
		
		//output points
		for (int i=0;i<outputPoints.size();i++)
			outputPoints.getElement(i).update();

	}

	

    public String getClassName() {

    	return "AlgoPrism";

    }
    
    
 

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
				outputSegmentsSide.addOutput((GeoSegment3D) polyhedron.getSegment(points[i], points[i+bottomPointsLength]),false);
			}

			//top
			outputPolygonsTop.addOutput(polyhedron.getFace(index), false);
			for (int i=0; i<bottomPointsLength; i++)
				outputSegmentsTop.addOutput((GeoSegment3D) polyhedron.getSegment(points[bottomPointsLength+i], points[bottomPointsLength+((i+1) % bottomPointsLength)]),false);
		}else{
			Collection<GeoPolygon3D> faces = polyhedron.getFacesCollection();
			int top = faces.size();			
			int step = 1;
			for (GeoPolygon polygon : faces){
				
				GeoSegmentND[] segments = polygon.getSegments();
				if(step==1 && !bottomAsInput){//bottom
					outputPolygonsBottom.addOutput((GeoPolygon3D) polygon, false);
					for (int i=0; i<segments.length; i++)
						outputSegmentsBottom.addOutput((GeoSegment3D) segments[i],false);	
					step++;
					continue;
				}else if(step==top){//top
					outputPolygonsTop.addOutput((GeoPolygon3D) polygon, false);
					for (int i=0; i<segments.length; i++)
						outputSegmentsTop.addOutput((GeoSegment3D) segments[i],false);	
					step++;
					continue;
				}

				//sides
				outputPolygonsSide.addOutput((GeoPolygon3D) polygon, false);
				outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[3],false);		
				step++;
			}
		}
		

		
		refreshOutput();
		
	}
	
    

}
