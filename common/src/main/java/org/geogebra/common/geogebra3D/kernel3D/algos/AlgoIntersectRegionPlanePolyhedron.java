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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyhedron;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoSegment3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * Algo for intersection of a plane with a polyhedron, outputs polygons
 * 
 * @author matthieu
 */
public class AlgoIntersectRegionPlanePolyhedron
		extends AlgoIntersectPathPlanePolygon3D {

	private GeoPolyhedron polyhedron;

	private OutputHandler<GeoPolygon3D> outputPolygons;
	private OutputHandler<GeoPoint3D> outputPoints;
	/** output segments for polyhedron */
	protected OutputHandler<GeoSegment3D> outputSegmentsPolyhedron;

	private boolean hasLabels = false;
	/**
	 * set for all intersection points coords. Used for intersections equal to
	 * just one point.
	 */
	private TreeSet<Coords> polyhedronVertices;

	/**
	 * map from intersection parents to set of polygons
	 */
	private TreeMap<GeoElementND, TreeSet<GeoPolygon>> parentToPolygons;
	private VerticesList verticesList;

	private ArrayList<Vertices> polyhedronFaces;

	private TreeSet<Vertices> checkVerticesList;

	private ArrayList<Segment> segmentCoords;
	private TreeMap<GeoPolygon, ArrayList<Segment>> newCoordsList;
	private int removeSegmentCoordsIndex;
	private GeoPolygon removeSegmentCoordsPolygon;
	/**
	 * coords for each face
	 */
	protected TreeSet<CoordsWithParent> newCoords3D;

	/**
	 * class extending Coords with reference to parent geo
	 *
	 */
	private static class CoordsWithParent extends Coords
			implements Comparable<CoordsWithParent> {

		protected GeoElementND parent;

		private Double parameter;

		public CoordsWithParent(Double parameter, Coords v,
				GeoElementND parent) {
			super(v);
			this.parent = parent;
			this.parameter = parameter;
		}

		@Override
		public int compareTo(CoordsWithParent o) {
			// first compare parameters
			if (DoubleUtil.isGreater(parameter, o.parameter)) {
				return 1;
			}
			if (DoubleUtil.isGreater(o.parameter, parameter)) {
				return -1;
			}

			// if same parameter, compare parents
			return compareParentTo(o);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof CoordsWithParent) {
				return compareTo((CoordsWithParent) o) == 0;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return DoubleUtil.hashCode(parameter) ^ parent.hashCode();
		}

		/**
		 * compare parent to o
		 * 
		 * @param o
		 *            other coords
		 * @return comparison result
		 */
		public int compareParentTo(CoordsWithParent o) {
			return parent.toGeoElement().compareTo(o.parent.toGeoElement());
		}

	}

	private TreeSet<GeoPolygon> getPolygons(CoordsWithParent coords) {
		return parentToPolygons.get(coords.parent);
	}

	/**
	 * bi-point for each intersection segment
	 *
	 */
	private static class Segment {
		protected CoordsWithParent p1;
		protected CoordsWithParent p2;

		public Segment(CoordsWithParent p1, CoordsWithParent p2) {
			this.p1 = p1;
			this.p2 = p2;
		}

	}

	/**
	 * List of coords than can be compared
	 * 
	 * @author mathieu
	 *
	 */
	@SuppressWarnings("serial")
	private static class Vertices extends ArrayList<Coords>
			implements Comparable<Vertices> {

		// index for the lowest vertex
		private int lowest = -1;

		// direction for neighbor
		private short direction = 0;

		// index for currently looked (see next() method)
		private int current;

		protected Vertices() {
			// avoid synth access error
		}

		@Override
		public boolean add(Coords e) {

			if (lowest == -1) { // no lowest element for now
				lowest = 0; // first element is the lowest
			} else {
				if (COORDS_COMPARATOR.compare(e, get(lowest)) < 0) {
					lowest = size();
				}
			}

			return super.add(e);
		}

		/**
		 * find direction from lowest to lowest neighbor
		 */
		public void setDirection() {

			int n1 = lowest - 1;
			int n2 = lowest + 1;
			if (n1 < 0) {
				n1 = size() - 1;
			} else if (n2 >= size()) {
				n2 = 0;
			}

			if (COORDS_COMPARATOR.compare(get(n1), get(n2)) < 0) {
				direction = -1;
			} else {
				direction = 1;
			}

		}

		/**
		 * Set current index to next element
		 * 
		 * @return next element regarding direction
		 */
		private Coords next() {
			current += direction;
			if (current >= size()) {
				current = 0;
			} else if (current < 0) {
				current = size() - 1;
			}

			return get(current);

		}

		private void start() {
			current = lowest;
		}

		@Override
		public int compareTo(Vertices o) {

			// first compare sizes
			if (this.size() < o.size()) {
				return -1;
			}
			if (o.size() < this.size()) {
				return 1;
			}

			// compare lowest coords
			if (COORDS_COMPARATOR.compare(get(lowest), o.get(o.lowest)) < 0) {
				return -1;
			}
			if (COORDS_COMPARATOR.compare(get(lowest), o.get(o.lowest)) > 0) {
				return 1;
			}

			// compare neighbors
			start();
			o.start();
			int visited = 0;
			while (visited < size()) {
				Coords thisCoords = next();
				Coords oCoords = o.next();
				if (COORDS_COMPARATOR.compare(thisCoords, oCoords) < 0) {
					return -1;
				}
				if (COORDS_COMPARATOR.compare(thisCoords, oCoords) > 0) {
					return 1;
				}
				visited++;
			}

			// equal
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj); // see EQ_DOESNT_OVERRIDE_EQUALS in SpotBugs
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

	}

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 * @param plane
	 *            plane
	 * @param p
	 *            polyhedron
	 */
	public AlgoIntersectRegionPlanePolyhedron(Construction c, GeoPlane3D plane,
			GeoPolyhedron p) {
		this(c, plane, p, false);
	}

	private AlgoIntersectRegionPlanePolyhedron(Construction c, GeoPlane3D plane,
			GeoPolyhedron p, boolean hasLabels) {

		super(c);

		this.hasLabels = hasLabels;

		setFirstInput(plane);
		setSecondInput(p);

		createOutput();

		setInputOutput(); // for AlgoElement
	}

	/**
	 * common constructor
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            output labels
	 * @param plane
	 *            plane
	 * @param p
	 *            polyhedron
	 * @param outputSizes
	 *            output sizes
	 */
	public AlgoIntersectRegionPlanePolyhedron(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolyhedron p, int[] outputSizes) {

		this(c, plane, p, true);

		// set labels
		if (labels == null) {
			outputPolygons.setLabels(null);
			outputPoints.setLabels(null);
			outputSegmentsPolyhedron.setLabels(null);
		} else {
			int labelsLength = labels.length;
			if (labelsLength > 1) {
				// Log.debug("\nici :
				// "+outputSizes[0]+","+outputSizes[1]+","+outputSizes[2]);
				if (outputSizes != null) {
					// set output sizes
					outputPolygons.adjustOutputSize(outputSizes[0], false);
					outputPoints.adjustOutputSize(outputSizes[1], false);
					outputSegmentsPolyhedron.adjustOutputSize(outputSizes[2],
							false);

					// set labels
					int i1 = 0;
					int i2 = 0;

					while (i1 < outputSizes[0]) {
						outputPolygons.getElement(i1).setLabel(labels[i2]);
						i1++;
						i2++;
					}

					i1 = 0;
					while (i1 < outputSizes[1]) {
						outputPoints.getElement(i1).setLabel(labels[i2]);
						i1++;
						i2++;
					}

					i1 = 0;
					while (i1 < outputSizes[2]) {
						outputSegmentsPolyhedron.getElement(i1)
								.setLabel(labels[i2]);
						i1++;
						i2++;
					}

				} else {
					// set default
					outputPolygons.setLabels(null);
					outputSegmentsPolyhedron.setLabels(null);
					outputPoints.setLabels(null);
				}
			} else if (labelsLength == 1) {
				outputPolygons.setIndexLabels(labels[0]);
			}
		}

		update();
	}

	@Override
	protected void setSecondInput(GeoElement geo) {
		this.polyhedron = (GeoPolyhedron) geo;
	}

	@Override
	protected GeoElement getSecondInput() {
		return polyhedron;
	}

	@Override
	protected void addCoords(double parameter, Coords coords,
			GeoElementND parent) {
		Coords c = coords.copyVector();
		newCoords3D.add(new CoordsWithParent(parameter, c, parent));
		if (parent instanceof GeoPointND) {
			// boolean b=
			polyhedronVertices.add(c);
			// Log.debug("\nb: "+b+"\nparent: "+parent+"\ncoords:\n"+coords);
		}
	}

	@Override
	protected void setNewCoords() {

		if (newCoordsList == null) {
			newCoordsList = new TreeMap<>();
		} else {
			newCoordsList.clear();
		}

		if (parentToPolygons == null) {
			parentToPolygons = new TreeMap<>();
		} else {
			parentToPolygons.clear();
		}

		// for polyhedron vertices
		if (polyhedronVertices == null) {
			polyhedronVertices = new TreeSet<>(COORDS_COMPARATOR);
		} else {
			polyhedronVertices.clear();
		}

		/*
		 * if (originalEdges==null) originalEdges = new TreeMap<GeoElement,
		 * TreeMap<GeoElement,Segment>>(); else originalEdges.clear();
		 */

		for (GeoPolygon polygon : polyhedron.getPolygons()) {
			p = polygon;
			setNewCoordsList();
		}

		for (GeoPolygon polygon : polyhedron.getPolygonsLinked()) {
			p = polygon;
			setNewCoordsList();
		}

	}

	private void setNewCoordsList() {

		// check if the polygon is defined (e.g. when regular polygon as pyramid
		// bottom)
		if (!p.isDefined()) {
			return;
		}

		// line origin and direction
		setIntersectionLine();

		// check if polygon is included in the plane
		if (d1.isZero() && !(DoubleUtil.isZero(o1.getW()))) {
			// then include all edges of the polygon

			GeoPointND[] points = p.getPointsND();
			Vertices vertices = new Vertices();

			for (GeoPointND point : points) {
				vertices.add(point.getInhomCoordsInD3());
			}

			vertices.setDirection();

			// add to specific list to be added later
			polyhedronFaces.add(vertices);

			/*
			 * //check if this list has not already be computed
			 * if(checkVerticesList.add(vertices)){ addToVerticesList(vertices);
			 * }
			 */

			/*
			 * segmentCoords = new ArrayList<Segment>(); GeoPointND p2 =
			 * points[0]; for (int i = 0; i<points.length; i++){ GeoPointND p1 =
			 * p2; p2 = points[(i+1)%(points.length)];
			 * 
			 * segmentCoords.add(new Segment( new CoordsWithParent((double) i,
			 * p1.getInhomCoordsInD3(), p1), new CoordsWithParent((double) i+1,
			 * p2.getInhomCoordsInD3(), p2)));
			 * 
			 * newCoordsList.put(p, segmentCoords); //Log.debug(
			 * "\npoly (included):"
			 * +p+"\nsegmentCoords.size():"+segmentCoords.size()); }
			 */

		} else { // regular case: polygon not included in plane

			// fill a new points map
			if (newCoords3D == null) {
				newCoords3D = new TreeSet<>();
			} else {
				newCoords3D.clear();
			}

			// add intersection coords
			intersectionsCoords(p);

			// add polygon points
			addPolygonPoints();

			if (newCoords3D.size() > 1) { // save it only if at least two points
				segmentCoords = getSegmentsCoords();
				// add (polygon,segments) to newCoordsList
				if (segmentCoords.size() > 0) {
					newCoordsList.put(p, segmentCoords);
				}
			}
		}

	}

	/*
	 * segments equal to original edges
	 */
	// private TreeMap<GeoElement,TreeMap<GeoElement,Segment>> originalEdges;

	private ArrayList<Segment> getSegmentsCoords() {
		ArrayList<Segment> ret = new ArrayList<>();

		Iterator<CoordsWithParent> it = newCoords3D.iterator();
		CoordsWithParent b = it.next();
		// use start/end of segment to merge following segments
		CoordsWithParent startSegment = null;
		CoordsWithParent endSegment = null;
		while (it.hasNext()) {
			CoordsWithParent a = b;
			b = it.next();
			// check if the segment is included in the polygon: check the
			// midpoint
			if (checkMidpoint(p, a, b)) {
				if (startSegment == null) {
					startSegment = a; // new start segment
				}
				endSegment = b; // extend segment to b
			} else {
				if (startSegment != null) { // add last correct segment
					addSegment(startSegment, endSegment, ret);
					startSegment = null;
				}
			}
		}

		if (startSegment != null) {
			addSegment(startSegment, endSegment, ret);
		}

		return ret;
	}

	private void addSegment(CoordsWithParent startSegment,
			CoordsWithParent endSegment, ArrayList<Segment> segmentList) {

		// add new segment to list
		segmentList.add(new Segment(startSegment, endSegment));

		// add map parent to polygon
		addParentToPolygons(startSegment.parent);
		addParentToPolygons(endSegment.parent);
	}

	private void addParentToPolygons(GeoElementND parent) {
		TreeSet<GeoPolygon> polygons = parentToPolygons.get(parent);
		if (polygons == null) {
			polygons = new TreeSet<>();
			parentToPolygons.put(parent, polygons);
		}
		polygons.add(p);
	}

	@SuppressWarnings("serial")
	private static class VerticesList extends ArrayList<ArrayList<Coords>> {

		protected int cumulateSize = 0;

		protected VerticesList() {
			// avoid synth access error
		}

		@Override
		public boolean add(ArrayList<Coords> vertices) {
			cumulateSize += vertices.size();
			return super.add(vertices);
		}

		@Override
		public void clear() {
			cumulateSize = 0;
			super.clear();
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj); // see EQ_DOESNT_OVERRIDE_EQUALS in SpotBugs
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

	}

	/**
	 * find next vertex linking the start point of the polygon with new
	 * intersection segment
	 * 
	 * @param p2
	 *            polygon
	 * @param startPoint
	 *            start point
	 * @param oldPoint
	 *            vertex before startPoint
	 * @return next vertex
	 */
	private CoordsWithParent nextVertex(GeoPolygon p2,
			CoordsWithParent startPoint, GeoElementND oldPoint) {

		// get intersection segments coords for this polygon
		segmentCoords = newCoordsList.get(p2);

		CoordsWithParent a;
		CoordsWithParent b = null;

		// Log.debug("\nstart parent:"+startPoint.parent+"\nold
		// parent:"+oldPoint.parent);

		// check if for a segment, one of the vertex as same parent as starting
		// vertex
		// then take the second point as next vertex
		boolean notFound = true;
		int i;
		for (i = 0; i < segmentCoords.size() && notFound; i++) {
			Segment segment = segmentCoords.get(i);
			a = segment.p1;
			if (a.parent == startPoint.parent) {
				b = segment.p2;
				// Log.debug("\ni:"+i+"\na:"+a.parent+"\nb:"+b.parent);
				if (b.parent != oldPoint) { // prevent immediate return
					notFound = false;
				}
			} else {
				b = a;
				a = segment.p2;
				if (a.parent == startPoint.parent) {
					// Log.debug("\ni:"+i+"\na:"+a.parent+"\nb:"+b.parent);
					if (b.parent != oldPoint) { // prevent immediate return
						notFound = false;
					}
				}
			}
		}

		if (notFound) {
			b = null;
		} else {
			// remove the segment found: not usable anymore
			// removeSegmentCoords(i-1,p2);
			removeSegmentCoordsIndex = i - 1;
			removeSegmentCoordsPolygon = p2;
		}

		return b;
	}

	private void removeSegmentCoords() {
		removeSegmentCoords(removeSegmentCoordsIndex,
				removeSegmentCoordsPolygon);
	}

	private void removeSegmentCoords(int index, GeoPolygon p2) {

		segmentCoords.remove(index);
		// Log.debug("\np2:"+p2+"\nsize="+segmentCoords.size());
		if (segmentCoords.size() == 0) {
			newCoordsList.remove(p2);
		}

	}

	/**
	 * find next vertex linking a vertex of the polyhedron to next segment
	 * 
	 * @param startPoint
	 *            start vertex
	 * @param oldParent
	 *            vertex before startPoint
	 * @return next vertex
	 */
	private CoordsWithParent nextVertex(CoordsWithParent startPoint,
			GeoElementND oldParent, GeoElementND firstParent) {

		CoordsWithParent b;
		CoordsWithParent bFirstPoint = null;
		GeoPolygon pFirstPoint = null;
		int indexFirstPoint = 0;

		// 1) try keep same poly (interior point)
		if (newCoordsList.containsKey(p)) {
			b = nextVertex(p, startPoint, oldParent);
			if (b != null) {
				if (b.parent == firstParent) { // we may try another face to get
												// greater polygon
					bFirstPoint = b;
					pFirstPoint = p;
					indexFirstPoint = removeSegmentCoordsIndex;
				} else {
					// Log.debug("same: "+startPoint.parent+" -- "+oldParent+" :
					// "+b);
					removeSegmentCoords();
					return b;
				}
			}
		}

		// 2) try other polygons
		TreeSet<GeoPolygon> polySet = getPolygons(startPoint);
		Iterator<GeoPolygon> it = polySet.iterator();
		GeoPolygon p2 = null;
		while (it.hasNext()) {
			p2 = it.next();
			// Log.debug("\np2:"+p2+"\np2==p:"+(p2==p)+"\nkey:"+newCoordsList.containsKey(p2));
			// find other polygon, contained as a key
			if (p2 != p && newCoordsList.containsKey(p2)) {
				// Log.debug("\npoly2:"+p2);
				// try to find next vertex
				b = nextVertex(p2, startPoint, oldParent);
				if (b != null) { // if found
					if (b.parent == firstParent) { // we may try another face to
													// get greater polygon
						bFirstPoint = b;
						pFirstPoint = p2;
						indexFirstPoint = removeSegmentCoordsIndex;
					} else { // this one is ok
						p = p2;
						removeSegmentCoords();
						// Log.debug("other: "+firstParent+" -- "+b.parent);
						return b;
					}
				}
			}
		}

		// 3) return same as first point
		if (bFirstPoint != null) {
			removeSegmentCoords(indexFirstPoint, pFirstPoint);
			p = pFirstPoint;
			return bFirstPoint;
		}

		// 4) return null: no next vertex
		return null;

	}

	/**
	 * Add vertices from one to the next
	 * 
	 * @return vertices list
	 */
	private Vertices addVertices() {

		Vertices vertices = new Vertices();

		// take first segment for the face p
		segmentCoords = newCoordsList.get(p);

		if (segmentCoords.isEmpty()) {
			// may occur when the plane goes through some edge
			newCoordsList.remove(p);
			return null;
		}

		// start with first point of the segment
		CoordsWithParent firstPoint = segmentCoords.get(0).p1;
		CoordsWithParent startPoint = segmentCoords.get(0).p2;
		removeSegmentCoords(0, p);
		vertices.add(firstPoint);
		// Log.debug("\na.parent:"+firstPoint.parent);//Log.debug("\n\n\n\n\n");
		// Log.debug("\nb.parent:"+startPoint.parent+"\npoly:"+p);//Log.debug("\n\n\n\n\n");
		// at first oldParent is null, so polygons A-B-A are possible
		GeoElementND oldParent = null;
		while (startPoint.parent != firstPoint.parent) {
			vertices.add(startPoint);
			CoordsWithParent c = nextVertex(startPoint, oldParent,
					firstPoint.parent);
			if (c == null) {
				return null;
			}
			oldParent = startPoint.parent;
			startPoint = c;
			// Log.debug("\nb.parent:"+startPoint.parent+"\npoly:"+p);//Log.debug("\n\n\n\n\n");
		}

		return vertices;

	}

	/**
	 * set polyhedron vertices as dummy polygons output
	 * 
	 * @param indexPolygon0
	 *            start index for polygons
	 * @param indexPoint0
	 *            start index for points
	 * @param indexSegment0
	 *            start index for segments
	 */
	private void addPolyhedronVerticesToOutput(int indexPolygon0,
			int indexPoint0, int indexSegment0) {
		int indexPolygon = indexPolygon0;
		int indexSegment = indexSegment0;
		int indexPoint = indexPoint0;
		for (Coords coords : polyhedronVertices) {
			GeoPolygon outputPoly = outputPolygons.getElement(indexPolygon);
			GeoPoint3D point = outputPoints.getElement(indexPoint);
			point.setCoords(coords);
			GeoSegment3D seg = outputSegmentsPolyhedron
					.getElement(indexSegment);
			seg.modifyInputPolyAndPoints(outputPoly, point, point);
			outputPoly.setPoints(new GeoPoint3D[] { point, point }, null,
					false); // don't
							// create
							// segments
			outputPoly.setSegments(new GeoSegment3D[] { seg, seg });
			outputPoly.calcArea();
			indexPolygon++;
			indexPoint++;
			indexSegment++;
		}
	}

	private void addToVerticesList(Vertices vertices) {
		verticesList.add(vertices);

		// remove polyhedron vertices for empty polygons for these points,
		// since these points are already on a polygon
		if (polyhedronVertices.size() > 0) {
			for (Coords coords : vertices) {
				polyhedronVertices.remove(coords);
			}
		}
	}

	private void updateLabels(
			@SuppressWarnings("rawtypes") OutputHandler outputHandler) {
		if (hasLabels) {
			outputHandler.updateLabels();
		}
	}

	@Override
	public void compute() {

		// set intersection vertices
		// (set it here since maybe some faces are included in the plane)
		if (verticesList == null) {
			verticesList = new VerticesList();
		} else {
			verticesList.clear();
		}

		if (checkVerticesList == null) {
			checkVerticesList = new TreeSet<>();
		} else {
			checkVerticesList.clear();
		}

		if (polyhedronFaces == null) {
			polyhedronFaces = new ArrayList<>();
		} else {
			polyhedronFaces.clear();
		}

		// set the point map
		setNewCoords();

		// Log.debug("\noriginalEdges:"+originalEdges);

		/*
		 * for (GeoElementND parent : parentToPolygons.keySet()){ Log.debug(
		 * "\nparent: " +parent+"\npolygons: "+parentToPolygons.get(parent)); }
		 */

		// Log.debug(polyhedronVertices);

		// set output
		if (newCoordsList.size() == 0 && polyhedronFaces.size() == 0) { // no
																		// intersection,
																		// no
																		// face
																		// contained
			// set points, segments and polygons equal to intersection with
			// polyhedron vertices
			outputPolygons.adjustOutputSize(polyhedronVertices.size(), false);
			outputPoints.adjustOutputSize(polyhedronVertices.size(), false);
			outputSegmentsPolyhedron.adjustOutputSize(polyhedronVertices.size(),
					false);
			addPolyhedronVerticesToOutput(0, 0, 0);

		} else {

			// start with one face, set a polygon, then get a new face, etc.
			while (newCoordsList.size() != 0) {
				// Log.debug(""+newCoordsList.keySet());
				p = newCoordsList.firstKey();
				Vertices vertices = addVertices();
				if (vertices != null) { // prevent not matching search
					vertices.setDirection();
					// check if this list has not already be computed
					if (checkVerticesList.add(vertices)) {
						addToVerticesList(vertices);
					}
				}
			}

			// add polyhedron faces contained in the plane
			for (Vertices vertices : polyhedronFaces) {
				// check if this list has not already be computed
				if (checkVerticesList.add(vertices)) {
					addToVerticesList(vertices);
				}
			}

			// Log.debug(newCoordsList.keySet());

			// set output points
			outputPoints.adjustOutputSize(
					verticesList.cumulateSize + polyhedronVertices.size(),
					false);
			updateLabels(outputPoints);
			int segmentIndex = 0;
			for (ArrayList<Coords> vertices : verticesList) {
				int length = vertices.size();
				for (int i = 0; i < length; i++) {
					GeoPoint3D point = outputPoints.getElement(segmentIndex);
					point.setCoords(vertices.get(i));
					segmentIndex++;
				}
			}

			// adjust output polygons size
			outputPolygons.adjustOutputSize(
					verticesList.size() + polyhedronVertices.size(), false);
			updateLabels(outputPolygons);

			// get points list
			GeoPoint3D[] points = new GeoPoint3D[verticesList.cumulateSize];
			points = outputPoints.getOutput(points);

			// set output segments and polygons
			outputSegmentsPolyhedron.adjustOutputSize(
					verticesList.cumulateSize + polyhedronVertices.size(),
					false);
			updateLabels(outputSegmentsPolyhedron);
			int pointIndex = 0;
			int polygonIndex = 0;
			segmentIndex = 0;
			for (ArrayList<Coords> vertices : verticesList) {
				int length = vertices.size();
				// Log.debug("polygonIndex: "+polygonIndex);
				GeoPolygon outputPoly = outputPolygons.getElement(polygonIndex);
				GeoPoint3D[] polyPoints = new GeoPoint3D[length];
				GeoSegment3D[] polySegments = new GeoSegment3D[length];
				for (int i = 0; i < length; i++) {
					// Log.debug(points[polygonOffset + i]);
					outputSegmentsPolyhedron.getElement(segmentIndex)
							.modifyInputPolyAndPoints(outputPoly,
									points[pointIndex + i],
									points[pointIndex + (i + 1) % length]);
					polyPoints[i] = points[pointIndex + i];
					polySegments[i] = outputSegmentsPolyhedron
							.getElement(segmentIndex);
					segmentIndex++;
				}

				// update polygon
				outputPoly.setPoints(polyPoints, null, false); // don't create
																// segments
				outputPoly.setSegments(polySegments);
				outputPoly.calcArea();

				pointIndex += length;
				polygonIndex++;
			}

			// add isolate polyhedron vertices
			addPolyhedronVerticesToOutput(polygonIndex, pointIndex,
					segmentIndex);

		}
	}

	@Override
	protected boolean checkParameter(double t1) {
		return true; // nothing to check here
	}

	@Override
	public final Commands getClassName() {
		return Commands.IntersectPath;
	}

	private final void createOutput() {

		outputPolygons = new OutputHandler<>(
				new ElementFactory<GeoPolygon3D>() {
					@Override
					public GeoPolygon3D newElement() {
						GeoPolygon3D p1 = new GeoPolygon3D(cons, true);
						p1.setParentAlgorithm(
								AlgoIntersectRegionPlanePolyhedron.this);
						if (outputPolygons.size() > 0) {
							p1.setAllVisualProperties(
									outputPolygons.getElement(0), false);
						}
						p1.setViewFlags(getFirstInput().getViewSet());
						p1.setVisibleInView3D(getFirstInput());
						p1.setVisibleInViewForPlane(getFirstInput());
						p1.setNotFixedPointsLength(true);
						p1.setOrthoNormalRegionCS();
						if (hasLabels) {
							p1.setInitLabelsCalled(true);
						}
						return p1;
					}
				});

		outputPolygons.adjustOutputSize(1, false);

		outputPoints = new OutputHandler<>(
				new ElementFactory<GeoPoint3D>() {
					@Override
					public GeoPoint3D newElement() {
						GeoPoint3D newPoint = new GeoPoint3D(cons);
						newPoint.setCoords(0, 0, 0, 1);
						newPoint.setParentAlgorithm(
								AlgoIntersectRegionPlanePolyhedron.this);
						newPoint.setAuxiliaryObject(true);
						newPoint.setViewFlags(getFirstInput().getViewSet());
						newPoint.setVisibleInView3D(getFirstInput());
						newPoint.setVisibleInViewForPlane(getFirstInput());

						int size = outputPoints.size();
						if (size > 0) { // check if at least one element is
										// visible
							boolean visible = false;
							boolean labelVisible = false;
							for (int i = 0; i < size && !visible
									&& !labelVisible; i++) {
								visible = visible || outputPoints.getElement(i)
										.isEuclidianVisible();
								labelVisible = labelVisible || outputPoints
										.getElement(i).getLabelVisible();
							}
							newPoint.setEuclidianVisible(visible);
							if (!visible) { // if not visible, we don't want
											// setParentAlgorithm() to change it
								newPoint.dontSetEuclidianVisibleBySetParentAlgorithm();
							}
							newPoint.setLabelVisible(labelVisible);
						}

						if (outputPolygons.size() > 0) {
							GeoPolygon polygon = outputPolygons.getElement(0);
							if (polygon.getShowObjectCondition() != null) {
								try {
									newPoint.setShowObjectCondition(
											polygon.getShowObjectCondition());
								} catch (Exception e) {
									// circular definition
								}
							}
						}

						return newPoint;
					}
				});

		outputPoints.adjustOutputSize(1, false);

		outputSegmentsPolyhedron = // createOutputSegments();
				new OutputHandler<>(
						new ElementFactory<GeoSegment3D>() {
							@Override
							public GeoSegment3D newElement() {
								GeoSegment3D segment = (GeoSegment3D) outputPolygons
										.getElement(0).createSegment(cons,
												outputPoints.getElement(0),
												outputPoints.getElement(0),
												true);
								segment.setAuxiliaryObject(true);
								// segment.setLabelVisible(showNewSegmentsLabels);
								segment.setViewFlags(
										getFirstInput().getViewSet());
								segment.setVisibleInView3D(getFirstInput());
								segment.setVisibleInViewForPlane(
										getFirstInput());
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

		// add output sizes (polygons, points, segments)
		sb.append("\t<outputSizes val=\"");
		sb.append(outputPolygons.size());
		sb.append(",");
		sb.append(outputPoints.size());
		sb.append(",");
		sb.append(outputSegmentsPolyhedron.size());
		sb.append("\"");
		sb.append("/>\n");

		// common method
		super.getCmdOutputXML(sb, tpl);

	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("IntersectionOfAandB",
				getFirstInput().getLabel(tpl), getSecondInput().getLabel(tpl));
	}

	/**
	 * comparator using Kernel precision (compare x, then y, then z, then ...)
	 */
	public static final Comparator<Coords> COORDS_COMPARATOR = new Comparator<Coords>() {

		@Override
		public int compare(Coords o1, Coords o) {
			// 1) check vectors lengths
			if (o1.val.length < o.val.length) {
				return -1;
			}
			if (o1.val.length > o.val.length) {
				return 1;
			}

			// 2) check if one value is lower
			for (int i = 0; i < o1.val.length; i++) {
				if (DoubleUtil.isGreater(o.val[i], o1.val[i])) {
					return -1;
				}
				if (DoubleUtil.isGreater(o1.val[i], o.val[i])) {
					return 1;
				}
			}

			// 3) vectors are equal
			return 0;
		}

	};
}
