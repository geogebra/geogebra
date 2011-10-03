package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;
import geogebra.kernel.ConstructionElementCycle;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Path;
import geogebra.kernel.PathMover;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.main.Application;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author ggb3D
 * 
 * Class describing a GeoPolyhedron
 *
 */
public class GeoPolyhedron extends GeoElement3D {//implements Path {

	
	public static final int TYPE_NONE = 0;
	public static final int TYPE_PYRAMID = 1;
	public static final int TYPE_PSEUDO_PRISM = 2;
	public static final int TYPE_PRISM = 3;
	
	int type;
	
	
	/** vertices */
	//protected ArrayList<GeoPoint3D> points;
	
	
	/** edges index */
	protected TreeMap<ConstructionElementCycle,Long> segmentsIndex;
	
	/** max faces edges */
	protected long segmentsIndexMax = 0;

	/** edges */
	protected TreeMap<Long, GeoSegment3D> segments;
	
	/** edges linked (e.g basis of the prism) */
	protected TreeMap<ConstructionElementCycle,GeoSegmentND> segmentsLinked;
	
	
	
	/** faces index */
	protected TreeMap<ConstructionElementCycle,Long> polygonsIndex;
	
	/** max faces index */
	protected long polygonsIndexMax = 0;
	
	/** faces */
	protected TreeMap<Long,GeoPolygon3D> polygons;
	
	/** faces linked */
	protected TreeSet<GeoPolygon> polygonsLinked;
	
	/** points created by the algo */
	protected ArrayList<GeoPoint3D> pointsCreated;

	
	
	/** segments to remove for update */
	protected TreeSet<ConstructionElementCycle> oldSegments;
	
	/** polygons to remove for update */
	protected TreeSet<ConstructionElementCycle> oldPolygons;
	
	
	
	
	/** face currently constructed */
	private ConstructionElementCycle currentFace;
	
	
	

	

	
	
	/** constructor 
	 * @param c construction
	 */
	public GeoPolyhedron(Construction c) {
		super(c);
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings
		
		polygonsIndex = new TreeMap<ConstructionElementCycle,Long>();
		polygons = new TreeMap<Long, GeoPolygon3D>();
		
		segmentsIndex = new TreeMap<ConstructionElementCycle,Long>();
		segments = new TreeMap<Long, GeoSegment3D>();
		
		oldPolygons = new TreeSet<ConstructionElementCycle>();		
		oldSegments = new TreeSet<ConstructionElementCycle>();	
		
		segmentsLinked = new TreeMap<ConstructionElementCycle,GeoSegmentND>();
		polygonsLinked = new TreeSet<GeoPolygon>();
		
		pointsCreated = new ArrayList<GeoPoint3D>();
	}
	
	/**
	 * 
	 * @return segments linked to the polyhedron (eg segments of the bottom)
	 */
	public Collection<GeoSegmentND> getSegmentsLinked(){
		return segmentsLinked.values();
	}
	
	/**
	 * 
	 * @return polygons linked to the polyhedron (eg the bottom)
	 */
	public Collection<GeoPolygon> getPolygonsLinked(){
		return polygonsLinked;
	}
	
	
	/**
	 * 
	 * @param polyhedron
	 */
	public GeoPolyhedron(GeoPolyhedron polyhedron) {
		this(polyhedron.getConstruction());
		set(polyhedron);
	}
	
	
	/**
	 * set the type of polyhedron
	 * @param type
	 */
	public void setType(int type){
		this.type=type;
	}
	
	/**
	 * restart faces descriptions
	 */
	public void restartFaces(){
		
		oldPolygons.clear();
		for (ConstructionElementCycle key : polygonsIndex.keySet()){
			//Application.debug("key : "+key);
			oldPolygons.add(key);
		}
		
		oldSegments.clear();
		for (ConstructionElementCycle key : segmentsIndex.keySet())
			oldSegments.add(key);
		
	}
	
	
	/**
	 * start a new face
	 */
	public void startNewFace(){
		currentFace = new ConstructionElementCycle();
	}
	
	
	/** add the point to the current face
	 * and to the point list if it's a new one
	 * @param point
	 */
	public void addPointToCurrentFace(GeoPointND point){

		currentFace.add((GeoElement) point);
	}
	
	
	/**
	 * ends the current face and store it in the faces list
	 */
	public void endCurrentFace(){
		currentFace.setDirection();
		
		//if the old polygons contains the current face, then this won't be removed nor recreated
		if (oldPolygons.contains(currentFace)){
			oldPolygons.remove(currentFace);
			//update old segments
			Iterator<ConstructionElement> it = currentFace.iterator();
			GeoPoint3D endPoint = (GeoPoint3D) it.next();
			GeoPoint3D firstPoint = endPoint;
			for (; it.hasNext();){
				GeoPoint3D startPoint = endPoint;
				endPoint = (GeoPoint3D) it.next();
				oldSegments.remove(
						ConstructionElementCycle.SegmentDescription(startPoint, endPoint));
			}
			//last segment
			oldSegments.remove(
					ConstructionElementCycle.SegmentDescription(endPoint, firstPoint));	
		}else{
			//faces.add(currentFace);
			polygonsIndex.put(currentFace, new Long(polygonsIndexMax));
			polygonsIndexMax++;
		}
		
	}
	
	
	
	
	/**
	 * update the faces regarding vertices and faces description
	 */
	public void updateFaces(){
		
		
		//remove old faces and edges
		for (ConstructionElementCycle key : oldPolygons){
			
			GeoPolygon3D polygon = polygons.get(key);
			if (polygon!=null){
				Application.debug("polygon : "+polygon.getLabel());
				polygon.remove();
				polygons.remove(key);
			}
		}
		for (ConstructionElementCycle key : oldSegments){
			GeoSegment3D segment = segments.get(key);
			if (segment!=null){
				Application.debug("segment : "+segment.getLabel());
				segment.remove();
				segments.remove(key);
			}
		}
		
		
		// create missing faces
		for (ConstructionElementCycle currentFace : polygonsIndex.keySet()){
			
			//if a polygons already corresponds to the face description, then pass it
			if (polygons.containsKey(polygonsIndex.get(currentFace)))
				continue;
			
			//vertices of the face
			GeoPointND[] p = new GeoPointND[currentFace.size()];
			
			//edges linked to the face
			GeoSegmentND[] s = new GeoSegmentND[currentFace.size()];
			
			Iterator<ConstructionElement> it2 = currentFace.iterator();
			GeoPointND endPoint = (GeoPointND) it2.next();
			int j=0;
			p[j]= endPoint; //first point for the polygon
			GeoPointND firstPoint = endPoint;
			for (; it2.hasNext();){
				// creates edges
				GeoPointND startPoint = endPoint;
				endPoint = (GeoPointND) it2.next();
				s[j] = createSegment(startPoint, endPoint);
				
				//points for the polygon
				j++;
				p[j]=endPoint;

			}
			//last segment
			s[j] = createSegment(endPoint, firstPoint);
			
			GeoPolygon3D polygon = createPolygon(p);
			polygons.put(polygonsIndex.get(currentFace), polygon);
			polygon.setSegments(s);
        }  
	}
	
	
	
	
	 /** create a polygon joining the given points
	 * @param points vertices of the polygon
	 * @return the polygon
	 */
	public GeoPolygon3D createPolygon(GeoPointND[] points){
		 GeoPolygon3D polygon;

		 AlgoPolygon3D algo = new AlgoPolygon3D(cons,null,points,false,this);            
		 cons.removeFromConstructionList(algo);               

		 polygon = (GeoPolygon3D) algo.getPoly();
		 // refresh color to ensure segments have same color as polygon:
		 polygon.setObjColor(getObjectColor()); 

		 return polygon;
	 }
	
	/**
	 * add the polygon as a polygon linked to this (e.g basis of a prism)
	 * @param polygon
	 */
	public void addPolygonLinked(GeoPolygon polygon){
		polygonsLinked.add(polygon);
		GeoSegmentND[] segments = polygon.getSegments();
		for (int i=0; i<segments.length; i++)
			addSegmentLinked(segments[i]);
	}
	
	/**
	 * add the point as created point (by algo)
	 * @param point
	 */
	public void addPointCreated(GeoPoint3D point){
		pointsCreated.add(point);
	}
	
	 /**
	  * return a segment joining startPoint and endPoint
	  * if this segment already exists in segments, return the already stored one
	  * @param startPoint the start point
	  * @param endPoint the end point
	  * @return the segment
	  */
	
	 public GeoSegmentND createSegment(GeoPointND startPoint, GeoPointND endPoint){
		 
		 ConstructionElementCycle key = 
			 ConstructionElementCycle.SegmentDescription((GeoElement) startPoint,(GeoElement) endPoint);

		 //check if this segment is not already created
		 if (segmentsIndex.containsKey(key))
			 return segments.get(segmentsIndex.get(key));

		 //check if this segment is not a segment linked
		 if (segmentsLinked.containsKey(key))
			 return segmentsLinked.get(key);

		 GeoSegment3D segment;

		 AlgoJoinPoints3D algoSegment = new AlgoJoinPoints3D(cons, 
				 startPoint, endPoint, this, GeoElement3D.GEO_CLASS_SEGMENT3D);            
		 cons.removeFromConstructionList(algoSegment);               

		 segment = (GeoSegment3D) algoSegment.getCS(); 
		 // refresh color to ensure segments have same color as polygon:
		 segment.setObjColor(getObjectColor()); 
		 
		 //TODO translation for edge
		 //segment.setLabel("edge"+startPoint.getLabel()+endPoint.getLabel());

		 Long index = new Long(segmentsIndexMax);
		 segmentsIndex.put(key, index);
		 segments.put(index, segment);
		 segmentsIndexMax++;
			
		 return segment;
		 
		 
	 }
	 
	 
	 public void addSegmentLinked(GeoSegmentND segment){
		 ConstructionElementCycle key = 
			 ConstructionElementCycle.SegmentDescription(segment.getStartPointAsGeoElement(),segment.getEndPointAsGeoElement());

		 segmentsLinked.put(key,segment);
	 }
	 
	 
	 
	 public void defaultLabels(String [] labels){

		 if(cons.isSuppressLabelsActive()){ //for redefine
			 return;
		 }
		 
		 if (labels == null || labels.length == 0) 
	    		labels = new String[1];

		 setLabel(labels[0]); 


		 defaultPolygonsLabels();
		 defaultSegmentLabels();

	 }
	 
	 
	    /**
	     * Inits the labels of this polyhedron, its faces and edges.
	     * labels[0] for polyhedron itself, labels[1..n] for faces and edges,
	     * @param labels
	     */
	    void initLabels(String [] labels) {  
	    	
	    	//Application.printStacktrace("");
	    	
	    	if(cons.isSuppressLabelsActive()){ //for redefine
	    		return;
	    	}
	    	
	    	
	    	
	    	if (labels == null || labels.length == 0) {
	    		labels = new String[1];
	    	}
	    	
	    	/*
	    	String s="labels:\n";
	    	for (int i=0; i<labels.length; i++)
	    		s+=labels[i]+"\n";
	    	s+="points: "+pointsCreated.size()+"\npolygons: "+polygons.size()+"\nsegments: "+segments.size();
	    	Application.debug(s);
	    	*/

	        // first label for polyhedron itself
	    	setLabel(labels[0]);
	    	
	    	int index=1;
	    	
	    	// labels for created points	    	
	    	if (labels.length - index < pointsCreated.size()){
	    		defaultPolygonsLabels();
	    		defaultSegmentLabels();
	    		return;
	    	}
	    	
	    	for (GeoPoint3D point : pointsCreated){
	    		point.setLabel(labels[index]);
	    		index++;
	    	}
	    	
	    	
	    	// labels for polygons	    	
	    	if (labels.length - index < polygons.size()){
	    		defaultPolygonsLabels();
	    		defaultSegmentLabels();
	    		return;
	    	}
	    	
	    	for (GeoPolygon3D polygon : polygons.values()){
	    		polygon.setLabel(labels[index]);
	    		//Application.debug("labels["+index+"]="+labels[index]);
	    		index++;
	    	}

	    	
	    	// labels for segments
	    	if (labels.length - index < segments.size()){
	    		defaultSegmentLabels();
	    		return;
	    	}
    	
	    	// labels for segments
	    	for (GeoSegment3D segment : segments.values()){
	    		segment.setLabel(labels[index]);
	    		index++;
	    	}			 
	 
	    }

	    private void defaultPolygonsLabels() {
	    	for (ConstructionElementCycle key : polygonsIndex.keySet()){
	    		StringBuffer sb = new StringBuffer();
	    		sb.append("face"); //TODO translation
	    		
	    		//stores points names and find the first
	    		String[] points = new String[key.size()];
	    		int indexFirstPointName=0;	    		
	    		int i=0;
	    		for(Iterator<ConstructionElement> it = key.iterator();it.hasNext();){
	    			points[i]=((GeoElement) it.next()).getLabel();
	    			if (points[i].compareToIgnoreCase(points[indexFirstPointName])<0)
	    				indexFirstPointName = i;
	    			i++;
	    		}
	    		
	    		//sets the direction to the next first name
	    		int indexSecondPointPlus = indexFirstPointName+1;
	    		if (indexSecondPointPlus==points.length)
	    			indexSecondPointPlus=0;
	    		int indexSecondPointMinus = indexFirstPointName-1;
	    		if (indexSecondPointMinus==-1)
	    			indexSecondPointMinus=points.length-1;
	    		
	    		if (points[indexSecondPointPlus]
	    		           .compareToIgnoreCase(points[indexSecondPointMinus])<0){
	    			for (int j=indexFirstPointName;j<points.length;j++)
		    			sb.append(points[j]);
		    		for (int j=0;j<indexFirstPointName;j++)
		    			sb.append(points[j]);
	    		}else{
	    			for (int j=indexFirstPointName;j>=0;j--)
		    			sb.append(points[j]);
		    		for (int j=points.length-1;j>indexFirstPointName;j--)
		    			sb.append(points[j]);
	    		}
	    		
	    		
	    		polygons.get(polygonsIndex.get(key)).setLabel(sb.toString());
	    	}	
	    }


	    private void defaultSegmentLabels() {
	    	for (ConstructionElementCycle key : segmentsIndex.keySet()){
	    		StringBuffer sb = new StringBuffer();
	    		sb.append("edge"); //TODO translation
	    		String[] points = new String[2];
	    		int i=0;
	    		for(Iterator<ConstructionElement> it = key.iterator();it.hasNext();){
	    			points[i]=((GeoElement) it.next()).getLabel();
	    			i++;
	    		}
	    		//sets the points names in order
	    		if (points[0].compareToIgnoreCase(points[1])<0){
	    			sb.append(points[0]);
	    			sb.append(points[1]);
	    		}else{
	    			sb.append(points[1]);
	    			sb.append(points[0]);
	    		}
	    		segments.get(segmentsIndex.get(key)).setLabel(sb.toString());
	    	}	
	    }
	 
	
	
	 
	 public GeoSegment3D[] getSegments(){
		 
		 GeoSegment3D[] ret = new GeoSegment3D[segments.size()];
		 int i=0;
		 for (GeoSegment3D segment : segments.values()){
			 ret[i]=segment;
			 i++;
		 }
		 return ret;
	 }
	 
	 public GeoPolygon3D[] getFaces(){
		 GeoPolygon3D[] polygonsArray = new GeoPolygon3D[polygons.size()];
		 int index=0;
		 for (GeoPolygon3D polygon : polygons.values()){
			 polygonsArray[index]=polygon;
			 index++;
		 }

		 return polygonsArray;
	 }
	 
	 /**
	  * 
	  * @return collection of polygons created by this
	  */
	 public Collection<GeoPolygon3D> getPolygons(){
		 return polygons.values();
	 }


	 //TODO remove this and replace with tessellation
	 public void setInteriorPoint(Coords point){
		 for (GeoPolygon3D polygon : polygons.values()){
			 polygon.setInteriorPoint(point);
		 }
		 /* TODO
		 for (GeoPolygon polygon : polygonsLinked){
			 polygon.setInteriorPoint(point);
		 }
		 */
	 }

	 
	 
	 public void setEuclidianVisible(boolean visible) {
		 
		 super.setEuclidianVisible(visible);

		 for (GeoPolygon3D polygon : polygons.values()){
			 polygon.setEuclidianVisible(visible,false);
		 }
		 
		 for (GeoPolygon polygon : polygonsLinked){
			 polygon.setEuclidianVisible(visible,false);
		 }

		 for (GeoSegment3D segment : segments.values()){
			 segment.setEuclidianVisible(visible);
		 }
		 
		 for (GeoSegmentND segment : segmentsLinked.values()){
			 segment.setEuclidianVisible(visible);
		 }
	 }  
	 
	 
	 
	 public void setObjColor(Color color) {
		   
	   		super.setObjColor(color);
	   		

	   		for (GeoPolygon3D polygon : polygons.values()){
	   			polygon.setObjColor(color);
	   			polygon.updateVisualStyle();
	   		}
	   		
	   		for (GeoPolygon polygon : polygonsLinked){
	   			polygon.setObjColor(color);
	   			polygon.updateVisualStyle();
	   		}

	   		for (GeoSegment3D segment : segments.values()){
	   			segment.setObjColor(color);
	   			segment.updateVisualStyle();
	   		}
	   		
	   		for (GeoSegmentND segment : segmentsLinked.values()){
	   			segment.setObjColor(color);
	   			segment.updateVisualStyle();
	   		}
	   }
	 

	   public void setLineType(int type) {
			super.setLineType(type);

			if (polygons==null)
				return;
			
			for (GeoPolygon3D polygon : polygons.values()){
	   			polygon.setLineType(type,false);
	   			polygon.update();
	   		}
	   		
	   		for (GeoPolygon polygon : polygonsLinked){
	   			polygon.setLineType(type,false);
	   			polygon.update();
	   		}

	   		for (GeoSegment3D segment : segments.values()){
	   			segment.setLineType(type);
	   			segment.update();
	   		}
	   		
	   		for (GeoSegmentND segment : segmentsLinked.values()){
	   			((GeoElement) segment).setLineType(type);
	   			segment.update();
	   		}
			
	  }
	   
	 

	   public void setLineTypeHidden(int type) {
			super.setLineTypeHidden(type);

			if (polygons==null)
				return;
			
			for (GeoPolygon3D polygon : polygons.values()){
	   			polygon.setLineTypeHidden(type,false);
	   			polygon.update();
	   		}
	   		
	   		for (GeoPolygon polygon : polygonsLinked){
	   			polygon.setLineTypeHidden(type,false);
	   			polygon.update();
	   		}

	   		for (GeoSegment3D segment : segments.values()){
	   			segment.setLineTypeHidden(type);
	   			segment.update();
	   		}
	   		
	   		for (GeoSegmentND segment : segmentsLinked.values()){
	   			((GeoElement) segment).setLineTypeHidden(type);
	   			segment.update();
	   		}
			
	  }
	   
	   

	   public void setLineThickness(int th) {
			super.setLineThickness(th);
			
			if (polygons==null)
				return;
			
			for (GeoPolygon3D polygon : polygons.values()){
	   			polygon.setLineThickness(th,false);
	   			polygon.update();
	   		}
	   		
	   		for (GeoPolygon polygon : polygonsLinked){
	   			polygon.setLineThickness(th,false);
	   			polygon.update();
	   		}

	   		for (GeoSegment3D segment : segments.values()){
	   			segment.setLineThickness(th);
	   			segment.update();
	   		}
	   		
	   		for (GeoSegmentND segment : segmentsLinked.values()){
	   			segment.setLineThickness(th);
	   			segment.update();
	   		}
	   }
	   
	 
		public void setAlphaValue(float alpha) {
		   
	   		super.setAlphaValue(alpha);
	   		
   			for (GeoPolygon3D polygon : polygons.values()){
   				polygon.setAlphaValue(alpha);
   				polygon.update();
	   		}
   			

	   		for (GeoPolygon polygon : polygonsLinked){
	   			polygon.setAlphaValue(alpha);
	   			polygon.update();
	   		}
	   		
	   		
	
	   }
	 
	 



	 
		


		/*
		public void update() {

			for (GeoPolygon3D polygon : polygons.values()){
				polygon.update();
			}

			for (GeoSegment3D segment : segments.values()){
				segment.update();
			}


		}
		   
	 */
		
		/*
		 * update the polygons and the segments from their parent algorithms
		 *
		public void updatePolygonsAndSegmentsFromParentAlgorithms() {

			for (GeoPolygon3D polygon : polygons.values()){
				//polygon.updateCoordSysAndPoints2D();
				polygon.getParentAlgorithm().update();
			}

			for (GeoSegment3D segment : segments.values()){
				segment.getParentAlgorithm().update();
			}


		}
	 */
	 
	

	public GeoElement copy() {
		return new GeoPolyhedron(this);
	}


	public int getGeoClassType() {
		return GEO_CLASS_POLYHEDRON;
	}


	protected String getTypeString() {
		return "Polyhedron";
	}

	@Override
	public boolean isDefined() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	public void set(GeoElement geo) {
		if (geo instanceof GeoPolyhedron){
			GeoPolyhedron polyhedron = (GeoPolyhedron) geo;
			
			type=polyhedron.type;
			
			polygons.clear();
			polygons.putAll(polyhedron.polygons);
			polygonsIndex.clear();
			polygonsIndex.putAll(polyhedron.polygonsIndex);
			polygonsLinked.clear();
			polygonsLinked.addAll(polyhedron.polygonsLinked);			
			polygonsIndexMax = polyhedron.polygonsIndexMax;
			
			segments.clear();
			segments.putAll(polyhedron.segments);
			segmentsIndex.clear();
			segmentsIndex.putAll(polyhedron.segmentsIndex);
			segmentsLinked.clear();
			segmentsLinked.putAll(polyhedron.segmentsLinked);
			segmentsIndexMax = polyhedron.segmentsIndexMax;
			
		}
	}

	
	
	
	@Override
	public void setUndefined() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String toValueString() {
		// TODO Auto-generated method stub
		return "todo-GeoPolyhedron";
	}


	
	public String getClassName() {
		return "GeoPolyhedron";
	}
	
	
	
	
	
	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
	}
	
	
	
	protected void getXMLtags(StringBuilder sb) {
		getLineStyleXML(sb);
		super.getXMLtags(sb);
	}


	
	///////////////////////////////////////////
	// GeoElement3DInterface

	public Coords getLabelPosition(){
		return new Coords(4); //TODO
	}

	
	
	
	///////////////////////////////////////////
	// Path Interface
	

	/*
	public void pointChanged(GeoPointND PI) {
		// TODO Auto-generated method stub
		
	}


	public void pathChanged(GeoPointND PI) {
		// TODO Auto-generated method stub
		
	}


	public boolean isOnPath(GeoPointND PI, double eps) {
		// TODO Auto-generated method stub
		return false;
	}


	public double getMinParameter() {
		// TODO Auto-generated method stub
		return 0;
	}


	public double getMaxParameter() {
		// TODO Auto-generated method stub
		return 0;
	}


	public boolean isClosedPath() {
		// TODO Auto-generated method stub
		return false;
	}


	public PathMover createPathMover() {
		// TODO Auto-generated method stub
		return null;
	}

	*/
	
	public boolean isPath(){
		return true;
	}
	
	
	
	
	
	
	
	
	public void remove() {
		
		//prevent from removing this when redefine a prism (see AlgoJoinPoints3D and AlgoPolygon)
		if (this!=getConstruction().getKeepGeo())
			super.remove();
	}
	
	
	
	

}
