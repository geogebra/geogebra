package org.geogebra.common.geogebra3D.kernel3D.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionElementCycle;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.plugin.GeoClass;

/**
 * Net for a polyhedron
 * 
 * @author Vincent
 *
 */
public class GeoPolyhedronNet extends GeoPolyhedron {
	private GeoPolygon3D[] oldFaces;
	private int oldFacesIndex;

	private GeoSegment3D[] oldSegments;
	private StringBuilder sbToString = new StringBuilder(50);

	/**
	 * @param c
	 *            construction
	 */
	public GeoPolyhedronNet(Construction c) {
		super(c, TYPE_NET);
	}

	@Override
	public String getTypeString() {
		return "Net";
	}

	@Override
	public boolean isGeoPolyhedron() {
		return false;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format(getArea(), tpl));
		return sbToString.toString();
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		return kernel.format(getArea(), tpl);
	}

	@Override
	public void createFaces() {

		// save old faces
		if (polygons != null) {
			oldFaces = getFaces3D();
			oldFacesIndex = 0;
			// clear to renew
			polygons.clear();
		} else {
			oldFaces = null;
		}

		// save old edges
		if (segments != null) {
			oldSegments = getSegments3D();
			// clear to renew
			segments.clear();
		} else {
			oldSegments = null;
		}

		super.createFaces();

	}

	/**
	 * clear indexes (needed before new call to createFaces)
	 */
	public void clearIndexes() {
		polygonsIndex.clear();
		polygonsDescriptions.clear();
		polygonsIndexMax = 0;
		segmentsIndex.clear();
		segmentsIndexMax = 0;
	}

	@Override
	public GeoPolygon3D createPolygon(GeoPointND[] points, int index) {

		if (oldFaces != null && oldFacesIndex < oldFaces.length) {
			GeoPolygon3D polygon = oldFaces[oldFacesIndex];
			polygon.modifyInputPoints(points);
			polygons.put(index, polygon);
			oldFacesIndex++;
			return polygon;
		}

		return super.createPolygon(points, index);
	}

	@Override
	public GeoSegmentND createNewSegment(GeoPointND startPoint,
			GeoPointND endPoint, ConstructionElementCycle key) {

		if (oldSegments != null && segmentsIndexMax < oldSegments.length) {
			GeoSegment3D segment = oldSegments[(int) segmentsIndexMax];
			segment.modifyInputPoints(startPoint, endPoint);
			storeSegment(segment, key);
			return segment;
		}

		return super.createNewSegment(startPoint, endPoint, key);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.NET;
	}

	@Override
	public void setEuclidianVisible(boolean visible) {

		super.setEuclidianVisible(visible);

		for (GeoPoint3D point : pointsCreated) {
			point.setEuclidianVisible(visible);
		}
	}

	@Override
	public double getDouble() {
		return getArea();
	}
}
