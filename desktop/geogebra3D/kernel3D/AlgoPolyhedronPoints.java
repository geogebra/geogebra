package geogebra3D.kernel3D;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElementCycle;
import geogebra.main.Application;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public abstract class AlgoPolyhedronPoints extends AlgoPolyhedron{

	
	private GeoPointND[] bottomPoints;
	private GeoPointND topPoint;
	protected GeoPolygon bottom;
	private NumberValue height;
	
	protected boolean bottomAsInput = false;
	protected int bottomPointsLength = -1;
	

	
	
	/////////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	////////////////////////////////////////////
	
	
	
	/** creates a polyhedron regarding vertices 
	 * @param c construction 
	 * @param labels 
	 * @param points 
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels, GeoPointND[] points) {
		super(c);


		bottomPoints = new GeoPointND[points.length-1];
		for (int i=0; i<points.length-1; i++)
			bottomPoints[i] = points[i];
		topPoint = points[points.length-1];
		shift=1; //output points are shifted of 1 to input points
			
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		
		createPolyhedron(polyhedron);
		polyhedron.updateFaces();
		
		compute();
		
		// input : inputPoints or list of faces
		input = new GeoElement[points.length];
		for (int i=0; i<points.length; i++)
			input[i] = (GeoElement) points[i];		
		addAlgoToInput();
		
		polyhedron.updateFaces();
		setOutput();
		
		
        
        
        //polyhedron.defaultLabels(labels);
        polyhedron.initLabels(labels);
	}
	
	
	/** creates a polyhedron regarding bottom face and top vertex 
	 * @param c construction 
	 * @param labels 
	 * @param polygon 
	 * @param point 
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels, GeoPolygon polygon, GeoPointND point) {
		super(c);
		
		bottom = polygon;
		bottomAsInput = true;
		topPoint = point;
		shift=1; //output points are shifted of 1 to input points
		
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		
		createPolyhedron(polyhedron);

		update();
		
		// input : inputPoints or list of faces
		input = new GeoElement[2];
		input[0]=bottom;
		input[1]=(GeoElement) topPoint;
		addAlgoToInput();
		

		
		polyhedron.updateFaces();
		setOutput(); 
        
        setLabels(labels);
        
	}
	
	
	protected void setLabels(String[] labels){
		getPolyhedron().initLabels(labels);
	}
	
	
	/** creates a polyhedron regarding bottom face and top vertex 
	 * @param c construction 
	 * @param labels 
	 * @param polygon 
	 * @param height 
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels, GeoPolygon polygon, NumberValue height) {
		super(c);

		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		
		bottom = polygon;
		bottomAsInput = true;
		this.height = height;
		shift=0; //output points correspond to input points
		
		outputPoints.augmentOutputSize(1);
		topPoint=outputPoints.getElement(0);
		polyhedron.addPointCreated((GeoPoint3D) topPoint);
		createPolyhedron(polyhedron);
		
		update();
		
		// input : inputPoints or list of faces
		input = new GeoElement[2];
		input[0]=bottom;
		input[1]=(GeoElement) height;
		addAlgoToInput();
		
		polyhedron.updateFaces();
		setOutput(); 
		
		if (height instanceof GeoNumeric){
			if (((GeoNumeric) height).isIndependent()){

				for (GeoPolygon p : getPolyhedron().getPolygons()){
					p.setCoordParentNumber((GeoNumeric) height);
					p.setCoordParentDirector(bottom);
				}
				
				//getTopFace().setCoordParentNumber((GeoNumeric) height);
				//getTopFace().setCoordParentDirector(bottom);
			}
		}
      
        polyhedron.initLabels(labels);
	}

	
	/**
	 * create the polyhedron (faces and edges)
	 * @param polyhedron
	 */
	protected abstract void createPolyhedron(GeoPolyhedron polyhedron);
	
	protected abstract void addBottomPoints(int length);
	
	protected abstract void removeBottomPoints(int length);
	
	/**
	 * sets the bottom of the polyhedron
	 * @param polyhedron
	 * @return bottom key (if one)
	 */
	protected long setBottom(GeoPolyhedron polyhedron){
		if (bottom!=null){
			polyhedron.addPolygonLinked(bottom);
			return -1;
		}else{
			GeoPointND[] bottomPoints = getBottomPoints();
			
			polyhedron.startNewFace();
			for (int i=0; i<bottomPoints.length; i++){
				//polyhedron.createSegment(bottomPoints[i], bottomPoints[(i+1)%(bottomPointsLength)]);
				polyhedron.addPointToCurrentFace(bottomPoints[i]);
			}
			return polyhedron.endCurrentFace();
		}
	}
	
	protected GeoPolygon getBottom(){
		if (bottom!=null)
			return bottom;
		else
			return outputPolygons.getElement(0);
	}
	
	
	
	
	/////////////////////////////////////////////
	// END OF THE CONSTRUCTION
	////////////////////////////////////////////

	private Coords uptranslation;
	
	protected Coords getUpTranslation(){
		return uptranslation;
	}
	
	private int shift;
	
	protected int getShift(){
		return shift;
	}
	
	
	public void compute() {
		
		//check if bottom points length has changed (e.g. with regular polygon)
		/*
		if (bottomAsInput && bottom.getPointsLength()!=bottomPointsLength){
			Application.debug("bottom.getPointsLength()!=bottomPointsLength");
			int shift = bottom.getPointsLength()-bottomPointsLength;
			if (shift>0){
				addBottomPoints(shift);
				bottomPointsLength+=shift;
			}else if (shift<0){
				bottomPointsLength+=shift;
				removeBottomPoints(-shift);				
			}
		}
		*/	
		
		//recompute the translation from bottom to top
		if (height==null)
			uptranslation = getTopPoint().getInhomCoordsInD(3).sub(getBottomPoints()[0].getInhomCoordsInD(3));
		else
			uptranslation=bottom.getMainDirection().normalized().mul(height.getDouble());		
		

	}
	
	
	
	
	
	

	
	
	
	/**
	 * 
	 * @return bottom points
	 */
	protected GeoPointND[] getBottomPoints(){
		if (bottom!=null)
			return bottom.getPointsND();
		else
			return bottomPoints;
	}
	

	/**
	 * 
	 * @return top point
	 */
	protected GeoPointND getTopPoint(){
		return topPoint;
	}



    
    
    
    ///////////////////////////////////////////////////////
    // FOR PREVIEWABLE   
    ///////////////////////////////////////////////////////
    

	/**
	 * set visibility of output segments and polygons
	 * @param visible
	 */
	public void setOutputSegmentsAndPolygonsEuclidianVisible(boolean visible){
		for (int i=0; i<outputSegments.size(); i++)
			outputSegments.getElement(i).setEuclidianVisible(visible);
		for (int i=0; i<outputPolygons.size(); i++)
			outputPolygons.getElement(i).setEuclidianVisible(visible, false);
	}
	
	/**
	 * notify kernel update of output segments and polygons
	 */
	public void notifyUpdateOutputSegmentsAndPolygons(){
		for (int i=0; i<outputSegments.size(); i++)
			getKernel().notifyUpdate(outputSegments.getElement(i));
		for (int i=0; i<outputPolygons.size(); i++)
			getKernel().notifyUpdate(outputPolygons.getElement(i));
	}


	/**
	 * set output points invisible (use for previewable)
	 * @param visible 
	 */
	public void setOutputPointsEuclidianVisible(boolean visible){
		for (int i=0; i<outputPoints.size(); i++)
			outputPoints.getElement(i).setEuclidianVisible(visible);
	}
	
	
	/**
	 * notify kernel update of output points
	 */
	public void notifyUpdateOutputPoints(){
		for (int i=0; i<outputPoints.size(); i++)
			getKernel().notifyUpdate(outputPoints.getElement(i));
	}
	

	
	/**
	 * used for previewable of prism
	 * @return the middle point of the bottom face (for prism)
	 */
	public Coords getBottomMiddlePoint(){
		Coords ret = new Coords(4);
		
		GeoPointND[] points = getBottomPoints();
			
		
		for (int i=0; i<points.length; i++)
			ret = ret.add(points[i].getCoordsInD(3));
		
		return ret.mul((double) 1/points.length);
	}

	/**
	 * used for previewable of prism
	 * @return the middle point of the top face (for prism)
	 */
	public Coords getTopMiddlePoint(){
		Coords ret = new Coords(4);
		for (int i=0; i<outputPoints.size(); i++)
			ret = ret.add(outputPoints.getElement(i).getCoordsInD(3));
		
		return ret.mul((double) 1/outputPoints.size());
	}
	
	
	public GeoPolygon getTopFace(){
		return outputPolygons.getElement(outputPolygons.size()-1);
		
	}
	
	public NumberValue getHeight(){
		return height;
	}
    
	
	
	
	

	
}
