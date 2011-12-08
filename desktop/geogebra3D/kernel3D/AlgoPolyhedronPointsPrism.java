package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.ConstructionElementCycle;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoPolygon;
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
	

	private long[] faces;
	private GeoPointND[] points;

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
	
	
	
	
	
	
	protected void createPolyhedron(GeoPolyhedron polyhedron){

		GeoPointND[] bottomPoints = getBottomPoints();
		GeoPointND topPoint = getTopPoint();
		
		bottomPointsLength = bottomPoints.length;
		
		///////////
		//vertices
		///////////

		points = new GeoPointND[bottomPointsLength*2];
		outputPoints.augmentOutputSize(bottomPointsLength-1);
		//outputPoints.setLabels(null);
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
		
		faces = new long[2+bottomPointsLength];
		
		//bottom
		faces[0]=setBottom(polyhedron);

		
		//sides of the prism
		for (int i=0; i<bottomPointsLength; i++){
			polyhedron.startNewFace();
			polyhedron.addPointToCurrentFace(points[i]);
			polyhedron.addPointToCurrentFace(points[(i+1)%(bottomPointsLength)]);
			polyhedron.addPointToCurrentFace(points[bottomPointsLength + ((i+1)%(bottomPointsLength))]);
			polyhedron.addPointToCurrentFace(points[bottomPointsLength + i]);
			faces[i+1]=polyhedron.endCurrentFace();
		}
		

		
		//top of the prism
		polyhedron.startNewFace();
		for (int i=0; i<bottomPointsLength; i++)
			polyhedron.addPointToCurrentFace(points[bottomPointsLength+i]);
		faces[1+bottomPointsLength]=polyhedron.endCurrentFace();


		polyhedron.setType(GeoPolyhedron.TYPE_PRISM);
		
	}
	
	

	
	protected void updateOutput(){
		
		//add polyhedron's segments and polygons, without setting this algo as algoparent
		GeoPolyhedron polyhedron = getPolyhedron();
		
		if (faces[0]!=-1){ //check bottom
			outputPolygons.addOutput(polyhedron.getFace(faces[0]), false);
			for (int i=0; i<bottomPointsLength; i++)
				outputSegments.addOutput((GeoSegment3D) polyhedron.getSegment(points[i], points[(i+1) % bottomPointsLength]),false);
		}
		
		//sides
		for (int i=0; i<bottomPointsLength; i++){
			outputPolygons.addOutput(polyhedron.getFace(faces[i+1]), false);
			outputSegments.addOutput((GeoSegment3D) polyhedron.getSegment(points[i], points[i+bottomPointsLength]),false);
		}

		//top
		outputPolygons.addOutput(polyhedron.getFace(faces[bottomPointsLength+1]), false);
		for (int i=0; i<bottomPointsLength; i++)
			outputSegments.addOutput((GeoSegment3D) polyhedron.getSegment(points[bottomPointsLength+i], points[bottomPointsLength+((i+1) % bottomPointsLength)]),false);

		//Application.debug(outputSegments.size());
		//for (int i=0; i<outputSegments.size(); i++) Application.debug("segment "+i+":"+outputSegments.getElement(i).getParentAlgorithm());

		
		refreshOutput();
		
	}
	
	
	protected void setLabels(String[] labels){
		if (labels==null || labels.length <= 1 || app.fileVersionBefore(Application.getSubValues("4.9.10.0")))
			super.setLabels(labels);
		else{
			for (int i=0; i<labels.length; i++)
				getOutput(i).setLabel(labels[i]);
		}
	}

	
	protected void addBottomPoints(int length){
		outputPoints.augmentOutputSize(length);
		
		//new sides of the prism
		GeoPolyhedron polyhedron = getPolyhedron();
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
		}
		
		polyhedron.updateFaces();
		
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

		GeoPolyhedron polyhedron = getPolyhedron();
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
    
    

}
