package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoJoinPoints3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolygon3D;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoPolyhedronPoints;
import org.geogebra.common.geogebra3D.kernel3D.transform.MirrorableAtPlane;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionElementCycle;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathMover;
import org.geogebra.common.kernel.PathMoverGeneric;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.PathParameter;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.RegionParameters;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.geos.Dilateable;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.Traceable;
import org.geogebra.common.kernel.geos.Transformable;
import org.geogebra.common.kernel.geos.Translateable;
import org.geogebra.common.kernel.geos.properties.Auxiliary;
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoPolyhedronInterface;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.HasFaces;
import org.geogebra.common.kernel.kernelND.HasHeight;
import org.geogebra.common.kernel.kernelND.HasSegments;
import org.geogebra.common.kernel.kernelND.HasVolume;
import org.geogebra.common.kernel.kernelND.RotateableND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * @author ggb3D
 * 
 *         Class describing a GeoPolyhedron
 * 
 */
public class GeoPolyhedron extends GeoElement3D
		implements HasSegments, HasVolume, Traceable, RotateableND,
		Translateable, MirrorableAtPlane, Transformable, Dilateable, HasHeight,
		Path, GeoPolyhedronInterface, GeoNumberValue, Region, HasFaces {

	/** unknown */
	public static final int TYPE_UNKNOWN = 0;
	/** pyramid */
	public static final int TYPE_PYRAMID = 1;
	/** prism */
	public static final int TYPE_PRISM = 3;
	/** tetrahedron */
	public static final int TYPE_TETRAHEDRON = 4;
	/** cube */
	public static final int TYPE_CUBE = 5;
	/** octahedron */
	public static final int TYPE_OCTAHEDRON = 6;
	/** dodecahedron */
	public static final int TYPE_DODECAHEDRON = 7;
	/** icosahedron */
	public static final int TYPE_ICOSAHEDRON = 8;
	/** net */
	public static final int TYPE_NET = 9;
	/** one of the TYPE_* constants */
	int type;

	/** vertices */
	// protected ArrayList<GeoPoint3D> points;

	/** edges index */
	protected TreeMap<ConstructionElementCycle, Long> segmentsIndex;

	/** max faces edges */
	protected long segmentsIndexMax = 0;

	/** edges */
	protected TreeMap<Long, GeoSegment3D> segments;

	/** edges linked (e.g basis of the prism -- WARNING: not always updated) */
	private TreeMap<ConstructionElementCycle, GeoSegmentND> segmentsLinked;

	/** faces index */
	protected TreeMap<ConstructionElementCycle, Integer> polygonsIndex;
	/** faces descriptions */
	protected ArrayList<ConstructionElementCycle> polygonsDescriptions;

	/** max faces index */
	protected int polygonsIndexMax = 0;

	/** faces */
	protected TreeMap<Integer, GeoPolygon3D> polygons;

	/** faces linked */
	protected ArrayList<GeoPolygon> polygonsLinked;

	/** points created by the algo */
	protected ArrayList<GeoPoint3D> pointsCreated;

	/** face currently constructed */
	private ConstructionElementCycle currentFace;
	/**
	 * last face index (for pyramid/prism)
	 */
	private int topFaceIndex;
	private StringBuilder sb = new StringBuilder();
	private boolean allLabelsAreSet = false;
	static private Comparator<GeoPointND> pointIdComparator = null;
	private TreeMap<GeoPointND, GeoPoint3D> copyPoints;
	private boolean isDefined = true;

	private boolean trace;

	private double volume = Double.NaN;
	private double area = Double.NaN;
	/**
	 * oriented (positive or negative) height
	 */
	private double orientedHeight;

	private StringBuilder sbToString = new StringBuilder(50);

	private boolean childrenLabelsSet = false;

	/**
	 * Temporary points
	 */
	static public class DummyGeoPoint3D extends GeoPoint3D {

		private int index;

		/**
		 * constructor
		 * 
		 * @param c
		 *            construction
		 * @param index
		 *            index
		 */
		public DummyGeoPoint3D(Construction c, int index) {
			super(c);
			this.index = index;
		}

		/**
		 * 
		 * @return index for replacement
		 */
		public int getDummyIndex() {
			return index;
		}

	}

	/**
	 * Temporary segments
	 */
	static public class DummyGeoSegment3D extends GeoSegment3D {

		private int index;

		/**
		 * constructor
		 * 
		 * @param c
		 *            construction
		 * @param index
		 *            index
		 */
		public DummyGeoSegment3D(Construction c, int index) {
			super(c);
			this.index = index;
		}

		/**
		 * 
		 * @return index for replacement
		 */
		public int getDummyIndex() {
			return index;
		}

	}

	static private class PointChangedHelper {
		private Coords coordsOld;
		private GeoPoint3D point;
		private boolean useLine;
		private double minDistLine;
		private double minDist;
		private Coords result;
		private double t1;
		private double t2;
		private boolean isOnPath;
		private int index;
		private int resultIndex;
		private GeoPolygon resultPolygon;

		public PointChangedHelper(GeoPoint3D point) {
			this.point = point;
			coordsOld = Coords.createInhomCoorsInD3();
			coordsOld.set3(point.getInhomCoordsInD3());
			minDistLine = Double.POSITIVE_INFINITY;
			minDist = Double.POSITIVE_INFINITY;
			useLine = point.hasWillingCoords() && point.hasWillingDirection();
			index = 0;
		}

		public void update(GeoPolygon polygon) {
			point.setRegion(polygon);
			point.setCoords(coordsOld, false);
			polygon.pointChangedForRegion(point);
			if (useLine) {
				double distLine = point.getInhomCoords().distLine(
						point.getWillingCoords(), point.getWillingDirection());
				double dist = point.getInhomCoords().sub(point.getWillingCoords())
						.squareNorm();
				if (DoubleUtil.isGreater(minDistLine, distLine)) {
					update(distLine, dist, polygon);
				} else if (DoubleUtil.isEqual(minDistLine, distLine)) {
					if (dist < minDist) {
						update(dist, polygon);
					}
				}
			} else {
				double dist = point.getInhomCoords().sub(coordsOld).squareNorm();
				if (dist < minDist) {
					update(dist, polygon);
				}
			}
			index++;
		}

		private void update(double distLine, double dist, GeoPolygon polygon) {
			minDistLine = distLine;
			update(dist, polygon);
		}

		private void update(double dist, GeoPolygon polygon) {
			minDist = dist;
			result = point.getInhomCoords().copyVector();
			RegionParameters rp = point.getRegionParameters();
			t1 = rp.getT1();
			t2 = rp.getT2();
			isOnPath = rp.isOnPath();
			resultIndex = index;
			resultPolygon = polygon;
		}

		public void setResult() {
			point.setCoords(result, false);
			RegionParameters rp = point.getRegionParameters();
			rp.setT1(getNormalized(t1) + resultIndex);
			rp.setT2(t2);
			rp.setNormal(resultPolygon.getDirectionInD3());
			rp.setIsOnPath(isOnPath);
		}

		static public double getNormalized(double t) {
			return (PathNormalizer.inverseInfFunction(t) + 1) / 2;
		}

		static public double getUnNormalized(double t) {
			return PathNormalizer.infFunction(2 * t - 1);
		}
	}

	/**
	 * constructor
	 * 
	 * @param c
	 *            construction
	 * @param polyhedronType
	 *            polyhedron type
	 */
	public GeoPolyhedron(Construction c, int polyhedronType) {
		super(c);

		// needs to be done before setConstructionDefaults() as color depends on
		// type
		this.type = polyhedronType;

		// moved from GeoElement's constructor
		// must be called from the subclass, see
		// http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		polygonsIndex = new TreeMap<>();
		polygonsDescriptions = new ArrayList<>();
		polygons = new TreeMap<>();

		segmentsIndex = new TreeMap<>();
		segments = new TreeMap<>();

		segmentsLinked = new TreeMap<>();
		polygonsLinked = new ArrayList<>();

		pointsCreated = new ArrayList<>();
	}

	/**
	 * Update segments linked set with the polygon's segment
	 * 
	 * @param polygon
	 *            source polygon
	 */
	private void addSegmentsLinked(GeoPolygon polygon) {
		if (polygon.getSegments() != null) {
			for (GeoSegmentND segment : polygon.getSegments()) {
				addSegmentLinked(segment);
			}
		}
	}

	/**
	 * update set of segments linked to this
	 */
	public void updateSegmentsLinked() {
		segmentsLinked.clear();
		for (GeoPolygon p : getPolygonsLinked()) {
			addSegmentsLinked(p);
		}
	}

	/**
	 * 
	 * @return segments linked to the polyhedron (eg segments of the bottom)
	 */
	public Collection<GeoSegmentND> getSegmentsLinked() {
		return segmentsLinked.values();
	}

	/**
	 * 
	 * @return polygons linked to the polyhedron (eg the bottom)
	 */
	public Collection<GeoPolygon> getPolygonsLinked() {
		return polygonsLinked;
	}

	/**
	 * Copy constructor
	 * 
	 * @param polyhedron
	 *            original
	 */
	public GeoPolyhedron(GeoPolyhedron polyhedron) {
		this(polyhedron.getConstruction(), polyhedron.getPolyhedronType());
		set(polyhedron);
	}

	/**
	 * 
	 * @return the type of polyhedron
	 */
	public int getType() {
		return type;
	}

	/**
	 * start a new face
	 */
	public void startNewFace() {
		currentFace = new ConstructionElementCycle();
	}

	/**
	 * add the point to the current face and to the point list if it's a new one
	 * 
	 * @param point
	 *            vertex
	 */
	public void addPointToCurrentFace(GeoPointND point) {
		currentFace.add(point);
	}

	/**
	 * ends the current face and store it in the faces list
	 */
	public void endCurrentFace() {
		currentFace.setDirection();

		// Application.debug(polygonsIndexMax);

		// add to index
		polygonsIndex.put(currentFace, Integer.valueOf(polygonsIndexMax));
		polygonsDescriptions.add(currentFace);
		polygonsIndexMax++;

	}

	/**
	 * 
	 * @return index for polygon described by current constructing face (null if
	 *         not exists)
	 */
	public Integer getCurrentFaceIndex() {
		currentFace.setDirection();
		Integer index = polygonsIndex.get(currentFace);
		int ret;
		if (index == null) {
			ret = -1;
		} else {
			ret = index;
		}
		Log.debug(currentFace + ": " + ret);
		return index;
	}

	/**
	 * 
	 * @return current face descriptor
	 */
	public ConstructionElementCycle getCurrentFace() {
		return currentFace;
	}

	/**
	 * 
	 * @return constructed polygons indices
	 */
	public Collection<Integer> getPolygonsIndices() {
		return polygonsIndex.values();
	}

	/**
	 * says that current face created is the last face (for pyramid/prism) (warn
	 * : call it AFTER endCurrentFace())
	 */
	public void setCurrentFaceIsTopFace() {
		topFaceIndex = polygonsIndexMax - 1;
	}

	/**
	 * creates a polygon corresponding to the index
	 * 
	 * @param index
	 *            index of the polygon
	 * @return polygon corresponding
	 */
	public GeoPolygon3D createPolygon(int index) {
		currentFace = polygonsDescriptions.get(index);

		// vertices of the face
		GeoPointND[] p = new GeoPointND[currentFace.size()];

		// edges linked to the face
		GeoSegmentND[] s = new GeoSegmentND[currentFace.size()];

		GeoPointND endPoint = (GeoPointND) currentFace.get(0);
		p[0] = endPoint; // first point for the polygon
		GeoPointND firstPoint = endPoint;
		int j;
		for (j = 1; j < currentFace.size(); j++) {
			// creates edges
			GeoPointND startPoint = endPoint;
			endPoint = (GeoPointND) currentFace.get(j);
			s[j - 1] = createSegment(startPoint, endPoint);

			// points for the polygon
			p[j] = endPoint;

		}
		// last segment
		s[j - 1] = createSegment(endPoint, firstPoint);

		GeoPolygon3D polygon = createPolygon(p, index);
		polygon.setSegments(s);

		return polygon;
	}

	/**
	 * update the faces
	 */
	public void createFaces() {
		for (int index = 0; index < polygonsDescriptions.size(); index++) {
			createPolygon(index);
		}
	}

	/**
	 * create a polygon joining the given points
	 * 
	 * @param points
	 *            vertices of the polygon
	 * @param index
	 *            index in polygons map
	 * @return the polygon
	 */
	public GeoPolygon3D createPolygon(GeoPointND[] points, int index) {
		AlgoPolygon3D algo = new AlgoPolygon3D(cons, points, false, this);
		cons.removeFromConstructionList(algo);

		GeoPolygon3D polygon = (GeoPolygon3D) algo.getPoly();
		// refresh color to ensure polygons have same color as polyhedron:
		polygon.setObjColor(getObjectColor());
		polygon.setAlphaValue(getAlphaValue());
		polygon.setLineThickness(getLineThickness());
		polygon.setLineType(getLineType());

		// force init labels called to avoid polygon to draw edges
		polygon.setInitLabelsCalled(true);

		if (condShowObject != null) {
			try {
				polygon.setShowObjectCondition(getShowObjectCondition());
			} catch (Exception e) {
				// circular definition
			}
		}

		polygon.setAuxiliaryObject(Auxiliary.YES_DEFAULT);

		// put the polygon into the collection
		polygons.put(index, polygon);

		return polygon;
	}

	/**
	 * add the polygon as a polygon linked to this (e.g basis of a prism)
	 * 
	 * @param polygon
	 *            existing polygon
	 */
	public void addPolygonLinked(GeoPolygon polygon) {
		polygonsLinked.add(polygon);
		addSegmentsLinked(polygon);
		polygon.addMeta(this);

	}

	/**
	 * add the point as created point (by algo)
	 * 
	 * @param point
	 *            vertex
	 */
	public void addPointCreated(GeoPoint3D point) {
		pointsCreated.add(point);
	}

	/**
	 * return a segment joining startPoint and endPoint if this segment already
	 * exists in segments, return the already stored one
	 * 
	 * @param startPoint
	 *            the start point
	 * @param endPoint
	 *            the end point
	 * @return the segment
	 */

	public GeoSegmentND createSegment(GeoPointND startPoint,
			GeoPointND endPoint) {

		// Application.debug(startPoint.getLabel() + endPoint.getLabel());

		if (startPoint instanceof DummyGeoPoint3D
				&& endPoint instanceof DummyGeoPoint3D) {
			return new DummyGeoSegment3D(cons,
					((DummyGeoPoint3D) startPoint).getDummyIndex());
		}

		ConstructionElementCycle key = ConstructionElementCycle
				.segmentDescription((GeoElement) startPoint,
						(GeoElement) endPoint);

		// check if this segment is not already created
		if (segmentsIndex.containsKey(key)) {
			// Application.debug(startPoint.getLabel() + endPoint.getLabel());
			// App.error("segmentsIndex : "+key);
			return segments.get(segmentsIndex.get(key));
		}

		// check if this segment is not a segment linked
		if (segmentsLinked.containsKey(key)) {
			// App.error("segmentsLinked : "+key);
			return segmentsLinked.get(key);
		}

		return createNewSegment(startPoint, endPoint, key);

	}

	/**
	 * create new segment (if not already exists)
	 * 
	 * @param startPoint
	 *            start point
	 * @param endPoint
	 *            end point
	 * @param key
	 *            key for segment
	 * @return new segment
	 */
	protected GeoSegmentND createNewSegment(GeoPointND startPoint,
			GeoPointND endPoint, ConstructionElementCycle key) {

		// App.error("new segment : "+key);

		GeoSegment3D segment;

		AlgoJoinPoints3D algoSegment = new AlgoJoinPoints3D(cons, startPoint,
				endPoint, this, GeoClass.SEGMENT3D);
		cons.removeFromConstructionList(algoSegment);

		segment = (GeoSegment3D) algoSegment.getCS();
		// refresh color to ensure segments have same color as polyhedron:
		segment.setObjColor(getObjectColor());
		segment.setLineThickness(getLineThickness());
		segment.setLineType(getLineType());

		if (condShowObject != null) {
			try {
				segment.setShowObjectCondition(getShowObjectCondition());
			} catch (Exception e) {
				// circular definition
			}
		}

		segment.setAuxiliaryObject(Auxiliary.YES_DEFAULT);
		storeSegment(segment, key);

		return segment;

	}

	/**
	 * store the segment with the given key
	 * 
	 * @param segment
	 *            segment
	 * @param key
	 *            key
	 */
	protected void storeSegment(GeoSegment3D segment,
			ConstructionElementCycle key) {
		Long index = Long.valueOf(segmentsIndexMax);
		segmentsIndex.put(key, index);
		segments.put(index, segment);
		segmentsIndexMax++;
	}

	/**
	 * @param startPoint
	 *            segment start point
	 * @param endPoint
	 *            segment end point
	 * @return segment with given endpoints or null if not found
	 */
	public GeoSegmentND getSegment(GeoPointND startPoint, GeoPointND endPoint) {
		// Application.debug(startPoint.getLabel() + endPoint.getLabel());

		ConstructionElementCycle key = ConstructionElementCycle
				.segmentDescription((GeoElement) startPoint,
						(GeoElement) endPoint);

		// check if this segment is already created
		if (segmentsIndex.containsKey(key)) {
			return segments.get(segmentsIndex.get(key));
		}

		// check if this segment is a segment linked
		if (segmentsLinked.containsKey(key)) {
			return segmentsLinked.get(key);
		}

		return null;
	}

	/**
	 * @param segment
	 *            existing segment
	 */
	public void addSegmentLinked(GeoSegmentND segment) {
		ConstructionElementCycle key = ConstructionElementCycle
				.segmentDescription(segment.getStartPointAsGeoElement(),
						segment.getEndPointAsGeoElement());

		// Log.debug("linked : "+key);
		segmentsLinked.put(key, segment);
	}

	/**
	 * @param labels
	 *            output labels, only first one is used
	 */
	public void defaultLabels(String[] labels) {
		if (cons.isSuppressLabelsActive()) { // for redefine
			return;
		}

		if (labels == null || labels.length == 0) {
			setLabel(null);
		} else {

			setLabel(labels[0]);
		}

		defaultPolygonsLabels();
		defaultSegmentLabels();

	}

	/**
	 * Returns whether the method initLabels() was called for this polygon. This
	 * is important to know whether the segments have gotten labels.
	 * 
	 * @return true iff all labels (of created polygons, segments, points) are
	 *         set.
	 */
	final public boolean allLabelsAreSet() {
		return allLabelsAreSet;
	}

	/**
	 * set init labels called
	 * 
	 * @param flag
	 *            flag for labels
	 */
	public void setAllLabelsAreSet(boolean flag) {
		allLabelsAreSet = flag;
	}

	/**
	 * Inits the labels of this polyhedron, its faces and edges. labels[0] for
	 * polyhedron itself, labels[1..n] for faces and edges,
	 * 
	 * @param labels
	 *            labels for this, points, faces, edges
	 */
	public void initLabels(String[] labels) {
		// Application.printStacktrace("");

		if (cons.isSuppressLabelsActive()) { // for redefine
			return;
		}
		if (labels == null || labels.length == 0) {
			initLabels(new String[1]);
			return;
		}
		setAllLabelsAreSet(true);

		/*
		 * String s="labels:\n"; for (int i=0; i<labels.length; i++)
		 * s+=labels[i]+"\n"; s+="points: "+pointsCreated.size()+"\npolygons: "
		 * +polygons .size()+"\nsegments: "+segments.size();
		 * Application.debug(s);
		 */

		// first label for polyhedron itself
		setLabel(labels[0]);

		int index = 1;

		// labels for created points
		if (labels.length - index < pointsCreated.size()) {
			defaultPointsLabels();
			defaultPolygonsLabels();
			defaultSegmentLabels();
			return;
		}

		for (GeoPoint3D point : pointsCreated) {
			point.setLabel(labels[index]);
			index++;
		}

		// labels for polygons
		if (labels.length - index < polygons.size()) {
			defaultPolygonsLabels();
			defaultSegmentLabels();
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setLabel(labels[index]);
			// Application.debug("labels["+index+"]="+labels[index]);
			index++;
		}

		// labels for segments
		if (labels.length - index < segments.size()) {
			defaultSegmentLabels();
			return;
		}

		// labels for segments
		for (GeoSegment3D segment : segments.values()) {
			segment.setLabel(labels[index]);
			index++;
		}

	}

	private void defaultPointsLabels() {
		for (GeoPointND point : pointsCreated) {
			point.setLabel(null);
		}
	}

	/**
	 * @param geo
	 *            element
	 * @return level of usability of the label
	 */
	private static int usableLabel(GeoElement geo) {
		if (!geo.isLabelSet()) {
			return 2; // not usable
		} else if (geo.getLabel(StringTemplate.defaultTemplate).contains("_")) {
			return 2; // not usable
		}
		else {
			return 0; // usable
		}

	}

	private void defaultPolygonsLabels() {
		for (Entry<ConstructionElementCycle, Integer> entry : polygonsIndex
				.entrySet()) {

			ConstructionElementCycle key = entry.getKey();

			// stores points names and find the first
			String label1 = null;
			int labelUsability = 0;

			String[] points = new String[key.size()];
			int indexFirstPointName = 0;
			int i = 0;
			for (Iterator<GeoElementND> it = key.iterator(); it.hasNext()
					&& (labelUsability < 2);) {
				GeoElement p = (GeoElement) it.next();
				labelUsability += usableLabel(p);
				if (labelUsability < 2) {
					points[i] = p.getLabel(StringTemplate.defaultTemplate);
					if (points[i].compareToIgnoreCase(
							points[indexFirstPointName]) < 0) {
						indexFirstPointName = i;
					}
					i++;
				}
			}

			if (labelUsability < 2) {

				sb.setLength(0);
				sb.append(getLoc().getMenu("Name.face"));

				// sets the direction to the next first name
				int indexSecondPointPlus = indexFirstPointName + 1;
				if (indexSecondPointPlus == points.length) {
					indexSecondPointPlus = 0;
				}
				int indexSecondPointMinus = indexFirstPointName - 1;
				if (indexSecondPointMinus == -1) {
					indexSecondPointMinus = points.length - 1;
				}

				if (points[indexSecondPointPlus].compareToIgnoreCase(
						points[indexSecondPointMinus]) < 0) {
					for (int j = indexFirstPointName; j < points.length; j++) {
						sb.append(points[j]);
					}
					for (int j = 0; j < indexFirstPointName; j++) {
						sb.append(points[j]);
					}
				} else {
					for (int j = indexFirstPointName; j >= 0; j--) {
						sb.append(points[j]);
					}
					for (int j = points.length
							- 1; j > indexFirstPointName; j--) {
						sb.append(points[j]);
					}
				}

				label1 = sb.toString();
			}

			polygons.get(entry.getValue()).setLabel(label1);
		}
	}

	private void defaultSegmentLabels() {
		for (Entry<ConstructionElementCycle, Long> entry : segmentsIndex
				.entrySet()) {

			ConstructionElementCycle key = entry.getKey();

			int labelUsability = 0;
			String label1 = null;

			String[] points = new String[2];
			int i = 0;
			for (Iterator<GeoElementND> it = key.iterator(); it.hasNext()
					&& (labelUsability < 2);) {
				GeoElement p = (GeoElement) it.next();
				labelUsability += usableLabel(p);
				if (labelUsability < 2) {
					points[i] = p.getLabel(StringTemplate.defaultTemplate);
					i++;
				}
			}

			if (labelUsability < 2) {

				sb.setLength(0);
				sb.append(getLoc().getMenu("Name.edge"));
				// sets the points names in order
				if (points[0].compareToIgnoreCase(points[1]) < 0) {
					sb.append(points[0]);
					sb.append(points[1]);
				} else {
					sb.append(points[1]);
					sb.append(points[0]);
				}

				label1 = sb.toString();
			}

			segments.get(entry.getValue()).setLabel(label1);
		}
	}

	@Override
	public GeoSegmentND[] getSegments() {
		GeoSegmentND[] ret = new GeoSegmentND[segmentsLinked.size()
				+ segments.size()];
		int i = 0;
		for (GeoSegmentND segment : segmentsLinked.values()) {
			ret[i] = segment;
			i++;
		}
		for (GeoSegment3D segment : segments.values()) {
			ret[i] = segment;
			i++;
		}
		return ret;
	}

	/**
	 * @return polyhedron's edges
	 */
	public GeoSegment3D[] getSegments3D() {
		GeoSegment3D[] ret = new GeoSegment3D[segments.size()];
		int i = 0;
		for (GeoSegment3D segment : segments.values()) {
			ret[i] = segment;
			i++;
		}
		return ret;
	}

	/**
	 * @return polyhedron's faces
	 */
	public GeoPolygon[] getFaces() {
		GeoPolygon[] polygonsArray = new GeoPolygon[polygonsLinked.size()
				+ polygons.size()];
		int index = 0;
		for (GeoPolygon polygon : polygonsLinked) {
			polygonsArray[index] = polygon;
			index++;
		}
		for (GeoPolygon polygon : polygons.values()) {
			polygonsArray[index] = polygon;
			index++;
		}

		return polygonsArray;
	}

	/**
	 * @return polyhedron's faces
	 */
	public GeoPolygon3D[] getFaces3D() {
		GeoPolygon3D[] polygonsArray = new GeoPolygon3D[polygons.size()];
		int index = 0;
		for (GeoPolygon3D polygon : polygons.values()) {
			polygonsArray[index] = polygon;
			index++;
		}

		return polygonsArray;
	}

	/**
	 * @return faces as collection
	 */
	public Collection<GeoPolygon3D> getFacesCollection() {
		return polygons.values();
	}

	@Override
	public GeoPolygon getFace(int index) {
		int polygonsLinkedSize = polygonsLinked.size();
		if (index < polygonsLinkedSize) {
			return polygonsLinked.get(index);
		}
		return polygons.get(index - polygonsLinkedSize);
	}

	@Override
	public int getFacesSize() {
		return polygonsLinked.size() + polygons.size();
	}

	/**
	 * @param index
	 *            index
	 * @return face
	 */
	public GeoPolygon3D getFace3D(int index) {
		return polygons.get(index);
	}

	/**
	 * 
	 * @return collection of polygons created by this
	 */
	public Collection<GeoPolygon3D> getPolygons() {
		return polygons.values();
	}

	/**
	 * set all polygons to reverse normals (for 3D drawing)
	 * 
	 * @param flag
	 *            flag
	 */
	public void setReverseNormalsForDrawing(boolean flag) {
		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setReverseNormalForDrawing(flag);
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setReverseNormalForDrawing(flag);
		}
	}

	/**
	 * set all polygons to reverse normals
	 * 
	 * @param flag
	 *            flag
	 */
	public void setReverseNormals(boolean flag) {
		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setReverseNormal(flag);
		}
		setReverseNormalsForDrawing(flag);
	}

	/**
	 * set all polygons to reverse normals
	 */
	public void setReverseNormals() {
		setReverseNormals(true);
	}

	@Override
	public void setEuclidianVisible(boolean visible) {
		super.setEuclidianVisible(visible);

		if (cons.isFileLoading()) {
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setEuclidianVisible(visible, false);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setEuclidianVisible(visible, false);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setEuclidianVisible(visible);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			segment.setEuclidianVisible(visible);
		}
	}

	@Override
	public void setObjColor(GColor color) {
		super.setObjColor(color);

		if (cons.isFileLoading()) {
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setObjColor(color);
			polygon.updateVisualStyle(GProperty.COLOR);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setObjColor(color);
			polygon.updateVisualStyle(GProperty.COLOR);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setObjColor(color);
			segment.updateVisualStyle(GProperty.COLOR);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			segment.setObjColor(color);
			segment.updateVisualStyle(GProperty.COLOR);
		}

		getKernel().notifyRepaint();
	}

	@Override
	public void removeColorFunction() {
		if (getColorFunction() == null) {
			return;
		}
		super.removeColorFunction();

		if (polygons == null || cons.isFileLoading()) {
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.removeColorFunction();
			polygon.updateVisualStyle(GProperty.COLOR);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.removeColorFunction();
			polygon.updateVisualStyle(GProperty.COLOR);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.removeColorFunction();
			segment.updateVisualStyle(GProperty.COLOR);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((GeoElement) segment).removeColorFunction();
			segment.updateVisualStyle(GProperty.COLOR);
		}

		getKernel().notifyRepaint();
	}

	@Override
	public void setColorFunction(final GeoList col) {
		super.setColorFunction(col);

		if (polygons == null || cons.isFileLoading()) {
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setColorFunction(col);
			polygon.updateVisualStyle(GProperty.COLOR);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setColorFunction(col);
			polygon.updateVisualStyle(GProperty.COLOR);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setColorFunction(col);
			segment.updateVisualStyle(GProperty.COLOR);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((GeoElement) segment).setColorFunction(col);
			segment.updateVisualStyle(GProperty.COLOR);
		}

		getKernel().notifyRepaint();

	}

	@Override
	public void setLineType(int type) {
		super.setLineType(type);

		if (polygons == null || cons.isFileLoading()) {
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setLineType(type, false);
			polygon.updateVisualStyle(GProperty.LINE_STYLE);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setLineType(type, false);
			polygon.updateVisualStyle(GProperty.LINE_STYLE);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setLineType(type);
			segment.updateVisualStyle(GProperty.LINE_STYLE);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((GeoElement) segment).setLineType(type);
			segment.updateVisualStyle(GProperty.LINE_STYLE);
		}

	}

	@Override
	public void setLayer(int layer2) {
		super.setLayer(layer2);

		if (polygons == null || cons.isFileLoading()) {
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setLayer(layer2);
			polygon.updateVisualStyle(GProperty.LAYER);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setLayer(layer2);
			polygon.updateVisualStyle(GProperty.LAYER);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setLayer(layer2);
			segment.updateVisualStyle(GProperty.LAYER);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((GeoElement) segment).setLayer(layer2);
			segment.updateVisualStyle(GProperty.LAYER);
		}
	}

	@Override
	public void setLineTypeHidden(int type) {
		super.setLineTypeHidden(type);

		if (polygons == null || cons.isFileLoading()) {
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setLineTypeHidden(type, false);
			polygon.updateVisualStyle(GProperty.LINE_STYLE);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setLineTypeHidden(type, false);
			polygon.updateVisualStyle(GProperty.LINE_STYLE);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setLineTypeHidden(type);
			segment.updateVisualStyle(GProperty.LINE_STYLE);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((GeoElement) segment).setLineTypeHidden(type);
			segment.updateVisualStyle(GProperty.LINE_STYLE);
		}

	}

	@Override
	public void setLineThickness(int th) {
		super.setLineThickness(th);

		if (polygons == null || cons.isFileLoading()) {
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setLineThickness(th, false);
			polygon.update();
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setLineThickness(th, false);
			polygon.updateVisualStyle(GProperty.LINE_STYLE);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setLineThickness(th);
			segment.updateVisualStyle(GProperty.LINE_STYLE);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			segment.setLineThickness(th);
			segment.updateVisualStyle(GProperty.LINE_STYLE);
		}

		updateVisualStyle(GProperty.LINE_STYLE);
	}

	@Override
	public void setLineThicknessOrVisibility(int th) {
		super.setLineThickness(th);

		if (polygons == null || cons.isFileLoading()) {
			return;
		}
		GProperty prop = th > 0 ? GProperty.LINE_STYLE : GProperty.COMBINED;
		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setLineThickness(th, false);
			polygon.update();
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setLineThickness(th, false);
			polygon.updateVisualStyle(prop);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setLineThicknessOrVisibility(th);
			segment.updateVisualStyle(prop);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((GeoElement) segment).setLineThicknessOrVisibility(th);
			segment.updateVisualStyle(prop);
		}
	}

	@Override
	public void setAlphaValue(double alpha) {
		super.setAlphaValue(alpha);

		if (cons.isFileLoading()) {
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setAlphaValue(alpha);
			polygon.updateVisualStyle(GProperty.COLOR);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setAlphaValue(alpha);
			polygon.updateVisualStyle(GProperty.COLOR);
		}

		getKernel().notifyRepaint();

	}

	@Override
	public GeoElement copy() {
		return new GeoPolyhedron(this);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.POLYHEDRON;
	}

	@Override
	public String getTypeString() {
		switch (type) {
		case TYPE_PRISM:
			return "Prism";
		case TYPE_PYRAMID:
			return "Pyramid";

		case TYPE_TETRAHEDRON:
			return "Tetrahedron";
		case TYPE_CUBE:
			return "Cube";
		case TYPE_OCTAHEDRON:
			return "Octahedron";
		case TYPE_DODECAHEDRON:
			return "Dodecahedron";
		case TYPE_ICOSAHEDRON:
			return "Icosahedron";

		default:
			return "Polyhedron";
		}
	}

	/**
	 * 
	 * @return polyhedron type
	 */
	public int getPolyhedronType() {
		return type;
	}

	@Override
	public boolean isDefined() {
		return isDefined;
	}

	@Override
	public boolean isEqual(GeoElementND Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElementND geo) {
		// use instanceof to support Net too
		if (geo instanceof GeoPolyhedron) {
			GeoPolyhedron polyhedron = (GeoPolyhedron) geo;

			isDefined = polyhedron.isDefined;

			// global
			type = polyhedron.type;
			setVolume(polyhedron.getVolume());
			setArea(polyhedron.getArea());
			setOrientedHeight(polyhedron.getOrientedHeight());

			topFaceIndex = polyhedron.topFaceIndex;
			if (!polyhedron.polygonsLinked.isEmpty()) {
				// in this case, polyhedron has a bottom face linked
				topFaceIndex++;
			}

			// init copy points list
			if (copyPoints == null) {
				if (pointIdComparator == null) {
					pointIdComparator = new Comparator<GeoPointND>() {
						@Override
						public int compare(GeoPointND o1, GeoPointND o2) {
							if (o1.getID() < o2.getID()) {
								return -1;
							}
							if (o1.getID() > o2.getID()) {
								return 1;
							}
							return 0;
						}
					};
				}
				copyPoints = new TreeMap<>(
						pointIdComparator);
			}

			// set segments
			int index = 0;
			for (GeoSegmentND s : polyhedron.segmentsLinked.values()) {
				if (setSegment(index, s)) {
					index++;
				}
			}
			for (GeoSegment3D s : polyhedron.segments.values()) {
				if (setSegment(index, s)) {
					index++;
				}
			}

			// set last segments undefined
			if (!segments.isEmpty()) {
				for (int i = index; i <= segments.lastKey(); i++) {
					segments.get((long) i).setUndefined();
				}
			}

			// set polygons
			index = 0;
			for (GeoPolygon p : polyhedron.polygonsLinked) {
				if (setPolygon(index, p)) {
					index++;
				}
			}
			for (GeoPolygon p : polyhedron.polygons.values()) {
				if (setPolygon(index, p)) {
					index++;
				}
			}

			// set points values
			for (Map.Entry<GeoPointND, GeoPoint3D> entry : copyPoints
					.entrySet()) {
				// set copy point to original point
				entry.getValue().set(entry.getKey());
			}

			AlgoElement algo = getParentAlgorithm();
			if (!(algo instanceof AlgoTransformation)) {
				// we need it e.g. for polyhedron0 = polyhedron, for lists
				updatePolygonsAndSegmentsAlgos();
			}

		}
	}

	private GeoPoint3D getCopyPoint(GeoPointND point) {
		GeoPoint3D copyPoint = copyPoints.get(point);
		if (copyPoint == null) {
			copyPoint = new GeoPoint3D(point);
			copyPoints.put(point, copyPoint);
		}
		return copyPoint;
	}

	private boolean setPolygon(int index, GeoPolygon p) {

		if (!p.isDefined()) {
			return false;
		}

		int pointsLength = p.getPointsLength();

		if (pointsLength == 0) {
			return false;
		}

		// set list of points
		GeoPoint3D[] pointsList = new GeoPoint3D[pointsLength];
		int i = 0;
		for (GeoPointND point : p.getPointsND()) {
			GeoPoint3D copyPoint = getCopyPoint(point);
			pointsList[i] = copyPoint;
			i++;
		}

		// set polygon
		GeoPolygon3D poly = polygons.get(index);
		if (poly == null) {
			startNewFace();
			for (GeoPointND point : pointsList) {
				addPointToCurrentFace(point);
			}
			endCurrentFace();
			createPolygon(index);
		} else {
			poly.modifyInputPoints(pointsList);
		}

		return true;
	}

	private boolean setSegment(long index, GeoSegmentND s) {

		if (!s.isDefined()) {
			return false;
		}

		GeoPoint3D startPoint = getCopyPoint(s.getStartPoint());
		GeoPoint3D endPoint = getCopyPoint(s.getEndPoint());
		ConstructionElementCycle key = ConstructionElementCycle
				.segmentDescription(startPoint, endPoint);

		if (index >= segmentsIndexMax) {
			// seg = (GeoSegment3D)
			createNewSegment(startPoint, endPoint, key);
		} else {
			GeoSegment3D seg = segments.get(index);
			seg.modifyInputPoints(startPoint, endPoint);
			segmentsIndex.put(key, index);
		}

		return true;
	}

	@Override
	public void setUndefined() {
		isDefined = false;

		volume = Double.NaN;

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setUndefined();
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setUndefined();
		}
	}

	/**
	 * Set defined flag to true
	 */
	public void setDefined() {
		isDefined = true;
	}

	@Override
	protected boolean showInEuclidianView() {

		return isDefined();
	}

	@Override
	public boolean isGeoPolyhedron() {
		return true;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return kernel.format(getVolume(), tpl);
	}

	@Override
	public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format(getVolume(), tpl));
		return sbToString.toString();
	}

	@Override
	public String toStringMinimal(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(regrFormat(getVolume()));
		return sbToString.toString();
	}

	/** to be able to fill it with an alpha value */
	@Override
	public boolean isFillable() {
		return true;
	}

	@Override
	protected void getXMLtags(StringBuilder sbXml) {
		getLineStyleXML(sbXml);
		super.getXMLtags(sbXml);
	}

	// /////////////////////////////////////////
	// GeoElement3DInterface

	@Override
	public Coords getLabelPosition() {
		return Coords.O; // TODO
	}

	@Override
	public void remove() {
		for (GeoPolygon polygon : polygonsLinked) {
			polygon.removeMeta(this);
		}

		// prevent from removing this when redefine a prism (see
		// AlgoJoinPoints3D and AlgoPolygon)
		if (this != getConstruction().getKeepGeo()) {
			super.remove();
		}
	}

	// //////////////////////////
	// VOLUME
	// //////////////////////////

	/**
	 * sets the volume
	 * 
	 * @param volume
	 *            volume
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}

	@Override
	public double getVolume() {
		return volume;
	}

	@Override
	public boolean hasFiniteVolume() {
		return isDefined();
	}

	// //////////////////////////
	// AREA
	// //////////////////////////

	/**
	 * sets the area (total area of the faces)
	 * 
	 * @param area
	 *            area
	 */
	public void setArea(double area) {
		this.area = area;
	}

	/**
	 * Note : recalc area when from pyramid/prism algo
	 * 
	 * @return area
	 */
	public double getArea() {
		// if parent algo is prism/pyramid, update area from faces
		AlgoElement algo = getParentAlgorithm();
		if (algo instanceof AlgoPolyhedronPoints) {
			area = 0;

			for (GeoPolygon p : polygonsLinked) {
				area += p.getArea();
			}
			for (GeoPolygon p : polygons.values()) {
				if (p.isDefined()) {
					area += p.getArea();
				}
			}
		}

		return area;
	}

	/**
	 * @return true when defined
	 */
	public boolean hasFiniteArea() {
		return isDefined();
	}

	// ////////////////
	// TRACE
	// ////////////////

	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
	public boolean getTrace() {
		return trace;
	}

	@Override
	public void setTrace(boolean trace) {
		this.trace = trace;

		if (polygons == null) {
			return;
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setTrace(trace);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setTrace(trace);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setTrace(trace);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((Traceable) segment).setTrace(trace);
		}

		getKernel().notifyRepaint();
	}

	// ////////////////////////////////
	// TRANSFORM
	// ////////////////////////////////

	@Override
	public void rotate(NumberValue r, GeoPointND S) {
		for (GeoPoint3D point : copyPoints.values()) {
			if (point.isDefined()) {
				point.rotate(r, S);
			}
		}

		updatePolygonsAndSegmentsAlgos();
	}

	@Override
	public void rotate(NumberValue r) {
		for (GeoPoint3D point : copyPoints.values()) {
			if (point.isDefined()) {
				point.rotate(r);
			}
		}

		updatePolygonsAndSegmentsAlgos();

	}

	@Override
	public void rotate(NumberValue r, GeoPointND S,
			GeoDirectionND orientation) {

		for (GeoPoint3D point : copyPoints.values()) {
			if (point.isDefined()) {
				point.rotate(r, S, orientation);
			}
		}

		updatePolygonsAndSegmentsAlgos();
	}

	@Override
	public void rotate(NumberValue r, GeoLineND line) {
		for (GeoPoint3D point : copyPoints.values()) {
			if (point.isDefined()) {
				point.rotate(r, line);
			}
		}

		updatePolygonsAndSegmentsAlgos();

	}

	@Override
	final public boolean isTranslateable() {
		return true;
	}

	@Override
	public void translate(Coords v) {
		for (GeoPoint3D point : copyPoints.values()) {
			if (point.isDefined()) {
				point.translate(v);
			}
		}

		updatePolygonsAndSegmentsAlgos();
	}

	private void updatePolygonsAndSegmentsAlgos() {
		for (GeoSegment3D seg : segments.values()) {
			seg.getParentAlgorithm().update();
		}

		for (GeoPolygon3D p : polygons.values()) {
			p.getParentAlgorithm().update();
		}
	}

	// //////////////////////
	// MIRROR
	// //////////////////////

	@Override
	public void mirror(Coords Q) {
		for (GeoPoint3D point : copyPoints.values()) {
			if (point.isDefined()) {
				point.mirror(Q);
			}
		}

		updatePolygonsAndSegmentsAlgos();
	}

	@Override
	public void mirror(GeoLineND g) {
		for (GeoPoint3D point : copyPoints.values()) {
			if (point.isDefined()) {
				point.mirror(g);
			}
		}

		updatePolygonsAndSegmentsAlgos();
	}

	@Override
	public void mirror(GeoCoordSys2D plane) {
		for (GeoPoint3D point : copyPoints.values()) {
			if (point.isDefined()) {
				point.mirror(plane);
			}
		}

		updatePolygonsAndSegmentsAlgos();
	}

	// //////////////////////
	// DILATE
	// //////////////////////

	@Override
	public void dilate(NumberValue rval, Coords S) {
		for (GeoPoint3D point : copyPoints.values()) {
			if (point.isDefined()) {
				point.dilate(rval, S);
			}
		}

		updatePolygonsAndSegmentsAlgos();

		double r = rval.getDouble();
		double rAbs = Math.abs(r);
		volume *= rAbs * rAbs * rAbs;
		area *= rAbs * rAbs;
		orientedHeight *= r;

	}

	/**
	 * set oriented (positive or negative) height
	 * 
	 * @param height
	 *            height
	 */
	public void setOrientedHeight(double height) {
		orientedHeight = height;
	}

	@Override
	public double getOrientedHeight() {
		return orientedHeight;
	}

	/**
	 * 
	 * @return bottom face (for pyramid & prism)
	 */
	public GeoPolygon getBottomFace() {
		if (polygonsLinked.isEmpty()) {
			return polygons.get(0);
		}
		return polygonsLinked.get(0);
	}

	/**
	 * 
	 * @return last face (for pyramid/prism)
	 */
	public GeoPolygon getTopFace() {
		return polygons.get(topFaceIndex);
	}

	/**
	 * 
	 * @return first side face (for prism)
	 */
	public GeoPolygon getFirstSideFace() {
		if (polygonsLinked.isEmpty()) {
			return polygons.get(1);
		}
		return polygons.get(0);
	}

	/**
	 * 
	 * @return top point (for pyramid)
	 */
	public Coords getTopPoint() {
		GeoPolygon p = getFirstSideFace();
		return p.getPoint3D(p.getPointsLength() - 1);

	}

	// /////////////////////////////////
	// Path interface

	@Override
	public boolean isPath() {
		return true;
	}

	@Override
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	@Override
	public double getMaxParameter() {
		return segmentsLinked.size() + segments.size();
	}

	@Override
	public double getMinParameter() {
		return 0;
	}

	@Override
	public boolean isClosedPath() {
		return true;
	}

	@Override
	public void pathChanged(GeoPointND PI) {
		// if kernel doesn't use path/region parameters, do as if point changed
		// its coords
		if (!getKernel().usePathAndRegionParameters(PI)) {
			pointChanged(PI);
			return;
		}

		// TODO remove that
		if (!(PI instanceof GeoPoint3D)) {
			return;
		}

		GeoPoint3D P = (GeoPoint3D) PI;

		PathParameter pp = P.getPathParameter();

		// remember old parameter
		double oldT = pp.getT();

		// find the segment where the point lies
		int index = (int) pp.getT();
		GeoSegmentND seg;
		if (index < segmentsLinked.size()) {
			seg = (GeoSegmentND) segmentsLinked.values().toArray()[index];
		} else {
			seg = (GeoSegmentND) segments.values().toArray()[index
					- segmentsLinked.size()];
		}

		// sets the path parameter for the segment, calc the new position of the
		// point
		pp.setT(pp.getT() - index);
		seg.pathChanged(P);

		// Log.debug(seg+" , "+oldT);

		// recall the old parameter
		pp.setT(oldT);
	}

	@Override
	public void pointChanged(GeoPointND PI) {
		// TODO remove that
		if (!(PI instanceof GeoPoint3D)) {
			return;
		}

		GeoPoint3D P = (GeoPoint3D) PI;

		Coords coordsOld = P.getInhomCoords().copyVector();

		// prevent from region bad coords calculations
		Region region = P.getRegion();
		P.setRegion(null);

		double minDist = Double.POSITIVE_INFINITY;
		Coords res = null;
		double param = 0;

		// find closest point on each segment
		PathParameter pp = P.getPathParameter();
		int i = 0;
		for (GeoSegmentND segment : segmentsLinked.values()) {

			P.setCoords(coordsOld, false); // prevent circular path.pointChanged

			if (segment.isDefined()) {
				segment.pointChanged(P);
			}

			double dist; // = P.getInhomCoords().sub(coordsOld).squareNorm();
			// double dist = 0;
			if (P.hasWillingCoords() && P.hasWillingDirection()) {
				dist = P.getInhomCoords().distLine(P.getWillingCoords(),
						P.getWillingDirection());
			} else {
				dist = P.getInhomCoords().sub(coordsOld).squareNorm();
			}

			if (dist < minDist) {
				minDist = dist;
				// remember closest point
				res = P.getInhomCoords().copyVector();
				param = i + pp.getT();
				// Application.debug(i);
			}

			i++;
		}

		for (GeoSegmentND segment : segments.values()) {

			P.setCoords(coordsOld, false); // prevent circular path.pointChanged

			if (segment.isDefined()) {
				segment.pointChanged(P);
			}

			double dist; // = P.getInhomCoords().sub(coordsOld).squareNorm();
			// double dist = 0;
			if (P.hasWillingCoords() && P.hasWillingDirection()) {
				dist = P.getInhomCoords().distLine(P.getWillingCoords(),
						P.getWillingDirection());
			} else {
				dist = P.getInhomCoords().sub(coordsOld).squareNorm();
			}

			if (dist < minDist) {
				minDist = dist;
				// remember closest point
				res = P.getInhomCoords().copyVector();
				param = i + pp.getT();
				// Application.debug(i);
			}

			i++;
		}

		P.setCoords(res, false);
		pp.setT(param);

		P.setRegion(region);
	}

	@Override
	public boolean isOnPath(GeoPointND P, double eps) {
		if (P.getPath() == this) {
			return true;
		}

		// check if P is on one of the segments
		for (GeoSegmentND segment : segmentsLinked.values()) {
			if (segment.isDefined() && segment.isOnPath(P, eps)) {
				return true;
			}
		}
		for (GeoSegmentND segment : segments.values()) {
			if (segment.isDefined() && segment.isOnPath(P, eps)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setShowObjectCondition(final GeoBoolean cond)
			throws CircularDefinitionException {

		super.setShowObjectCondition(cond);

		if (cons.isFileLoading()) {
			return;
		}

		for (GeoPoint3D point : pointsCreated) {
			point.setShowObjectCondition(cond);
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.setShowObjectCondition(cond);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.setShowObjectCondition(cond);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.setShowObjectCondition(cond);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((GeoElement) segment).setShowObjectCondition(cond);
		}
	}

	@Override
	public void updateVisualStyle(GProperty prop) {
		super.updateVisualStyle(prop);

		for (GeoPoint3D point : pointsCreated) {
			point.updateVisualStyle(prop);
		}

		for (GeoPolygon3D polygon : polygons.values()) {
			polygon.updateVisualStyle(prop);
		}

		for (GeoPolygon polygon : polygonsLinked) {
			polygon.updateVisualStyle(prop);
		}

		for (GeoSegment3D segment : segments.values()) {
			segment.updateVisualStyle(prop);
		}

		for (GeoSegmentND segment : getSegmentsLinked()) {
			((GeoElement) segment).updateVisualStyle(prop);
		}

	}

	@Override
	public void setPointSizeOrVisibility(int size) {
		for (GeoPoint3D point : pointsCreated) {
			setPointSize(point, size);

		}
		if (getParentAlgorithm() != null) {
			for (GeoElement point : getParentAlgorithm().getInput()) {
				if (point.isGeoPoint()) {
					setPointSize((GeoPointND) point, size);
				}
			}
		}

		for (GeoPolygon p : polygonsLinked) {
			p.setPointSizeOrVisibility(size);
		}
	}

	private static void setPointSize(GeoPointND point, int size) {
		if (size > 0) {
			point.setEuclidianVisibleIfNoConditionToShowObject(true);
			point.setPointSize(size);
		} else {
			point.setEuclidianVisibleIfNoConditionToShowObject(false);
		}
		point.updateRepaint();
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.ON_FILLING;
	}

	@Override
	public MyDouble getNumber() {
		return new MyDouble(kernel, getDouble());
	}

	@Override
	public double getDouble() {
		return getVolume();
	}

	@Override
	public ValueType getValueType() {
		return ValueType.NUMBER;
	}

	@Override
	public void pseudoCentroid(Coords coords) {
		coords.set(0, 0, 0);
		int n = 0;
		for (GeoSegment3D segment : segments.values()) {
			pseudoCentroidAdd(coords, segment);
			n++;
		}
		for (GeoSegmentND segment : segmentsLinked.values()) {
			pseudoCentroidAdd(coords, segment);
			n++;
		}

		coords.mulInside(0.5 / n);
	}

	final private static void pseudoCentroidAdd(Coords coords,
			GeoSegmentND segment) {
		coords.setAdd3(coords, segment.getStartInhomCoords());
		coords.setAdd3(coords, segment.getEndInhomCoords());
	}

	@Override
	public int getMinimumLineThickness() {
		return 0;
	}

	/**
	 * @param flag
	 *            whether children are labeled
	 */
	public void setChildrenLabelsSet(boolean flag) {
		childrenLabelsSet = flag;
	}

	/**
	 * @return whether children are labeled
	 */
	public boolean getChildrenLabelsSet() {
		return childrenLabelsSet;
	}

	/**
	 * replace dummy points and segments
	 * 
	 * @param points
	 *            points for replacement
	 * @param replacementSegments
	 *            segments for replacement
	 */
	public void replaceDummies(GeoPointND[] points, GeoSegmentND[] replacementSegments) {
		for (GeoSegment3D seg : getSegments3D()) {
			GeoPointND p1 = seg.getStartPoint();
			GeoPointND p2 = seg.getEndPoint();
			boolean needsChange = false;
			if (p1 instanceof DummyGeoPoint3D) {
				p1 = points[((DummyGeoPoint3D) p1).getDummyIndex()];
				needsChange = true;
			}
			if (p2 instanceof DummyGeoPoint3D) {
				p2 = points[((DummyGeoPoint3D) p2).getDummyIndex()];
				needsChange = true;
			}
			if (needsChange) {
				seg.modifyInputPoints(p1, p2);
			}
		}
		for (GeoPolygon3D poly : getPolygons()) {
			GeoPointND[] polyPoints = poly.getPointsND();
			GeoPointND[] newPoints = new GeoPointND[polyPoints.length];
			boolean pointsNeedChange = false;
			for (int i = 0; i < polyPoints.length; i++) {
				GeoPointND p = polyPoints[i];
				if (p instanceof DummyGeoPoint3D) {
					newPoints[i] = points[((DummyGeoPoint3D) p).getDummyIndex()];
					pointsNeedChange = true;
				} else {
					newPoints[i] = p;
				}
			}

			if (pointsNeedChange) {
				GeoSegmentND[] polySegments = poly.getSegments();
				GeoSegmentND[] newSegments = new GeoSegmentND[polySegments.length];
				boolean segmentsNeedChange = false;
				for (int i = 0; i < polySegments.length; i++) {
					GeoSegmentND s = polySegments[i];
					if (s instanceof DummyGeoSegment3D) {
						newSegments[i] = replacementSegments[((DummyGeoSegment3D) s)
								.getDummyIndex()];
						segmentsNeedChange = true;
					} else {
						newSegments[i] = s;
					}
				}
				poly.modifyInputPoints(newPoints);
				if (segmentsNeedChange) {
					poly.setSegments(newSegments);
				}
			}
		}
	}

	@Override
	public boolean isRegion() {
		return true;
	}

	@Override
	public boolean isRegion3D() {
		return true;
	}

	@Override
	public void pointChangedForRegion(GeoPointND P) {
		Region oldRegion = P.getRegion();
		PointChangedHelper helper = new PointChangedHelper((GeoPoint3D) P);
		for (GeoPolygon p : getPolygonsLinked()) {
			if (p.isDefined()) {
				helper.update(p);
			}
		}
		for (GeoPolygon3D p : getPolygons()) {
			if (p.isDefined()) {
				helper.update(p);
			}
		}
		helper.setResult();
		P.setRegion(oldRegion);
	}

	@Override
	public void regionChanged(GeoPointND P) {
		RegionParameters rp = P.getRegionParameters();
		double t1 = rp.getT1();
		int index = (int) t1;
		GeoPolygon polygon = index < getFacesSize() ? getFace(index)
				: polygons.lastEntry().getValue();
		if (!polygon.isDefined()) {
			for (int i = getFacesSize() - 1; i >= 0
					&& !polygon.isDefined(); i--) {
				polygon = getFace(i);
			}
		}
		rp.setT1(PointChangedHelper.getUnNormalized(t1 - index));
		Region oldRegion = P.getRegion();
		P.setRegion(polygon);
		polygon.regionChanged(P);
		rp.setT1(t1);
		rp.setNormal(polygon.getDirectionInD3());
		P.setRegion(oldRegion);
	}

	@Override
	public boolean isInRegion(GeoPointND P) {
		for (GeoPolygon3D p : getPolygons()) {
			if (p.isInRegion(P)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isInRegion(double x0, double y0) {
		// not used
		return false;
	}

}
