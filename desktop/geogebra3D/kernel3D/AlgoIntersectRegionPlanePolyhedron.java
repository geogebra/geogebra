/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoIntersectLines.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoElementND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoSegmentND;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Algo for intersection of a plane with a polyhedron, outputs polygons
 * 
 * @author matthieu
 */
public class AlgoIntersectRegionPlanePolyhedron extends AlgoIntersectPathPlanePolygon3D {
	
	private GeoPolyhedron polyhedron;
	

	private OutputHandler<GeoPolygon3D> outputPolygons;
	private OutputHandler<GeoPoint3D> outputPoints;
	protected OutputHandler<GeoSegment3D> outputSegments; // output
	

	
	/**
	 * class extending Coords with reference to parent geo
	 *
	 */
	private class CoordsWithParent extends Coords implements Comparable<CoordsWithParent> {

		protected GeoElementND parent;
		
		private Double parameter;
		
		public CoordsWithParent(Double parameter, Coords v, GeoElementND parent) {
			super(v);
			this.parent = parent;
			this.parameter =  parameter;
		}

		/**
		 * 
		 * @return polygons on which this point belongs
		 */
		public TreeSet<GeoPolygon> getPolygons() {
			
			//parent is segment
			if (parent instanceof GeoSegmentND)
				return ((GeoSegmentND) parent).getEdgeOf();
			
			//parent is point
			return ((GeoPointND) parent).getVertexOf();
			
		}

		public int compareTo(CoordsWithParent o) {
			//first compare parameters
			if (Kernel.isGreater(parameter, o.parameter))
				return 1;
			if (Kernel.isGreater(o.parameter, parameter))
				return -1;
			
			//if same parameter, compare parents
			return compareParentTo(o);
			
		}
		
		/**
		 * compare parent to o
		 * @param o other coords
		 * @return comparison result
		 */
		public int compareParentTo(CoordsWithParent o){
			return parent.toGeoElement().compareTo(o.parent.toGeoElement());
		}
		
	}
	
	/**
	 * coords for each face
	 */
	protected TreeSet<CoordsWithParent> newCoords;

	
	/**
	 * bi-point for each intersection segment
	 *
	 */
	private class Segment {
		protected CoordsWithParent p1, p2;
		
		public Segment(CoordsWithParent p1, CoordsWithParent p2) {
				this.p1 = p1;
				this.p2 = p2;
		}
		
	}
	
	
	
	/**
	 * List of coords than can be compared
	 * @author mathieu
	 *
	 */
	private class Vertices extends ArrayList<Coords> implements Comparable<Vertices>{
		
		//index for the lowest vertex
		private int lowest = -1;
		
		//direction for neighbor
		private short direction = 0;
		
		//index for currently looked (see next() method)
		private int current;

		public Vertices() {
			super();
		}
		
		@Override
		public boolean add(Coords e){
			
			if (lowest == -1){ //no lowest element for now
				lowest = 0; //first element is the lowest
			}else{
				if (Coords.COMPARATOR.compare(e, get(lowest)) < 0){
					lowest = size();
				}
			}
			
			return super.add(e);
		}

		
		/**
		 * find direction from lowest to lowest neighbor
		 */
		public void setDirection(){
			
			int n1 = lowest-1;
			int n2 = lowest+1;
			if (n1 < 0){
				n1 = size()-1;
			}else if (n2 >= size()){
				n2 = 0;
			}
			
			if (Coords.COMPARATOR.compare(get(n1), get(n2)) < 0){
				direction = -1;
			}else{
				direction = 1;
			}
						
		}
		
		/**
		 * Set current index to next element
		 * @return next element regarding direction
		 */
		private Coords next(){
			current += direction;
			if (current >= size()){
				current = 0;
			}else if (current < 0){
				current = size()-1;
			}
			
			return get(current);
				
		}
		
		
		public int compareTo(Vertices o) {
			
			//first compare sizes
			if (this.size()<o.size())
				return -1;
			if (o.size()<this.size())
				return 1;
			
			//compare lowest coords
			if (Coords.COMPARATOR.compare(get(lowest),o.get(o.lowest))<0)
				return -1;
			if (Coords.COMPARATOR.compare(get(lowest),o.get(o.lowest))>0)
				return 1;
			
			//compare neighbors 
			int visited = 0;
			while (visited<size()){
				Coords thisCoords = next();
				Coords oCoords = o.next();
				if (Coords.COMPARATOR.compare(thisCoords,oCoords)<0)
					return -1;
				if (Coords.COMPARATOR.compare(thisCoords,oCoords)>0)
					return 1;
				visited++;
			}
			
			//equal
			return 0;
		}
		
	}
	
	
	

	/**
	 * common constructor
	 * 
	 * @param c
	 * @param labels
	 * @param plane plane
	 * @param p polyhedron
	 * @param outputSizes output sizes
	 */
	public AlgoIntersectRegionPlanePolyhedron(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolyhedron p, int[] outputSizes) {



			super(c);
			

			setFirstInput(plane);
			setSecondInput(p);

			createOutput();

			
			setInputOutput(); // for AlgoElement

			// set labels
			if (labels==null){
				outputPolygons.setLabels(null);
				outputPoints.setLabels(null);
				outputSegments.setLabels(null);
			}else{
				int labelsLength = labels.length;
				if (labelsLength > 1) {
					//App.debug("\nici : "+outputSizes[0]+","+outputSizes[1]+","+outputSizes[2]);
					if (outputSizes != null){
						//set output sizes
						outputPolygons.adjustOutputSize(outputSizes[0], false);
						outputPoints.adjustOutputSize(outputSizes[1], false);
						outputSegments.adjustOutputSize(outputSizes[2], false);
						
						
						
						//set labels
						int i1 = 0;
						int i2 = 0;
					
						while (i1 < outputSizes[0]){
							outputPolygons.getElement(i1).setLabel(labels[i2]);
							i1++;
							i2++;
						}
						
						i1 = 0;
						while (i1 < outputSizes[1]){
							outputPoints.getElement(i1).setLabel(labels[i2]);
							i1++;
							i2++;
						}
						
						i1 = 0;
						while (i1 < outputSizes[2]){
							outputSegments.getElement(i1).setLabel(labels[i2]);
							i1++;
							i2++;
						}
						
						
						
					}else{
						//set default
						outputPolygons.setLabels(null);
						outputSegments.setLabels(null);
						outputPoints.setLabels(null);
					}
				} else if (labelsLength == 1) {
					outputPolygons.setIndexLabels(labels[0]);
				} 
			}
			
			
			update();

		

	}

	@Override
	protected void setSecondInput(GeoElement geo){
		this.polyhedron = (GeoPolyhedron) geo;
	}

	@Override
	protected GeoElement getSecondInput(){
		return polyhedron;
	}

	


	/**
	 * set for all intersection points coords. Used for intersections equal to just one point.
	 */
	private TreeSet<Coords> polyhedronVertices;
	
	
	
	@Override
	protected void addCoords(double parameter, Coords coords, GeoElementND geo){
		newCoords.add(new CoordsWithParent(parameter, coords, geo));
		if (geo instanceof GeoPointND)
			polyhedronVertices.add(coords);
	}
	
	private TreeMap<GeoPolygon, ArrayList<Segment>> newCoordsList;
	
	@Override
	protected void setNewCoords(){
		
		if (newCoordsList==null)
			newCoordsList = new TreeMap<GeoPolygon, ArrayList<Segment>>();
		else
			newCoordsList.clear();
		
		//for polyhedron vertices
		if (polyhedronVertices == null)
			polyhedronVertices =  new TreeSet<Coords>(Coords.COMPARATOR);
		else
			polyhedronVertices.clear();


		
		/*
		if (originalEdges==null)
			originalEdges = new TreeMap<GeoElement, TreeMap<GeoElement,Segment>>();
		else
			originalEdges.clear();
			*/
		
		for (GeoPolygon polygon: polyhedron.getPolygons()){
			p = polygon;
			setNewCoordsList();
		}
		
		for (GeoPolygon polygon: polyhedron.getPolygonsLinked()){
			p = polygon;
			setNewCoordsList();
		}


	}
	
	private void setNewCoordsList(){
		//line origin and direction
		setIntersectionLine();
		
		
		//check if polygon is included in the plane		
		if (d1.isZero() && !(Kernel.isZero(o1.getW()))){//then include all edges of the polygon
			
			GeoPointND[] points = p.getPointsND();
			Vertices vertices = new Vertices();
			
			for (GeoPointND point : points){
				vertices.add(point.getInhomCoordsInD(3));
			}
			
			vertices.setDirection();
			//check if this list has not already be computed
			if(checkVerticesList.add(vertices)){
				verticesList.add(vertices);
			}

			/*
			segmentCoords = new ArrayList<Segment>();
			GeoPointND p2 = points[0];
			for (int i = 0; i<points.length; i++){
				GeoPointND p1 = p2;
				p2 = points[(i+1)%(points.length)];
				
				segmentCoords.add(new Segment(
						new CoordsWithParent((double) i, p1.getInhomCoordsInD(3), p1), 
						new CoordsWithParent((double) i+1, p2.getInhomCoordsInD(3), p2)));
				
				newCoordsList.put(p, segmentCoords);
				//App.debug("\npoly (included):"+p+"\nsegmentCoords.size():"+segmentCoords.size());
			}
			*/

		}else{//regular case: polygon not included in plane

			// fill a new points map
			if (newCoords==null)
				newCoords = new TreeSet<CoordsWithParent>();
			else
				newCoords.clear();

			//add intersection coords
			intersectionsCoords(p);

			//add polygon points
			addPolygonPoints();

			if (newCoords.size()>1){ //save it only if at least two points
				segmentCoords = getSegmentsCoords();
				//add (polygon,segments) to newCoordsList
				if (segmentCoords.size()>0){
					newCoordsList.put(p, segmentCoords);
					//App.debug("\npoly:"+p+"\nnewCoords.size():"+newCoords.size()+"\nsegmentCoords.size():"+segmentCoords.size());
				}
			}
		}
		

	}
	
	/*
	 * segments equal to original edges
	 */
	//private TreeMap<GeoElement,TreeMap<GeoElement,Segment>> originalEdges;
	
	private ArrayList<Segment> getSegmentsCoords(){
		ArrayList<Segment> ret = new ArrayList<Segment>();
		
		Iterator<CoordsWithParent> it = newCoords.iterator();
		CoordsWithParent b = it.next();
		//use start/end of segment to merge following segments
		CoordsWithParent startSegment = null;
		CoordsWithParent endSegment = null;
		while (it.hasNext()) {
			CoordsWithParent a = b;
			b = it.next();
			//check if the segment is included in the polygon: check the midpoint
			if (checkMidpoint(p, a, b)){
				if (startSegment==null)
					startSegment = a; //new start segment
				endSegment = b; //extend segment to b
			}else{
				if (startSegment!=null){//add last correct segment
					ret.add(new Segment(startSegment,endSegment));
					startSegment=null;
				}
			}
		}
		
		if (startSegment!=null)//add last correct segment
			ret.add(new Segment(startSegment,endSegment));
		
		
		return ret;
	}
	
	
	
	@SuppressWarnings("serial")
	private class VerticesList extends ArrayList<ArrayList<Coords>>{
		
		protected int cumulateSize = 0;
		
		public VerticesList() {
			super();
		}

		@Override
		public boolean add(ArrayList<Coords> vertices){
			cumulateSize+=vertices.size();
			return super.add(vertices);
		}
		
		@Override
		public void clear(){
			cumulateSize = 0;
			super.clear();
		}
		
	}
	
	private VerticesList verticesList;
	
	private TreeSet<Vertices> checkVerticesList;
	
	
	
	private ArrayList<Segment> segmentCoords;
	
	/**
	 * find next vertex linking the start point of the polygon with new intersection segment
	 * @param p2
	 * @param startPoint
	 * @param oldPoint vertex before startPoint
	 * @return next vertex
	 */
	private CoordsWithParent nextVertex(GeoPolygon p2, CoordsWithParent startPoint, GeoElementND oldPoint){
		
		//get intersection segments coords for this polygon
		segmentCoords = newCoordsList.get(p2);
				
		CoordsWithParent a;
		CoordsWithParent b = null;
		
		//App.debug("\nstart parent:"+startPoint.parent+"\nold parent:"+oldPoint.parent);
		
		//check if for a segment, one of the vertex as same parent as starting vertex
		//then take the second point as next vertex
		boolean notFound = true;
		int i;
		for (i=0; i<segmentCoords.size() && notFound; i++){
			Segment segment = segmentCoords.get(i);
			a = segment.p1;
			if (a.parent==startPoint.parent){
				b = segment.p2;
				//App.debug("\ni:"+i+"\na:"+a.parent+"\nb:"+b.parent);
				if (b.parent!=oldPoint){ //prevent immediate return
					notFound = false;
				}
			}else{
				b = a;
				a = segment.p2;
				if (a.parent==startPoint.parent){
					//App.debug("\ni:"+i+"\na:"+a.parent+"\nb:"+b.parent);
					if (b.parent!=oldPoint){ //prevent immediate return
						notFound = false;
					}
				}
			}
		}


		if (notFound)
			b=null;
		else
			//remove the segment found: not usable anymore
			removeSegmentCoords(i-1,p2);

		return b;
	}
	
	private void removeSegmentCoords(int index, GeoPolygon p2){
		
		segmentCoords.remove(index);
		//App.debug("\np2:"+p2+"\nsize="+segmentCoords.size());
		if (segmentCoords.size()==0)
			newCoordsList.remove(p2);
			
	}
	
	
	/**
	 * find next vertex linking a vertex of the polyhedron to next segment
	 * @param startPoint start vertex
	 * @param oldPoint vertex before startPoint
	 * @return next vertex
	 */
	private CoordsWithParent nextVertex(CoordsWithParent startPoint, GeoElementND oldPoint){
		
		CoordsWithParent b;
		
		// 1) try keep same poly (interior point)
		if (newCoordsList.containsKey(p)){
			b=nextVertex(p, startPoint, oldPoint);
			if (b!=null)
				return b;
		}
		
		

		
		// 2) try other polygons
		TreeSet<GeoPolygon> polySet = startPoint.getPolygons();
		Iterator<GeoPolygon> it = polySet.iterator();
		GeoPolygon p2 = null;
		while (it.hasNext()){
			p2 = it.next();
			//App.debug("\np2:"+p2+"\np2==p:"+(p2==p)+"\nkey:"+newCoordsList.containsKey(p2));
			//find other polygon, contained as a key
			if (p2 != p && newCoordsList.containsKey(p2)){					
				//App.debug("\npoly2:"+p2);
				//try to find next vertex
				b=nextVertex(p2, startPoint, oldPoint);
				if (b!=null){ //if found
					p=p2;
					//App.debug("\nb.parent:"+b.parent);
					return b;
				}
			}
		}
		
		//3) return null: no next vertex
		return null;
		
	}
	
	/**
	 * Add vertices from one to the next
	 * @return vertices list
	 */
	private Vertices addVertices(){
		
		Vertices vertices = new Vertices();

		//take first segment for the face p
		segmentCoords = newCoordsList.get(p);
		
		//start with first point of the segment
		CoordsWithParent firstPoint = segmentCoords.get(0).p1;
		CoordsWithParent startPoint = segmentCoords.get(0).p2;
		removeSegmentCoords(0, p);
		vertices.add(firstPoint);
		//App.debug("\na.parent:"+firstPoint.parent);//App.debug("\n\n\n\n\n");
		//App.debug("\nb.parent:"+startPoint.parent+"\npoly:"+p);//App.debug("\n\n\n\n\n");
		//at first oldParent is null, so polygons A-B-A are possible
		GeoElementND oldParent = null;
		while (startPoint.parent!=firstPoint.parent){
			//if (!startPoint.isEqual(oldPoint))
			vertices.add(startPoint);
			CoordsWithParent c = nextVertex(startPoint,oldParent);
			if (c==null) //no next point
				return null;
			oldParent = startPoint.parent;
			startPoint = c;
			//App.debug("\nb.parent:"+startPoint.parent+"\npoly:"+p);//App.debug("\n\n\n\n\n");
		}
		
		return vertices;

	}
	
	/**
	 * set polyhedron vertices as dummy polygons output
	 * @param indexPolygon start index for polygons
	 * @param indexPoint start index for points
	 * @param indexSegment start index for segments
	 */
	private void addPolyhedronVerticesToOutput(int indexPolygon, int indexPoint, int indexSegment){
		for (Coords coords : polyhedronVertices){
			GeoPolygon outputPoly = outputPolygons.getElement(indexPolygon);
			GeoPoint3D point =  outputPoints.getElement(indexPoint);
			point.setCoords(coords);
			GeoSegment3D seg = outputSegments.getElement(indexSegment);
			seg.modifyInputPolyAndPoints(outputPoly, point, point);
			outputPoly.setPoints(new GeoPoint3D[] {point, point}, null, false); // don't create segments
			outputPoly.setSegments(new GeoSegment3D[] {seg, seg});
			outputPoly.calcArea();
			indexPolygon++;
			indexPoint++;
			indexSegment++;
		}
	}
	
	@Override
	public void compute() {
		
		//set intersection vertices
		//(set it here since maybe some faces are included in the plane)
		if (verticesList == null)
			verticesList = new VerticesList();
		else
			verticesList.clear();
		
		if (checkVerticesList == null)
			checkVerticesList = new TreeSet<Vertices>();
		else
			checkVerticesList.clear();
		
		

		
		// set the point map
		setNewCoords();
		
		//App.debug("\noriginalEdges:"+originalEdges);
		

		
		
		//App.debug(polyhedronVertices);
		
		
		// set output
		if (newCoordsList.size()==0 && verticesList.size()==0) { //no intersection
			//set points, segments and polygons equal to intersection with polyhedron vertices
			outputPolygons.adjustOutputSize(polyhedronVertices.size(), false);
			outputPoints.adjustOutputSize(polyhedronVertices.size(), false);
			outputSegments.adjustOutputSize(polyhedronVertices.size(), false);
			addPolyhedronVerticesToOutput(0, 0, 0);
			
		} else {		

			//start with one face, set a polygon, then get a new face, etc.
			while (newCoordsList.size()!=0){
				//App.debug(newCoordsList.keySet());
				p = newCoordsList.firstKey();
				Vertices vertices = addVertices();
				if (vertices!=null){ //prevent not matching search
					vertices.setDirection();
					//check if this list has not already be computed
					if(checkVerticesList.add(vertices)){
						verticesList.add(vertices);
					}
				}
			}
			//App.debug(newCoordsList.keySet());
			
			
			//set output points
			outputPoints.adjustOutputSize(verticesList.cumulateSize + polyhedronVertices.size(),false);
			outputPoints.updateLabels();
			int segmentIndex = 0;
			for (ArrayList<Coords> vertices : verticesList){
				int length = vertices.size();
				for (int i = 0; i<length; i++){
					GeoPoint3D point = outputPoints.getElement(segmentIndex);
					point.setCoords(vertices.get(i));
					segmentIndex++;
				}
			}
			
			//adjust output polygons size
			outputPolygons.adjustOutputSize(verticesList.size() + polyhedronVertices.size(), false);
			outputPolygons.updateLabels();
			
			//get points list
			GeoPoint3D[] points = new GeoPoint3D[verticesList.cumulateSize];
			points = outputPoints.getOutput(points);
			
			//set output segments and polygons
			outputSegments.adjustOutputSize(verticesList.cumulateSize + polyhedronVertices.size(),false);
			outputSegments.updateLabels();
			int pointIndex = 0;
			int polygonIndex = 0;
			segmentIndex = 0;
			for (ArrayList<Coords> vertices : verticesList){
				int length = vertices.size();
				//App.debug("polygonIndex: "+polygonIndex);
				GeoPolygon outputPoly = outputPolygons.getElement(polygonIndex);
				GeoPoint3D[] polyPoints = new GeoPoint3D[length];
				GeoSegment3D[] polySegments = new GeoSegment3D[length];
				for (int i = 0; i<length; i++){
					//App.debug(points[polygonOffset + i]);
					outputSegments.getElement(segmentIndex).modifyInputPolyAndPoints(
							outputPoly,
							points[pointIndex + i],
							points[pointIndex + (i + 1) % length]);
					polyPoints[i] = points[pointIndex + i];
					polySegments[i] = outputSegments.getElement(segmentIndex);
					segmentIndex++;
				}
				
				// update polygon
				outputPoly.setPoints(polyPoints, null, false); // don't create segments
				outputPoly.setSegments(polySegments);
				outputPoly.calcArea();
				
				pointIndex+=length;
				polygonIndex++;
			}
			
			//add isolate polyhedron vertices
			addPolyhedronVerticesToOutput(polygonIndex, pointIndex, segmentIndex);

			
		}
	}
	

    @Override
	protected boolean checkParameter(double t1){
    	return true; //nothing to check here
    }

    
	@Override
	public final Commands getClassName() {
        return Commands.IntersectRegion;
    }
	
	
	
	
	
	
	
	
	
	private final void createOutput(){
		
		outputPolygons = new OutputHandler<GeoPolygon3D>(
				new elementFactory<GeoPolygon3D>() {
					public GeoPolygon3D newElement() {
						GeoPolygon3D p = new GeoPolygon3D(cons);
						p.setParentAlgorithm(AlgoIntersectRegionPlanePolyhedron.this);
						if (outputPolygons.size()>0)
							p.setAllVisualProperties(outputPolygons.getElement(0), false);
						p.setViewFlags(getFirstInput().getViewSet());
						p.setNotFixedPointsLength(true);
						p.setOrthoNormalRegionCS();
						return p;
					}
				});
		
		outputPolygons.adjustOutputSize(1,false);
		
		outputPoints = new OutputHandler<GeoPoint3D>(
				new elementFactory<GeoPoint3D>() {
					public GeoPoint3D newElement() {
						GeoPoint3D newPoint = new GeoPoint3D(cons);
						newPoint.setCoords(0, 0, 0, 1);
						newPoint.setParentAlgorithm(AlgoIntersectRegionPlanePolyhedron.this);
						newPoint.setAuxiliaryObject(true);
						newPoint.setViewFlags(getFirstInput().getViewSet());
						/*
						newPoint.setPointSize(A.getPointSize());
						newPoint.setEuclidianVisible(A.isEuclidianVisible()
								|| B.isEuclidianVisible());
						newPoint.setAuxiliaryObject(true);
						newPoint.setViewFlags(A.getViewSet());
						GeoBoolean conditionToShow = A.getShowObjectCondition();
						if (conditionToShow == null)
							conditionToShow = B.getShowObjectCondition();
						if (conditionToShow != null) {
							try {
								((GeoElement) newPoint)
										.setShowObjectCondition(conditionToShow);
							} catch (Exception e) {
								// circular exception -- do nothing
							}
						}
						*/
						return newPoint;
					}
				});
		
		outputPoints.adjustOutputSize(1,false);
		

		outputSegments = //createOutputSegments();
				new OutputHandler<GeoSegment3D>(
						new elementFactory<GeoSegment3D>() {
							public GeoSegment3D newElement() {
								GeoSegment3D segment = (GeoSegment3D) outputPolygons
										.getElement(0).createSegment(outputPoints.getElement(0), outputPoints.getElement(0), true);
								segment.setAuxiliaryObject(true);
								//segment.setLabelVisible(showNewSegmentsLabels);
								segment.setViewFlags(getFirstInput().getViewSet());
								return segment;
							}
						});
		
	}
	
	
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = getFirstInput();
		input[1] = getSecondInput();

		// set dependencies
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
		cons.addToAlgorithmList(this);
	}
	
	
	
	@Override
	protected void getCmdOutputXML(StringBuilder sb, StringTemplate tpl) {
		
		//add output sizes (polygons, points, segments)
		sb.append("\t<outputSizes val=\"");
		sb.append(outputPolygons.size());
		sb.append(",");
		sb.append(outputPoints.size());
		sb.append(",");
		sb.append(outputSegments.size());
		sb.append("\"");
		sb.append("/>\n");
	
		//common method
		super.getCmdOutputXML(sb, tpl);


	}
}
