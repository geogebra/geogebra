package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
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
	protected void updateOutput(int newBottomPointsLength, GeoPointND[] bottomPoints) {
		
		updateOutputPoints();
		
		//current length
		int nOld = outputSegmentsSide.size();
		
		/*
		Application.printStacktrace("nOld:"+nOld
				+"\nbottomPointsLength:"+bottomPointsLength
				+"\nnewBottomPointsLength:"+newBottomPointsLength
				+"\noutputSegmentsSide:"+outputSegmentsSide.size());
				*/
		
		if (newBottomPointsLength>nOld){
			//int length=newBottomPointsLength-nOld;
			for (int i=nOld; i<newBottomPointsLength; i++){
				polyhedron.startNewFace();
				polyhedron.addPointToCurrentFace(bottomPoints[i]);
				polyhedron.addPointToCurrentFace(bottomPoints[(i+1)%newBottomPointsLength]);
				polyhedron.addPointToCurrentFace(getTopPoint());
				polyhedron.endCurrentFace();
				GeoPolygon3D polygon = polyhedron.createPolygon(i);
				outputPolygonsSide.addOutput(polygon, false);
				outputSegmentsSide.addOutput((GeoSegment3D) polygon.getSegments()[2],false);
			}
			outputSegmentsSide.setLabels(null);	
			
			refreshOutput();
		}else if (newBottomPointsLength<nOld){
			
			
			//update last side
			GeoPolygon polygon = outputPolygonsSide.getElement(newBottomPointsLength-1);
			GeoPointND[] p = new GeoPointND[3];
			p[0] = bottomPoints[newBottomPointsLength-1];
			p[1] = bottomPoints[0];
			p[2] = getTopPoint();
			polygon.setPoints(p,null,false); //don't create segments
			GeoSegmentND[] s = new GeoSegmentND[3];
			s[0] = getBottom().getSegments()[newBottomPointsLength];
			s[1] = outputSegmentsSide.getElement(newBottomPointsLength);
			s[2] = outputSegmentsSide.getElement(0);
			polygon.setSegments(s);
			polygon.calcArea();  
			
			
		}
		
		/*
		Application.debug("nOld:"+nOld
				+"\nbottomPointsLength:"+bottomPointsLength
				+"\nnewBottomPointsLength:"+newBottomPointsLength
				+"\noutputSegmentsSide:"+outputSegmentsSide.size());
		*/
		
		if (bottomPointsLength<newBottomPointsLength){
			//update last sides
			for(int i=bottomPointsLength; i<newBottomPointsLength; i++)
				updateSide(i,bottomPoints);
		}
		
		
		bottomPointsLength=newBottomPointsLength;
		
	}

	private void updateSide(int index, GeoPointND[] bottomPoints){
		GeoPolygon polygon = outputPolygonsSide.getElement(index-1);
		GeoPointND[] p = new GeoPointND[3];
		p[0] = bottomPoints[index-1];
		p[1] = bottomPoints[index];
		p[2] = getTopPoint();
		polygon.setPoints(p,null,false); //don't create segments
		GeoSegmentND[] s = new GeoSegmentND[3];
		s[0] = getBottom().getSegments()[index];
		s[1] = outputSegmentsSide.getElement(index);
		s[2] = outputSegmentsSide.getElement(index-1);
		polygon.setSegments(s);
		polygon.calcArea();  
	}
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	private Coords interiorPoint = new Coords(4);
	private Coords bottomCenter = new Coords(4);
	
	@Override
	protected void updateOutputPoints(){
		
		GeoPointND[] bottomPoints = getBottomPoints();
		
		Coords bottomCenter1 = new Coords(4);
		//interiorPoint.set(0);
		for (int i=0;i<bottomPoints.length;i++){
			bottomCenter1 = bottomCenter1.add(bottomPoints[i].getInhomCoordsInD(3));
		}
		
		bottomCenter = bottomCenter1.mul((double) 1/(bottomPoints.length));
		
		if (height!=null){
			Coords v = bottom.getMainDirection().normalized().mul(height.getDouble());
			getTopPoint().setCoords(bottomCenter.add(v),true);
		}
		
		interiorPoint=(bottomCenter1.add(getTopPoint().getInhomCoordsInD(3))).mul((double) 1/(bottomPoints.length+1));
	}
	
	
	@Override
	public void compute() {
		
		//updateInteriorPoint();

		if (!preCompute()){
			if (height!=null)
				((GeoPoint3D) getTopPoint()).setUndefined();
			return;
		}
		
		
		polyhedron.setInteriorPoint(interiorPoint);
		//Application.debug("interior\n"+interiorPoint);

	}

	
	

	@Override
	public Commands getClassName() {
        return Commands.Pyramid;
    }
    
    

	@Override
	protected void updateOutput(){
		//Application.printStacktrace("");
		
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
			//Application.debug(faces.size());
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
					//Application.debug(outputSegmentsSide.size());
				}
			}
		}
		

		
		
		refreshOutput();
		
	}

	@Override
	protected int getSideLengthFromLabelsLength(int length){
		
		//Application.debug("bottomAsInput="+bottomAsInput+",shift="+getShift());

		if (bottomAsInput)
			return (length + getShift() -2)/2;

		return (length + getShift() - 3)/3;	

	}

	// TODO Consider locusequability
}
