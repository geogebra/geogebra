package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

import java.util.Collection;

/**
 * @author ggb3D
 * 
 * Creates a new Prism
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
	
	
	
	
	
	
	@Override
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
	
	/**
	 * 
	 * @param index index of the point
	 * @return top point #index
	 */
	protected GeoPointND getTopPoint(int index){
		if (index==0)
			return getTopPoint();
		return outputPoints.getElement(index-getShift());
	}
	
	

	@Override
	protected void updateOutput(int newBottomPointsLength, GeoPointND[] bottomPoints) {
		
		//current length of top points
		int nOld = outputPoints.size()+getShift();
		
		
		
		if (newBottomPointsLength>nOld){
			
			
			int length=newBottomPointsLength-nOld;
			outputPoints.augmentOutputSize(length);
			outputPoints.setLabels(null);
			
			updateOutputPoints();

			//new sides of the prism		
			int l = nOld+length;
			for (int i=nOld; i<l; i++){
				polyhedron.startNewFace();
				polyhedron.addPointToCurrentFace(bottomPoints[i]);
				polyhedron.addPointToCurrentFace(bottomPoints[(i+1)%l]);
				polyhedron.addPointToCurrentFace(getTopPoint((i+1)%l));
				polyhedron.addPointToCurrentFace(getTopPoint(i));
				polyhedron.endCurrentFace();
				GeoPolygon3D polygon = polyhedron.createPolygon(i+1); //i+1 due to top face
				outputPolygonsSide.addOutput(polygon, false);
				outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[3],false);	
				outputSegmentsTop.addOutput((GeoSegment3D) polygon.getSegments()[2],false);				
			}
			outputSegmentsSide.setLabels(null);
			outputSegmentsTop.setLabels(null);		
			
			refreshOutput();
		}else if (newBottomPointsLength<nOld){
			
			for(int i=newBottomPointsLength; i<bottomPointsLength; i++){
    			outputPoints.getElement(i-getShift()).setUndefined();
    		}
			
			updateOutputPoints();
			
			//update top side
			outputSegmentsTop.getElement(newBottomPointsLength-1).modifyInputPoints(getTopPoint(newBottomPointsLength-1),getTopPoint());			
			GeoPolygon polygon = getTopFace();
			GeoPointND[] p = new GeoPointND[newBottomPointsLength];
			p[0]=getTopPoint();
			for(int i=0;i<newBottomPointsLength-1;i++)
				p[1+i] = getTopPoint(i+1);				
			//polygon.setPoints(p,null,false); //don't create segments
			polygon.modifyInputPoints(p);
			polygon.setSegments(outputSegmentsTop.getOutput(new GeoSegment3D[newBottomPointsLength]));
			polygon.calcArea();  
			
			//update last side
			polygon = outputPolygonsSide.getElement(newBottomPointsLength-1);
			p = new GeoPointND[4];
			p[0] = bottomPoints[newBottomPointsLength-1];
			p[1] = bottomPoints[0];
			p[2] = getTopPoint();
			p[3] = getTopPoint(newBottomPointsLength-1);
			polygon.setPoints(p,null,false); //don't create segments
			GeoSegmentND[] s = new GeoSegmentND[4];
			s[0] = getBottom().getSegments()[newBottomPointsLength];
			s[1] = outputSegmentsSide.getElement(newBottomPointsLength);
			s[2] = outputSegmentsTop.getElement(newBottomPointsLength);
			s[3] = outputSegmentsSide.getElement(newBottomPointsLength-1);
			polygon.setSegments(s);
			polygon.calcArea();  
			
			
		}else 
			updateOutputPoints();
		
		
		
		if (bottomPointsLength<newBottomPointsLength){
			
			//update top side
			updateTop(newBottomPointsLength);

			//update last sides
			for(int i=bottomPointsLength; i<newBottomPointsLength; i++)
				updateSide(i,bottomPoints);
		}
		
		
		bottomPointsLength=newBottomPointsLength;
	}
	
	private void updateTop(int n){
		
		GeoPolygon polygon = getTopFace();
		GeoPointND[] p = new GeoPointND[n];
		p[0]=getTopPoint();
		for(int i=0;i<n-1;i++)
			p[1+i] = getTopPoint(i+1);				
		//polygon.setPoints(p,null,false); //don't create segments
		polygon.modifyInputPoints(p);
		polygon.setSegments(outputSegmentsTop.getOutput(new GeoSegment3D[n]));
		polygon.calcArea();  
	}
	
	private void updateSide(int index, GeoPointND[] bottomPoints){
		outputSegmentsTop.getElement(index-1).modifyInputPoints(getTopPoint(index-1),getTopPoint(index));				
		GeoPolygon polygon = outputPolygonsSide.getElement(index-1);
		GeoPointND[] p = new GeoPointND[4];
		p[0] = bottomPoints[index-1];
		p[1] = bottomPoints[index];
		p[2] = getTopPoint(index);
		p[3] = getTopPoint(index-1);
		polygon.setPoints(p,null,false); //don't create segments
		GeoSegmentND[] s = new GeoSegmentND[4];
		s[0] = getBottom().getSegments()[index];
		s[1] = outputSegmentsSide.getElement(index);
		s[2] = outputSegmentsTop.getElement(index);
		s[3] = outputSegmentsSide.getElement(index-1);
		polygon.setSegments(s);
		polygon.calcArea();  
	}
	
	
	
	protected void removeBottomPoints(int length){
		for(int i=bottomPointsLength; i<bottomPointsLength+length; i++)
			outputPoints.getElement(i-getShift()).setUndefined();
		
	}
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	private Coords uptranslation, interiorPoint;
	
	
	@Override
	protected void updateOutputPoints(){
		
		//Application.printStacktrace("");
		
		if (height==null)
			uptranslation = getTopPoint().getInhomCoordsInD(3).sub(getBottomPoints()[0].getInhomCoordsInD(3));
		else
			uptranslation=bottom.getMainDirection().normalized().mul(height.getDouble());		

		
		GeoPointND[] bottomPoints = getBottomPoints();

		//translation from bottom to top
		for (int i=0;i<outputPoints.size() && i+getShift()<bottomPoints.length;i++)
			outputPoints.getElement(i).setCoords(bottomPoints[i+getShift()].getInhomCoordsInD(3).add(uptranslation),true);

		
		
		//TODO remove this and replace with tesselation
		interiorPoint = new Coords(4);
		for (int i=0;i<bottomPoints.length;i++){
			interiorPoint = interiorPoint.add(bottomPoints[i].getInhomCoordsInD(3));
		}
		interiorPoint = interiorPoint.mul((double) 1/(bottomPoints.length)).add(uptranslation.mul(0.5));
	}
	
	
	@Override
	public void compute() {

		if (!preCompute()){
			for (int i=0; i<bottomPointsLength-getShift(); i++)
				outputPoints.getElement(i).setUndefined();
			//bottomPointsLength=getBottom().getPointsLength();
			return;
		}
		
		if (!bottomAsInput)
			updateOutputPoints();


		polyhedron.setInteriorPoint(interiorPoint);


	}

	
	

	

    @Override
	public Algos getClassName() {

    	return Algos.AlgoPrism;

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
				outputSegmentsSide.addOutput((GeoSegment3D) polyhedron.getSegment(points[i], points[i+bottomPointsLength]),false);
			}

			//top
			outputPolygonsTop.addOutput(polyhedron.getFace(index), false);
			for (int i=0; i<bottomPointsLength; i++)
				outputSegmentsTop.addOutput((GeoSegment3D) polyhedron.getSegment(points[bottomPointsLength+i], points[bottomPointsLength+((i+1) % bottomPointsLength)]),false);
		}else{
			//Application.debug("ici");
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
	

	@Override
	protected int getSideLengthFromLabelsLength(int length){

		if (bottomAsInput)
			return (length + getShift() - 2)/4;

		return (length + getShift() - 3)/5;

	}

	// TODO Consider locusequability

    

}
