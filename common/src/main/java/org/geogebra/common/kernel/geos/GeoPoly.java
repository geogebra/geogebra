package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.Parametrizable;

/**
 * Common interface for polygons and polylines
 */
public interface GeoPoly extends Parametrizable {
	/**
	 * Returns true iff all vertices are labeled
	 * 
	 * @return true iff all vertices are labeled
	 */
	public boolean isAllVertexLabelsSet();

	/**
	 * Returns true iff number of vertices is not volatile
	 * 
	 * @return true iff number of vertices is not volatile
	 */
	public boolean isVertexCountFixed();

	/**
	 * Returns array of all vertices
	 * 
	 * @return array of all vertices
	 */
	public GeoPointND[] getPoints();

	/**
	 * Returns i-th vertex
	 * 
	 * @param i
	 *            index
	 * @return i-th vertex
	 */
	public GeoPoint getPoint(int i);

	/**
	 * @return boundary as Path
	 */
	public Path getBoundary();

	/**
	 * @return array of all vertices
	 */
	public GeoPointND[] getPointsND();

	/**
	 * @return number of vertices
	 */
	public int getNumPoints();

	/**
	 * @param index
	 *            index, starts with 0
	 * @return vertex with given index
	 */
	public GeoPointND getPointND(int index);

}
