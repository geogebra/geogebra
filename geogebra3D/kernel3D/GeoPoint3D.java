/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoPoint.java
 *
 * The point (x,y) has homogenous coordinates (x,y,1)
 *
 * Created on 30. August 2001, 17:39
 */

package geogebra3D.kernel3D;

import java.awt.geom.Point2D;
import java.util.TreeSet;

import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.kernel.AlgoDynamicCoordinates;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.LocateableList;
import geogebra.kernel.Path;
import geogebra.kernel.PathParameter;
import geogebra.kernel.PointProperties;
import geogebra.kernel.Region;
import geogebra.kernel.RegionParameters;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.Region3D;
import geogebra.main.Application;
import geogebra.util.Util;
import geogebra3D.Application3D;


/**
 *
 * @author  Markus + ggb3D
 * @version 
 */
public class GeoPoint3D extends GeoVec4D
implements GeoPointND, PointProperties, Vector3DValue{   	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private boolean isInfinite,isDefined;
	public int pointSize = EuclidianView.DEFAULT_POINT_SIZE; 
	
	
	//mouse moving
	private Coords willingCoords = null; //= new Ggb3DVector( new double[] {0,0,0,1.0});
	private Coords willingDirection = null; //new Ggb3DVector( new double[] {0,0,1,0.0});
	
	//paths
	private Path path;
	private PathParameter pp;
	
	//region
	private Region region;
	private RegionParameters regionParameters;
	/** 2D coord sys when point is on a region */
	//private GeoCoordSys2D coordSys2D = null;
	/** 2D x-coord when point is on a region */
	private double x2D = 0;
	/** 2D y-coord when point is on a region */
	private double y2D = 0;
	/** 2D z-coord when point is on a region (distance)*/
	private double z2D = 0;
        
    // temp
    public Coords inhom = new Coords(3);


    // list of Locateables (GeoElements) that this point is start point of
    // if this point is removed, the Locateables have to be notified
    private LocateableList locateableList;         

    
    public GeoPoint3D(Construction c) { 
    	super(c,4); 
    	setUndefined(); 
    }
  
    /**
     * Creates new GeoPoint 
     */  
    public GeoPoint3D(Construction c, String label, double x, double y, double z, double w) {               
        super(c, x, y, z, w); // GeoVec4D constructor  
        setLabel(label);
        
    }
    
    public GeoPoint3D(Construction c, String label, Coords v){
    	this(c,label,v.get(1),v.get(2),v.get(3),v.get(4));
    }
    
    
    
    
    public GeoPoint3D(Construction c, Path path) {
		super(c,4);
		setPath(path);
	}
    
    @Override
	public void setVisualStyle(GeoElement geo){
    	super.setVisualStyle(geo);
		if (geo instanceof PointProperties) {
			setPointSize(((PointProperties)geo).getPointSize());
			setPointStyle(((PointProperties)geo).getPointStyle());
		}
	}
    
    public void setPath(Path path){
    	this.path = path;
    }
    
    
    public GeoPoint3D(Construction c, Region region) {
		super(c,4);
		setRegion(region);
	}
    
    public void setRegion(Region region){
    	this.region = region;

		
    }
    

    

    ///////////////////////////////////////////////////////////
    // GeoPointND interface (TODO move it to abstract method)
    
    public double distance(GeoPointND P){
    	//TODO dimension ?
    	return getInhomCoordsInD(3).distance(P.getInhomCoordsInD(3));
    }
    
    // euclidian distance between this GeoPoint3D and P
    final public double distance(GeoPoint3D P) {       
        return getInhomCoords().distance(P.getInhomCoords());
    }            

   
    
    ///////////////////////////////////////////////////////////
    // COORDINATES
    
    
    public double getX(){
    	return getCoords().get(1);
    }
    public double getY(){
    	return getCoords().get(2);
    }
    public double getZ(){
    	return getCoords().get(3);
    }
    
    
    

    
	/** Sets homogenous coordinates and updates
	 * inhomogenous coordinates
	 * @param v coords
	 * @param doPathOrRegion says if path (or region) calculations have to be done
	 */    
	final public void setCoords(Coords v, boolean doPathOrRegion) {
		
		super.setCoords(v);
		
		updateCoords(); 
		
		if (doPathOrRegion){
			
			// region
			if (hasRegion()){
				//Application.printStacktrace(getLabel());
				
				region.pointChangedForRegion(this);
			}
			
			// path
			if (hasPath()) {
				// remember path parameter for undefined case
				//PathParameter tempPathParameter = getTempPathparameter();
				//tempPathParameter.set(getPathParameter());
				path.pointChanged(this);

			}
			updateCoords(); 
		}
		
	}  
	

	
	
	
	
	
	final public void setCoords(Coords v) {
		setCoords(v,true);
	}
	
	
	final public void setCoords(double x, double y, double z, double w) {
		
		setWillingCoords(null);
		setCoords(new Coords(x,y,z,w));
		
	}  	

	
	//sets from 2D coords
	final public void setCoords(double x, double y, double z){
		setCoords(x,y,0,z);
	}
	
	
	
	
	
	
	final public void updateCoords() {
		
		
		//Application.printStacktrace(getLabel());
		
		
		// infinite point
		if (kernel.isZero(v.get(4))) {
			//Application.debug("infinite");
			isInfinite = true;
			isDefined = !(Double.isNaN(v.get(1)) || Double.isNaN(v.get(2)) || Double.isNaN(v.get(3)));
			inhom.set(Double.NaN);
		} 
		// finite point
		else {
			isInfinite = false;
			isDefined = v.isDefined();
		
			if (isDefined) {
				// make sure the z coordinate is always positive
				// this is important for the orientation of a line or ray
				// computed using two points P, Q with cross(P, Q)
				//TODO cast in GgbVector				
				if (v.get(4) < 0) {
					for(int i=1;i<=4;i++)
						v.set(i,(v.get(i))*(-1.0));
				} 
				
				
				// update inhomogenous coords
				if (v.get(4) == 1.0) {
					inhom.set(1,v.get(1));
					inhom.set(2,v.get(2));
					inhom.set(3,v.get(3));
			    } else {        
					inhom.set(1,v.get(1)/v.get(4));
					inhom.set(2,v.get(2)/v.get(4));
					inhom.set(3,v.get(3)/v.get(4));
			    }
			} else {
				inhom.set(Double.NaN);
			}
		}
		
		//Application.debug("v=\n"+v+"\ninhom="+inhom);
		
		//sets the drawing matrix to coords
		getDrawingMatrix().setOrigin(getCoords());

	}
	 
	final public void setCoords(GeoVec3D v) {
		setCoords(v.x, v.y, v.z, 1.0);
	}  
          
    /** 
     * Returns (x/w, y/w, z/w) GgbVector.
     */
    final public Coords getInhomCoords() {
    	return inhom.copyVector();
    }   

    public Coords getInhomCoordsInD(int dimension){
    	Coords v;
    	switch(dimension){
    	case 3:
    		v = new Coords(4);
    		v.setX(inhom.getX());
    		v.setY(inhom.getY());
    		v.setZ(inhom.getZ());
    		v.setW(1);
    		return v;
    	case 2:
    		v = new Coords(2);
    		v.setX(inhom.getX());
    		v.setY(inhom.getY());
    		return v;
    	default:
    		return null;
    	}
    }
    
    
    public Coords getCoordsInD2(CoordSys coordSys){
    	   	
    	Coords coords;
		Coords[] project;
		
		if (getWillingCoords()!=null) //use willing coords
			coords = getWillingCoords();
		else //use real coords
			coords = getCoords();
		
		CoordMatrix4x4 matrix; //matrix for projection

		if (coordSys==null){ //project on plane xOy
			matrix = CoordMatrix4x4.Identity();
		}else{
			matrix = coordSys.getMatrixOrthonormal();
		}
		
		if (getWillingDirection()==null) //use normal direction for projection
			project = coords.projectPlane(matrix);
		else //use willing direction for projection
			project = coords.projectPlaneThruVIfPossible(matrix,getWillingDirection());		

		Coords v = new Coords(3);
		v.setX(project[1].getX());
		v.setY(project[1].getY());
		v.setZ(project[1].getW());
		return v;
    	
    }
    
    public Coords getCoordsInD(int dimension){
    	switch(dimension){
    	case 3:
    		return getCoords();
    	case 2:
    		//Application.debug("willingCoords=\n"+willingCoords+"\nwillingDirection=\n"+willingDirection);
    		/*
    		GgbVector coords;
    		if (getWillingCoords()!=null)
    			if (getWillingDirection()!=null){
    				//TODO use region matrix in place of identity
    				coords=getWillingCoords().projectPlaneThruV(GgbMatrix4x4.Identity(), getWillingDirection())[1];
    			}else
    				coords=getWillingCoords().projectPlane(GgbMatrix4x4.Identity())[1];
    		else
    			coords=getCoords();
    		GgbVector v = new GgbVector(3);
    		v.setX(coords.getX());
    		v.setY(coords.getY());
       		v.setZ(coords.getW());
       		return v;
       		*/
    		return getCoordsInD2(CoordSys.Identity3D());
    	default:
    		return null;
    	}
    }
   
    
    /** 
     * Returns (x/w, y/w, z/w) GgbVector.
     */
    final public void getInhomCoords(double[] d) {
    	double[] coords = getInhomCoords().get();
    	for (int i=0; i<d.length; i++)
    		d[i]=coords[i];
    }   
    
    
    final public double[] vectorTo(GeoPointND QI){
    	GeoPoint3D Q = (GeoPoint3D) QI;
    	//Application.debug("v=\n"+Q.getCoords().sub(getCoords()).get());
    	return Q.getCoords().sub(getCoords()).get();
    }
        
  
    
    
    
    
    
    

    
   
    
    
    
    
    
    
    
    
    

	protected boolean movePoint(Coords rwTransVec, Coords endPosition) {
	
		boolean movedGeo = false;
		
		if (endPosition != null) {					
			//setCoords(endPosition.x, endPosition.y, 1);
			//movedGeo = true;
		} 
		
		// translate point
		else {	
			
								
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			/*
			if (Math.abs(rwTransVec.getX()) > Kernel.MIN_PRECISION)
				x  = kernel.checkDecimalFraction(x);
			if (Math.abs(rwTransVec.getY()) > Kernel.MIN_PRECISION) 
				y = kernel.checkDecimalFraction(y);
				*/
				
			// set translated point coords
			if (hasPath()){
				double t=
					getPathParameter().getT()
					+rwTransVec.getX()
					+rwTransVec.getY()
					+rwTransVec.getZ()
					;
				//TODO use path unit and direction
				getPathParameter().setT(t);
				getPath().pathChanged(this);
				getPath().pointChanged(this);
				//getParentAlgorithm().update();
			}else if (hasRegion()){
				/* TODO make this work :)
				double x = getRegionParameters().getT1() +rwTransVec.getX();
				double y = getRegionParameters().getT2() +rwTransVec.getY();
				getRegionParameters().setT1(x);getRegionParameters().setT2(y);
				Application.debug("(x,y)="+x+","+y);
				//TODO use path unit and direction
				getParentAlgorithm().update();
				*/
			}else{
				Coords coords = (Coords) getInhomCoords().add(rwTransVec);
				setCoords(coords);	
			}
			
			movedGeo = true;
		}
		
		
		
		return movedGeo;
	
	}
    
    
    
    
    
    ///////////////////////////////////////////////////////////
    // PATHS
    
	public boolean hasPath() {
		return path != null;
	}
	
	final public boolean isPointOnPath() {
		return path != null;
	}
    
    
	public Path getPath() {
		return path;
	}
	

	
	
    final public PathParameter getPathParameter() {
    	if (pp == null)
    		pp = new PathParameter();
    	return pp;
    }	
	
  
	final public void doPath(){
		path.pointChanged(this);
		//check if the path is a 2D path : in this case, 2D coords have been modified
		if (!((GeoElement) path).isGeoElement3D())
			updateCoordsFrom2D(false,null);
		updateCoords(); 
	}
	


	//copied on GeoPoint
	public boolean isChangeable() {
		return !isFixed() && (isIndependent() || isPointOnPath() || isPointInRegion()); 
	}	
	
    ///////////////////////////////////////////////////////////
    // REGION
 	
	/** says if the point is in a Region
	 * @return true if the point is in a Region
	 */
	final public boolean hasRegion() {
		return region != null;
	}	
	
	final public boolean isPointInRegion() {
		return region != null;
	}
	

	final public void doRegion(){
		region.pointChangedForRegion(this);
		
		updateCoords(); 
	}
	
    final public RegionParameters getRegionParameters() {
    	if (regionParameters == null)
    		regionParameters = new RegionParameters();
    	return regionParameters;
    }
    

    
    final public Region getRegion(){
    	return region;
    }
	
    
    /** set the 2D coord sys where the region lies
     * @param cs 2D coord sys
     */
    /*
    public void setCoordSys2D(GeoCoordSys2D cs){
    	this.coordSys2D = cs;
    }
    */
    
    
    /**
     * update the 2D coords on the region (regarding willing coords and direction)
     */
    public void updateCoords2D(){
    	if (region!=null){ //use region 2D coord sys
    		
    		updateCoords2D(region, true);
    		
    	}else{//project on xOy plane
    		x2D = getX();
    		y2D = getY();
    		z2D = getZ();
    	}
    	
    			
    }
    
    
    /**
     * update the 2D coords on the region (regarding willing coords and direction)
     */
    public void updateCoords2D(Region region, boolean updateParameters){
    	
    	Coords coords;
		Coords[] project;
		
		if (getWillingCoords()!=null) //use willing coords
			coords = getWillingCoords();
		else //use real coords
			coords = getCoords();

		if (getWillingDirection()==null){ //use normal direction for projection
			project = ((Region3D) region).getNormalProjection(coords);
			//coords.projectPlane(coordSys2D.getMatrix4x4());
		}else{ //use willing direction for projection
			project = ((Region3D) region).getProjection(getCoords(),coords,getWillingDirection());
			//project = coords.projectPlaneThruV(coordSys2D.getMatrix4x4(),getWillingDirection());
		}
			
		x2D = project[1].get(1);
		y2D = project[1].get(2);
		z2D = project[1].get(3);

		if (updateParameters){
			RegionParameters rp = getRegionParameters();
			rp.setT1(project[1].get(1));rp.setT2(project[1].get(2));
			rp.setNormal(((GeoElement) region).getMainDirection());
		}
 
    }
    
    
    /** set 2D coords
     * @param x x-coord
     * @param y y-coord
     */
    public void setCoords2D(double x, double y, double z){
    	x2D = x/z;
    	y2D = y/z;
    }
    
	public double getX2D(){
		return x2D;
	}
	
	public double getY2D(){
		return y2D;
	}
	
	public double getZ2D(){
		return z2D;
	}
	
	
	public int getMode() { 
		return Kernel.COORD_CARTESIAN; //TODO other modes
	}
	
	
	/**
	 * update 3D coords regarding 2D coords 
	 * (if coordsys!=null, use it; else if region!=null, use its coord sys; else project on xOy plane)
	 * @param doPathOrRegion says if the path or the region calculations have to be done
	 */
	public void updateCoordsFrom2D(boolean doPathOrRegion, CoordSys coordsys){
		if (coordsys!=null)
			setCoords(coordsys.getPoint(getX2D(), getY2D()), doPathOrRegion);
		else if (region!=null){
			/*
			if (getLabel().contains("B1")){
				Application.debug(getX2D()+","+getY2D());
				if (getX2D()>3)
					Application.printStacktrace("ici");
			}
			*/
			setCoords(((Region3D) region).getPoint(getX2D(), getY2D()), doPathOrRegion);
		}else 
			setCoords(new Coords(getX2D(), getY2D(), 0, 1), doPathOrRegion);
	}
	
	
    ///////////////////////////////////////////////////////////
    // WILLING COORDS
	
	public void setWillingCoords(Coords willingCoords){
		this.willingCoords = willingCoords;
	}
	
	public void setWillingCoords(double x, double y, double z, double w){
		setWillingCoords(new Coords(new double[] {x,y,z,w}));
	}	
	
	public void setWillingDirection(Coords willingDirection){
		this.willingDirection = willingDirection;
	}
	
	public Coords getWillingCoords(){
		return willingCoords;
	}
	
	public Coords getWillingDirection(){
		return willingDirection;
	}
	
 
    
    ///////////////////////////////////////////////////////////
    // FREE UP THE POINT
	
	/**
	 * free up the point from is region (TODO path, other algorithms)
	 */
	public void freeUp(){
		if (hasRegion()){
			//remove the parent algorithm
			//Application.debug("algo : "+getParentAlgorithm().toString());
			AlgoElement parent = getParentAlgorithm();
			int index = parent.getConstructionIndex();
			getRegion().toGeoElement().removeAlgorithm(parent);
			getConstruction().removeFromAlgorithmList(parent);			
			setParentAlgorithm(null);
			getConstruction().removeFromConstructionList(parent);
			getConstruction().addToConstructionList(this, index);
			
			//remove the region
			setRegion(null);
			//change the color
			if (getObjectColor().equals(ConstructionDefaults.colRegionPoint))
				setObjColor(ConstructionDefaults.colPoint);
			// move from Dependent to Independent in AlgebraView
			if (app.useFullGui())
				((AlgebraView)(app.getGuiManager().getAlgebraView())).rename((GeoElement)this);
		}
	
	}
 
	
	
    ///////////////////////////////////////////////////////////
    // COMMON STUFF
   
    
	public String getClassName() {
		return "GeoPoint3D";
	}        
	
    protected String getTypeString() {
		return "Point3D";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_POINT3D;  
    }
    
    public GeoPoint3D(GeoPointND point) {
    	super(((GeoElement) point).getConstruction());
        set((GeoElement) point);        
    }
    

    
    
    public GeoPoint3D copy() {
        return new GeoPoint3D(this);        
    }                 
       
	
	final public boolean isGeoPoint() {
		return true;
	}


	public boolean isDefined() {

		return isDefined;
	}
	
	/*
	public void set(GeoPointND P){
		set((GeoElement) P);
	}
	*/
	public void set(GeoElement geo) {

    	if (geo.isGeoPoint()) {
	    	GeoPointND p = (GeoPointND) geo;  
	    	if (p.getPathParameter() != null) {
	    		PathParameter pathParameter = getPathParameter();
		    	pathParameter.set(p.getPathParameter());
	    	}
	    	setCoords(p);   
	    	//TODO ? moveMode = p.getMoveMode();
	    	updateCoords();
	    	//TODO setMode(p.toStringMode); // complex etc
    	}
    	/* TODO
    	else if (geo.isGeoVector()) {
    		GeoVector v = (GeoVector) geo; 
    		setCoords(v.x, v.y, 1d);   
	    	setMode(v.toStringMode); // complex etc
    	}
    	*/
		
	}
	
	
	public void setUndefined() {
		setCoords(new Coords(Double.NaN, Double.NaN, Double.NaN, Double.NaN),false);
		setWillingCoords(null);
		isDefined = false;
		
	}
	

	public boolean showInEuclidianView() {
		return isDefined;
	}
	
	
	final public String toString() {
		
		StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = "); 
		
		sbToString.append(toValueString());
		
		return sbToString.toString();  
	}

	
	

	public boolean hasValueStringChangeableRegardingView(){
		return true;
	}
	
	
	
	public String toValueString() {
    	if (isInfinite()) 
			return app.getPlain("undefined");
    	
    	
    	StringBuilder sbToString = getSbBuildValueString();
    	
    	boolean isVisibleInView2D = false;
    	Coords p = getInhomCoordsInD(3);
    	
    	if (getViewForValueString() instanceof EuclidianView){
    		Coords p2D = ((EuclidianView) getViewForValueString()).getCoordsForView(getInhomCoordsInD(3));
    		if (Kernel.isZero(p2D.getZ())){
    	    	isVisibleInView2D=true;
    	    	p = p2D;
    	    }else
    	    	return app.getPlain("NotIncluded");
    	}
    	
		sbToString.setLength(0);
		sbToString.append("(");
		sbToString.append(kernel.format(p.getX())); 
		sbToString.append(", ");
		sbToString.append(kernel.format(p.getY()));
		if (!isVisibleInView2D){
			sbToString.append(", ");
			sbToString.append(kernel.format(p.getZ()));
		}	
		sbToString.append(")");
		
		//TODO use point property
		return sbToString.toString(); 
	}




	public boolean isEqual(GeoElement geo) {
		
if (!geo.isGeoPoint()) return false;
    	
    	GeoPointND P = (GeoPointND)geo;
    	
        if (!(isDefined() && P.isDefined())) return false;   
        
        // both finite      
        if (isFinite() && P.isFinite()){
        	Coords c1 = getInhomCoords();
        	Coords c2 = P.getInhomCoordsInD(3);
        	return Kernel.isEqual(c1.getX(), c2.getX()) && 
        		Kernel.isEqual(c1.getY(), c2.getY()) &&
        		Kernel.isEqual(c1.getZ(), c2.getZ());
        }else if (isInfinite() && P.isInfinite()){
        	Coords c1 = getCoords();
        	Coords c2 = P.getCoordsInD(3);
			return c1.crossProduct(c2).equalsForKernel(0, Kernel.STANDARD_PRECISION);      
        }else 
        	return false;    
		
        
	}

	
	/**
	 * Returns whether this point has three changeable numbers as coordinates, 
	 * e.g. point A = (a, b, c) where a, b and c are free GeoNumeric objects.
	 */	
	public boolean hasChangeableCoordParentNumbers() {
		return false;
	}
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////
	// PointProperties
	
	

	public int getPointSize() {
		return pointSize;
	}

	public int getPointStyle() {
		//TODO
		return 0;
	}
	
	public boolean getTrace(){
		return false; //TODO
	}

	public void setPointSize(int size) {
		pointSize = size;		
	}

	public void setPointStyle(int type) {
		// TODO 
		
	}
	
	
	//////////////////////////////////
	// XML
	
    /**
     * returns all class-specific xml tags for saveXML
     * GeoGebra File Format
     */
	protected void getXMLtags(StringBuilder sb) {
        super.getXMLtags(sb); 
        
        
		// point size
		sb.append("\t<pointSize val=\"");
			sb.append(pointSize);
		sb.append("\"/>\n");
 
    }
	
	
	
    public String getStartPointXML() {
    	StringBuilder sb = new StringBuilder();    	
		sb.append("\t<startPoint ");
		
    	if (isAbsoluteStartPoint()) {		
			sb.append(" x=\"" + getCoords().get(1) + "\"");
			sb.append(" y=\"" + getCoords().get(2) + "\"");
			sb.append(" z=\"" + getCoords().get(3) + "\"");			
			sb.append(" w=\"" + getCoords().get(4) + "\"");			
    	} else {
			sb.append("exp=\"");
			boolean oldValue = kernel.isPrintLocalizedCommandNames();
			kernel.setPrintLocalizedCommandNames(false);
			sb.append(Util.encodeXML(getLabel()));
			kernel.setPrintLocalizedCommandNames(oldValue);
			sb.append("\"");			    	
    	}
		sb.append("/>\n");
		return sb.toString();
    }
    
	final public boolean isAbsoluteStartPoint() {
		return isIndependent() && !isLabelSet();
	}
 

	

	
	//////////////////////////////////
	// LocateableList
	
	
	public LocateableList getLocateableList(){
		if (locateableList == null)
			locateableList = new LocateableList(this);
		return locateableList;
	}
	
	/**
	 * Tells Locateables that their start point is removed
	 * and calls super.remove()
	 */
	public void doRemove() {
		if (locateableList != null) {
			
			locateableList.doRemove();

		}
		

		
		super.doRemove();
	}
	
	
	/**
	 * Calls super.update() and updateCascade() for all registered locateables.	 
	 */
	public void update() {  	
		super.update();
						
		// update all registered locatables (they have this point as start point)
		if (locateableList != null) {	
			GeoElement.updateCascade(locateableList, getTempSet(), false);
		}			
	}
	
	private static TreeSet tempSet;	
	protected static TreeSet getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet();
		}
		return tempSet;
	}
	
	
	//////////////////////////////////
	// GeoPointInterface interface
	
	
	public boolean isFinite(){
		return isDefined && !isInfinite;
	}
	
	public boolean isInfinite() {
		return isInfinite;
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return true;
	}



	public double[] getPointAsDouble() {
		return getInhomCoords().get();
	};
    
    
	
	public boolean getSpreadsheetTrace() {
		return false;
	}

	public Geo3DVec get3DVec() {
		return new Geo3DVec(kernel, getX(), getY(), getZ());
	}
    
	
	//////////////////////////////////
	// display in a 2D view ?
	
	/*
	public boolean isVisibleInView(Object view){
		if (view==((Application3D) app).getEuclidianView3D())
			return true;
		
		if (view==((Application3D) app).getEuclidianView())
			return Kernel.isZero(getCoords().getZ());
		
		return false;
		
	}
    */
	
	//////////////////////////////////
	// GeoElement3DInterface interface

	public Coords getLabelPosition(){
		//Application.debug(inhom.toString());
		return getCoords();
	}
	
	
	

	/////////////////////////////////////////
	// MOVING THE POINT (3D)
	/////////////////////////////////////////
	
	protected int moveMode = MOVE_MODE_XY;

	public void switchMoveMode(){
		

		switch (moveMode){
		case MOVE_MODE_XY:
			moveMode=MOVE_MODE_Z;
			break;
		case MOVE_MODE_Z:
			moveMode=MOVE_MODE_XY;
			break;			
		}
	}
	
	public void setMoveMode(int flag){
		moveMode = flag;
	}
	

	public int getMoveMode(){
		if (!isIndependent() || isFixed())
			return MOVE_MODE_NONE;
		else if (hasPath())
			return MOVE_MODE_NONE; // too complicated to use MOVE_MODE_Z when not lines
		else if (hasRegion())
			return MOVE_MODE_XY;
		else
			return moveMode;
	}

	private Coords moveNormalDirection;
	
	/**
	 * sets the normal to moving directions (for region points)
	 * @param d
	 */
	public void setMoveNormalDirection(Coords d){
		moveNormalDirection = d.copyVector();
	}
	
	/**
	 * 
	 * @return the normal to moving directions (for region points)
	 */
	public Coords getMoveNormalDirection(){
		return moveNormalDirection;
	}
   
	
	
	
	
	
	private boolean showUndefinedInAlgebraView = true;
	
	public void showUndefinedInAlgebraView(boolean flag) {
		showUndefinedInAlgebraView = flag;
	}
	
    public final boolean showInAlgebraView() {
        return (isDefined || showUndefinedInAlgebraView);
    }

	public void set(GeoPointND p) {
		// TODO ambiguous with set(GeoElement geo)
		this.set((GeoElement)p);
	}  

}
