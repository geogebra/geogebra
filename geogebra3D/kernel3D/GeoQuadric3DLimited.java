package geogebra3D.kernel3D;

import java.awt.Color;

import geogebra.kernel.AlgoJoinPointsSegment;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Matrix.CoordMatrix;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.main.Application;

/**
 * Class for limited quadrics (e.g. limited cones, cylinders, ...)
 * @author mathieu
 *
 */
public class GeoQuadric3DLimited extends GeoQuadricND implements NumberValue {
	
	/** side of the quadric */
	private GeoQuadric3DPart side;
	/** bottom and top of the quadric */
	private GeoConic3D bottom, top;
	
	private GeoPointND bottomPoint, topPoint;
	
	private double min, max;
	

	/**
	 * constructor
	 * @param c
	 */
	public GeoQuadric3DLimited(Construction c) {
		this(c,null,null);
	}
	

	public GeoQuadric3DLimited(Construction c, GeoPointND bottomPoint, GeoPointND topPoint) {

		super(c,3);

		setPoints(bottomPoint, topPoint);
		
		
		//TODO merge with GeoQuadricND
		eigenvecND = new Coords[3];
		for (int i=0;i<3;i++){
			eigenvecND[i] = new Coords(4);
			eigenvecND[i].set(i+1,1);
		}
		
		//diagonal (diagonalized matrix)
		diagonal = new double[4];
		
		
		
		
	}
	
	public void setPoints(GeoPointND bottomPoint, GeoPointND topPoint){
		this.bottomPoint=bottomPoint;
		this.topPoint=topPoint;
	}
	
	
	/*
	
	public void setParts(){
				
		AlgoQuadricSide algo = new AlgoQuadricSide(cons, bottomPoint, topPoint, this);            
		cons.removeFromConstructionList(algo);
		side = (GeoQuadric3DPart) algo.getQuadric();
		
		AlgoQuadricEnds algo2 = new AlgoQuadricEnds(cons, this, bottomPoint, topPoint);
		cons.removeFromConstructionList(algo2);
		bottom = algo2.getSection1();
		top = algo2.getSection2();
	
	}
	
	*/
	
	
	public void setParts(GeoQuadric3DPart side, GeoConic3D bottom, GeoConic3D top){
		this.side=side;
		this.bottom=bottom;
		this.top=top;
	}
	
	
	public GeoQuadric3DLimited(GeoQuadric3DLimited quadric) {
		this(quadric.getConstruction());
		this.bottom = new GeoConic3D(quadric.getConstruction());
		if (quadric.top!=null)
			this.top = new GeoConic3D(quadric.getConstruction());
		this.side=new GeoQuadric3DPart(quadric.getConstruction());
		set(quadric);
	}
	
	
	
	public GeoConic3D getBottom(){
		return bottom;
	}
	
	public GeoConic3D getTop(){
		return top;
	}
	
	public GeoQuadric3DPart getSide(){
		return side;
	}
	
	public void updatePartsVisualStyle(){
		 setObjColor(getObjectColor()); 
		 setLineThickness(getLineThickness()); 
		 setAlphaValue(getAlphaValue());
		 setEuclidianVisible(isEuclidianVisible());
		 
	}
	
	

	/**
	 * inits the labels
	 * @param labels
	 */
	public void initLabels(String[] labels) {
		
    	if(cons.isSuppressLabelsActive()){ //for redefine
    		return;
    	}
		
		
    	if (labels == null || labels.length == 0) {
    		labels = new String[1];
    	}
    	
		
		setLabel(labels[0]);

		
		if (labels.length<3){
			bottom.setLabel(null);
			if (top!=null)
				top.setLabel(null);
			side.setLabel(null);
			return;
		}else if (labels.length==3){		
			bottom.setLabel(labels[1]);
			side.setLabel(labels[2]);
		}else{
			bottom.setLabel(labels[1]);
			top.setLabel(labels[2]);
			side.setLabel(labels[3]);	
		}

	}
	
	
	public double getMin(){
		return min;
	}
	
	public double getMax(){
		return max;
	}
	
	
	//TODO merge in GeoQuadricND
	/**
	 * @param origin 
	 * @param direction 
	 * @param r 
	 * @param min 
	 * @param max 
	 * 
	 */
	public void setCylinder(Coords origin, Coords direction, double r, double min, double max){

		//limites
		this.min=min;
		this.max=max;
		
		// set center
		setMidpoint(origin.get());
		
		// set direction
		eigenvecND[2] = direction;
		

		// set others eigen vecs
		Coords[] ee = direction.completeOrthonormal();
		eigenvecND[0] = ee[0];
		eigenvecND[1] = ee[1];
		
		// set halfAxes = radius	
		for (int i=0;i<2;i++)
			halfAxes[i] = r;
		
		
		
		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = 0;
		diagonal[3] = -r*r;
		
		// set matrix
		setMatrixFromEigen();
		
		//set type
		setType(QUADRIC_CYLINDER);
		
	}
	
	

	public void setCone(Coords origin, Coords direction, double r, double min, double max){
		
		//limites
		this.min=min;
		this.max=max;
		
		// set center
		setMidpoint(origin.get());
		
		// set direction
		eigenvecND[2] = direction;
		
		// set others eigen vecs
		Coords[] ee = direction.completeOrthonormal();
		eigenvecND[0] = ee[0];
		eigenvecND[1] = ee[1];
		
		// set halfAxes = radius	
		for (int i=0;i<2;i++)
			halfAxes[i] = r;
		
		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = -r*r;
		diagonal[3] = 0;
		
		// set matrix
		setMatrixFromEigen();
		
			
		// set type
		type = QUADRIC_CONE;
	}
	
	
	
	public void set(Coords origin, Coords direction, double r, double min, double max){

		switch(type){
		case QUADRIC_CYLINDER:
			setCylinder(origin, direction, r, min, max);
			break;
		case QUADRIC_CONE:
			setCone(origin, direction, r, min, max);
			break;
		}
	}
	
	
	
	/////////////////////////
	// GEOELEMENT
	/////////////////////////
	

	public void setObjColor(Color color) {
		super.setObjColor(color);
		if (bottom==null)
			return;
		bottom.setObjColor(color);
		if (top!=null)
			top.setObjColor(color);
		side.setObjColor(color);

	}



	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
	}
	


	public void setEuclidianVisible(boolean visible) {

		super.setEuclidianVisible(visible);
		bottom.setEuclidianVisible(visible);
		if (top!=null)
			top.setEuclidianVisible(visible);
		side.setEuclidianVisible(visible);


	}  


	public void setLineType(int type) {
		super.setLineType(type);


		if (bottom==null)
			return;

		bottom.setLineType(type);
		if (top!=null)
			top.setLineType(type);

	}


	public void setLineTypeHidden(int type) {
		super.setLineTypeHidden(type);


		if (bottom==null)
			return;

		bottom.setLineTypeHidden(type);
		if (top!=null)
			top.setLineTypeHidden(type);
	}


	public void setLineThickness(int th) {
		super.setLineThickness(th);
		if (bottom==null)
			return;
		bottom.setLineThickness(th);
		if (top!=null)
			top.setLineThickness(th);	
	}


	public void setAlphaValue(float alpha) {

		super.setAlphaValue(alpha);


		if (bottom==null)
			return;

		bottom.setAlphaValue(alpha);
		bottom.updateVisualStyle();
		if (top!=null){
			top.setAlphaValue(alpha);
			top.updateVisualStyle();
		}
		side.setAlphaValue(alpha);
		side.updateVisualStyle();



	}


	public GeoElement copy() {
		return new GeoQuadric3DLimited(this);
	}


	public int getGeoClassType() {
		return GEO_CLASS_QUADRIC_LIMITED;
	}

	protected String getTypeString() {
		return side.getTypeString();
	}


	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	public void set(GeoElement geo) {
		
		
		if (geo instanceof GeoQuadric3DLimited){
			GeoQuadric3DLimited quadric = (GeoQuadric3DLimited) geo;

			min=quadric.min;
			max=quadric.max;
			volume=quadric.volume;
			
			bottom.set(quadric.bottom);
			if (quadric.top!=null)
				top.set(quadric.top);
			side.set(quadric.side);
			
			//TODO merge with GeoQuadric3D
			// copy everything
			toStringMode = quadric.toStringMode;
			type = quadric.type;
			for (int i = 0; i < 10; i++)
				matrix[i] = quadric.matrix[i]; // flat matrix A   
			
			for (int i=0; i<3; i++){
				eigenvecND[i].set(quadric.eigenvecND[i]);
				halfAxes[i] = quadric.halfAxes[i];
			}

			setMidpoint(quadric.getMidpoint().get());
			


			defined = quadric.defined;	
		
		}
		
	}

	public boolean showInAlgebraView() {
		return true;
	}

	protected boolean showInEuclidianView() {
		return true;
	}


	public String getClassName() {
		return "GeoQuadricLimited";
	}


	/////////////////////////////////////
	// GEOQUADRICND
	/////////////////////////////////////
	
	
	private double volume;
	
	public void calcVolume(){
		
		//Application.debug("ici");
		if (bottom==null){
			volume=Double.NaN;
			return;
		}
		
		switch(type){
		case QUADRIC_CYLINDER:
			volume=getHalfAxis(0)*getHalfAxis(0)*Math.PI*(max-min);
			break;
		case QUADRIC_CONE:
			volume=getHalfAxis(0)*getHalfAxis(0)*Math.PI*(max-min)/3;
			break;
		//default:
		//	volume=Double.NaN;
		}
	}
	
	public double getVolume(){
		if (defined)
			return volume;				        
		else 
			return Double.NaN;			        	
	}	

	
	public String toValueString() {
		switch(type){
		case QUADRIC_CYLINDER:
		case QUADRIC_CONE:
			return kernel.format(volume);
		case QUADRIC_EMPTY:
			return kernel.format(0);
		}
		
		return "todo-GeoQuadric3DLimited";
		
	}
	
	protected StringBuilder buildValueString() {
		return new StringBuilder(toValueString());
	}




	@Override
	public void setSphereND(GeoPointND M, GeoSegmentND segment) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setSphereND(GeoPointND M, GeoPointND P) {
		// TODO Auto-generated method stub
		
	}
	
	
	//////////////////////////////////
	// NumberValue
	//////////////////////////////////


	public MyDouble getNumber() {
		return new MyDouble(kernel,  getDouble() );
	}


	public double getDouble() {		
		return getVolume();
	}
	
	public boolean isNumberValue() {
		return true;
	}




}
