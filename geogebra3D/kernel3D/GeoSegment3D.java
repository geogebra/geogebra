package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.PathMover;
import geogebra.kernel.Matrix.CoordSys;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.main.Application;

/**
 * 
 * Class for 3D segments.
 * <p>
 * See {@link GeoCoordSys1D} for 1D coord sys abilities (matrix description, path for points).
 * 
 * 
 * @author ggb3d
 *
 */
public class GeoSegment3D extends GeoCoordSys1D implements GeoSegmentND {
	

	/** if is a segment from a GeoPolygon3D or GeoPolyhedron */
	private GeoElement geoParent = null;

	/** constructor with no points
	 * @param c the construction
	 */
	public GeoSegment3D(Construction c){
		super(c);
	}
	
	/** creates a segment linking p1 to p2
	 * @param c construction
	 * @param p1 start point
	 * @param p2 end point
	 */
	public GeoSegment3D(Construction c, GeoPointND p1, GeoPointND p2){
		super(c,p1,p2);
	}
	
	/** creates a segment linking v1 to v2
	 * @param c construction
	 * @param v1 start point
	 * @param v2 end point
	 */
	private GeoSegment3D(Construction c, Coords v1, Coords v2){
		super(c,v1,v2.sub(v1));
	}
	
	
	
	/** returns segment's length 
	 * @return length
	 */
	public double getLength(){
		return getUnit();
	}
	
	
	
	
	/**
	 * return "GeoSegment3D"
	 * @return "GeoSegment3D"
	 */
	public String getClassName() {
		return "GeoSegment3D";
	}        
	
	/**
	 * return "Segment3D"
	 * @return "Segment3D"
	 */
   protected String getTypeString() {
		return "Segment3D";
	}
    
	/**
	 * return {@link GeoElement3D#GEO_CLASS_SEGMENT3D}
	 * @return {@link GeoElement3D#GEO_CLASS_SEGMENT3D}
	 */
   public int getGeoClassType() {
    	return GEO_CLASS_SEGMENT3D; 
    }

	protected GeoCoordSys1D create(Construction cons){
		return new GeoSegment3D(cons);
	}


	
	/**
	 * TODO return if this is equal to Geo
	 * @param Geo GeoElement
	 * @return if this is equal to Geo
	 */
	public boolean isEqual(GeoElement Geo) {
		return false;
	}



	
	/**
	 * TODO say if this is to be shown in algebra view
	 * @return if this is to be shown in algebra view
	 * 
	 */	
	public boolean showInAlgebraView() {
		
		return true;
	}

	/**
	 * TODO say if this is to be shown in (3D) euclidian view
	 * @return if this is to be shown in (3D) euclidian view
	 * 
	 */	
	protected boolean showInEuclidianView() {
		
		return true;
	}


	public String toValueString() {
		
		return kernel.format(getLength());
	}
	
	
	/**
	 * return the length of the segment as a string 
	 * @return the length of the segment as a string 
	 * 
	 */	
	final public String toString() {
		
		StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");  //TODO use kernel property 
		

		sbToString.append(kernel.format(getLength()));
		
		return sbToString.toString();  
	}
	
	
	
	
	public boolean isGeoSegment(){
		return true;
	}
	
	
	
	
	
	//Path3D interface
	
	/**
	 * return the 2D segment path linked to
	 * @return the 2D segment path linked to
	 */
	/*
	public Path getPath2D(){
		return (Path) getGeoElement2D();
	}
	*/

	/**
	 * {@inheritDoc}
	 */
	public GeoElement getGeoElement2D(){ 
		
		if (!hasGeoElement2D()){
			AlgoTo2D algo = new AlgoTo2D(cons, this);
			setGeoElement2D(algo.getOut());
		}
		return super.getGeoElement2D();
	}

	public void setTwoPointsCoords(Coords start, Coords end) {
		this.setCoord(start, end.sub(start));
	}
	
	public boolean isOnPath(Coords p, double eps){
		//first check global line
		if (!super.isOnPath(p, eps))
			return false;
		
		//then check position on segment
		return respectLimitedPath(p, eps);

		
	}
	
	public boolean respectLimitedPath(Coords p, double eps) {  
		
		if (Kernel.isEqual(p.getW(),0,eps))//infinite point
			return false;
		double d = p.sub(getStartInhomCoords()).dotproduct(getDirectionInD3());
		if (d<-eps)
			return false;
		double l = getLength();
		if (d>l*l+eps)
			return false;
		
		return true;   	
	} 


	public PathMover createPathMover() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getMaxParameter() {
		return 1;
	}

	public double getMinParameter() {
		return 0;
	}

	public boolean isClosedPath() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	
	/////////////////////////////////////
	// if this if from a GeoPolygon3D or a GeoPolyhedron
	
	/** sets a GeoElement as parent (GeoPolygon3D or a GeoPolyhedron)
	 * @param geo the parent
	 */
	public void setGeoParent(GeoElement geo){
		this.geoParent = geo;
	}
	
	
	/** return the parent GeoElement (GeoPolygon3D or a GeoPolyhedron)
	 * @return the parent GeoElement (GeoPolygon3D or a GeoPolyhedron)
	 */
	public GeoElement getGeoParent(){
		return this.geoParent;
	}
	
	
	
	/////////////////////////////////////
	// GeoSegmentInterface interface
	

	public double getPointX(double parameter) {
		// TODO delete from GeoSegmentND
		return 0;
	}

	public double getPointY(double parameter) {
		// TODO delete from GeoSegmentND
		return 0;
	}

	
	//TODO add to GeoSegmentND
	public Coords getPointCoords(double parameter) {
		return startPoint.getCoordsInD(3).add(
				(endPoint.getCoordsInD(3).sub(startPoint.getCoordsInD(3)))
				.mul(parameter));	
	}
	
	
	public GeoElement getStartPointAsGeoElement() {
		return (GeoElement) startPoint;
	}
	
	
	public GeoElement getEndPointAsGeoElement() {
		return (GeoElement) endPoint;
	}





	public boolean isValidCoord(double x){
		return (x>=0) && (x<=1);
	}

	
	
	
	
	
	

    private GeoElement highlightingAncestor;
    
    public void setHighlightingAncestor(GeoElement geo){
    	highlightingAncestor=geo;
    }
    
    public GeoElement getHighlightingAncestor(){
    	return highlightingAncestor;
    }

	
	final public boolean isGeoLine() {
		return true;
	}
	
	final public boolean isDefined() {
		return coordsys.getMadeCoordSys()>=0;
	}
	
	
	
	
}
