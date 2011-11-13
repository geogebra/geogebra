package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;

/**
 * @author ggb3D
 * 
 * Creates a new GeoPolyhedron
 *
 */
public abstract class AlgoPolyhedronPoints extends AlgoElement3D{

	
	
	private GeoPointND[] bottomPoints;
	private GeoPointND topPoint;
	private GeoPolygon bottom;
	private NumberValue height;
	

	
	private OutputHandler<GeoSegment3D> outputSegments;
	private OutputHandler<GeoPolygon3D> outputPolygons;
	
	/** points generated as output  */
	protected OutputHandler<GeoPoint3D> outputPoints;

	private OutputHandler<GeoPolyhedron> outputPolyhedron;
	
	
	/////////////////////////////////////////////
	// POLYHEDRON OF DETERMINED TYPE
	////////////////////////////////////////////
	
	private AlgoPolyhedronPoints(Construction c){
		super(c);


		outputPolyhedron=new OutputHandler<GeoPolyhedron>(new elementFactory<GeoPolyhedron>() {
			public GeoPolyhedron newElement() {
				GeoPolyhedron p=new GeoPolyhedron(cons);
				p.setParentAlgorithm(AlgoPolyhedronPoints.this);
				return p;
			}
		});
		
		
		outputPoints=new OutputHandler<GeoPoint3D>(new elementFactory<GeoPoint3D>() {
			public GeoPoint3D newElement() {
				GeoPoint3D p=new GeoPoint3D(cons);
				p.setCoords(0, 0, 0, 1);
				p.setParentAlgorithm(AlgoPolyhedronPoints.this);
				return p;
			}
		});
		

		outputPolygons=new OutputHandler<GeoPolygon3D>(new elementFactory<GeoPolygon3D>() {
			public GeoPolygon3D newElement() {
				GeoPolygon3D p=new GeoPolygon3D(cons);
				return p;
			}
		});
		
		
			
		
		outputSegments=new OutputHandler<GeoSegment3D>(new elementFactory<GeoSegment3D>() {
			public GeoSegment3D newElement() {
				GeoSegment3D s=new GeoSegment3D(cons);
				return s;
			}
		});
		


		
		
	}
	
	
	/** creates a polyhedron regarding vertices 
	 * @param c construction 
	 * @param labels 
	 * @param points 
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels, GeoPointND[] points) {
		this(c);

		outputPolyhedron.adjustOutputSize(1);
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);



		bottomPoints = new GeoPointND[points.length-1];
		for (int i=0; i<points.length-1; i++)
			bottomPoints[i] = points[i];
		topPoint = points[points.length-1];
		shift=1; //output points are shifted of 1 to input points
			
		createPolyhedron(polyhedron);
		//polyhedron.updateFaces();
		
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
		this(c);

		outputPolyhedron.adjustOutputSize(1);
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		
		bottom = polygon;
		topPoint = point;
		shift=1; //output points are shifted of 1 to input points
		
		createPolyhedron(polyhedron);
		
		update();
		
		// input : inputPoints or list of faces
		input = new GeoElement[2];
		input[0]=bottom;
		input[1]=(GeoElement) topPoint;
		addAlgoToInput();
		
		polyhedron.updateFaces();
		setOutput(); 
		
		
        
        
        polyhedron.initLabels(labels);
	}
	
	
	/** creates a polyhedron regarding bottom face and top vertex 
	 * @param c construction 
	 * @param labels 
	 * @param polygon 
	 * @param height 
	 */
	public AlgoPolyhedronPoints(Construction c, String[] labels, GeoPolygon polygon, NumberValue height) {
		this(c);

		outputPolyhedron.adjustOutputSize(1);
		GeoPolyhedron polyhedron = outputPolyhedron.getElement(0);
		
		bottom = polygon;
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
	
	
	/**
	 * sets the bottom of the polyhedron
	 * @param polyhedron
	 */
	protected void setBottom(GeoPolyhedron polyhedron){
		if (bottom!=null)
			polyhedron.addPolygonLinked(bottom);
		else{
			GeoPointND[] bottomPoints = getBottomPoints();
			
			polyhedron.startNewFace();
			for (int i=0; i<bottomPoints.length; i++)
				polyhedron.addPointToCurrentFace(bottomPoints[i]);
			polyhedron.endCurrentFace();
		}
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
	
	
	protected void compute() {
		if (height==null)
			uptranslation = getTopPoint().getInhomCoordsInD(3).sub(getBottomPoints()[0].getInhomCoordsInD(3));
		else{
			//Coords v = bottom.getMainDirection();
			//Application.debug(height.getDouble()+"\nv=\n"+v);
			uptranslation=bottom.getMainDirection().normalized().mul(height.getDouble());		
		}

	}
	
	
	private void addAlgoToInput(){
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
	}

		
	private void setOutput(){
		
		updateOutput();
        cons.addToAlgorithmList(this);  
		
	}
	
	
	
	
	
	private void updateOutput(){
		
		//add polyhedron's segments and polygons, without setting this algo as algoparent
		GeoPolyhedron polyhedron = getPolyhedron();
		
		outputPolygons.addOutput(polyhedron.getFaces(),false,false);
		outputSegments.addOutput(polyhedron.getSegments(),false,true);
		
	}

	
	
	
	/**
	 * @return the polyhedron
	 */
	public GeoPolyhedron getPolyhedron(){
		return outputPolyhedron.getElement(0);
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




    

	
	
	
    public void update() {
    	
        // compute output from input
        compute();
        
        //polyhedron
        getPolyhedron().update();
        

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
    
	
	
	
	

    ///////////////////////////////////////////////////////
    // FOR AlgoElementWithResizeableOutput   (TODO)
    ///////////////////////////////////////////////////////
 
	/* add in constructor, just before setting labels :
	
	        //set labels dependencies: will be used with Construction.resolveLabelDependency()
        if (labels!=null && labels.length>1)
        	for (int i=0; i<labels.length; i++){
        		cons.setLabelDependsOn(labels[i], this);
        	}
	
	 */
	
	/*
	public GeoElement addLabelToOutput(String label, int type){

		switch(type){
		case GeoElement.GEO_CLASS_POLYHEDRON:
			return outputPolyhedron.addLabel(label);
		case GeoElement.GEO_CLASS_POINT3D:
			return (GeoElement) outputPoints.addLabel(label);
		case GeoElement.GEO_CLASS_POLYGON3D:
			return outputPolygons.addLabel(label);
		case GeoElement.GEO_CLASS_SEGMENT3D:
			return outputSegments.addLabel(label);
		default:
			return null;
		}

	}
	*/
	
	protected void getOutputXML(StringBuilder sb){
		super.getOutputXML(sb);
		GeoPolyhedron polyhedron = getPolyhedron();
		
		//append XML for polygon and segments linked once more, to avoid override of specific properties		
		for (GeoPolygon polygon : polyhedron.getPolygonsLinked())
			polygon.getXML(sb);
		for (GeoSegmentND segment : polyhedron.getSegmentsLinked())
			((GeoElement) segment).getXML(sb);
		
	}
}
