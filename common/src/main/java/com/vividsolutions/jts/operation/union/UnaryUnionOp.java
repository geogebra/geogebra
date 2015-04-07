/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package com.vividsolutions.jts.operation.union;


import java.util.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.util.*;
import com.vividsolutions.jts.operation.overlay.OverlayOp;

/**
 * Unions a collection of Geometry or a single Geometry 
 * (which may be a collection) together.
 * By using this special-purpose operation over a collection of geometries
 * it is possible to take advantage of various optimizations to improve performance.
 * Heterogeneous {@link GeometryCollection}s are fully supported.
 * <p>
 * The result obeys the following contract:
 * <ul>
 * <li>Unioning a set of overlapping {@link Polygons}s has the effect of
 * merging the areas (i.e. the same effect as 
 * iteratively unioning all individual polygons together).
 * 
 * <li>Unioning a set of {@link LineString}s has the effect of <b>fully noding</b> 
 * and <b>dissolving</b> the input linework.
 * In this context "fully noded" means that there will be a node or endpoint in the output 
 * for every endpoint or line segment crossing in the input.
 * "Dissolved" means that any duplicate (e.g. coincident) line segments or portions
 * of line segments will be reduced to a single line segment in the output.  
 * This is consistent with the semantics of the 
 * {@link Geometry#union(Geometry)} operation.
 * If <b>merged</b> linework is required, the {@link LineMerger} class can be used.
 * 
 * <li>Unioning a set of {@link Points}s has the effect of merging
 * al identical points (producing a set with no duplicates).
 * </ul>
 * 
 * @author mbdavis
 *
 */
public class UnaryUnionOp 
{
	public static Geometry union(Collection geoms)
	{
		UnaryUnionOp op = new UnaryUnionOp(geoms);
		return op.union();
	}
	
	public static Geometry union(Collection geoms, GeometryFactory geomFact)
	{
		UnaryUnionOp op = new UnaryUnionOp(geoms, geomFact);
		return op.union();
	}
	
	public static Geometry union(Geometry geom)
	{
		UnaryUnionOp op = new UnaryUnionOp(geom);
		return op.union();
	}
	
	private List polygons = new ArrayList();
	private List lines = new ArrayList();
	private List points = new ArrayList();
	
	private GeometryFactory geomFact = null;
	
	public UnaryUnionOp(Collection geoms, GeometryFactory geomFact)
	{
		this.geomFact = geomFact;
		extract(geoms);
	}
	
	public UnaryUnionOp(Collection geoms)
	{
		extract(geoms);
	}
	
	public UnaryUnionOp(Geometry geom)
	{
		extract(geom);
	}
	
	private void extract(Collection geoms)
	{
		for (Iterator i = geoms.iterator(); i.hasNext();) {
			Geometry geom = (Geometry) i.next();
			extract(geom);
		}
	}
	
	private void extract(Geometry geom)
	{
		if (geomFact == null)
			geomFact = geom.getFactory();
		
		/*
		PolygonExtracter.getPolygons(geom, polygons);
		LineStringExtracter.getLines(geom, lines);
		PointExtracter.getPoints(geom, points);
		*/
		GeometryExtracter.extract(geom, Polygon.class, polygons);
		GeometryExtracter.extract(geom, LineString.class, lines);
		GeometryExtracter.extract(geom, Point.class, points);
	}

	/**
	 * Gets the union of the input geometries.
	 * If no input geometries were provided, a POINT EMPTY is returned.
	 * 
	 * @return a Geometry containing the union
	 * @return an empty GEOMETRYCOLLECTION if no geometries were provided in the input
	 */
	public Geometry union()
	{
		if (geomFact == null) {
			return null;
		}
		
		
		Geometry unionPoints = null;
		if (points.size() > 0) {
			Geometry ptGeom = geomFact.buildGeometry(points);
			unionPoints = unionNoOpt(ptGeom);
		}
		
		Geometry unionLines = null;
		if (lines.size() > 0) {
			Geometry lineGeom = geomFact.buildGeometry(lines);
			unionLines = unionNoOpt(lineGeom);
		}
		
		Geometry unionPolygons = null;
		if (polygons.size() > 0) {
			unionPolygons = CascadedPolygonUnion.union(polygons);
		}
		
    /**
     * Performing two unions is somewhat inefficient,
     * but is mitigated by unioning lines and points first
     */
		Geometry unionLA = unionWithNull(unionLines, unionPolygons);
		Geometry union = null;
		if (unionPoints == null)
			union = unionLA;
		else if (unionLA == null)
			union = unionPoints;
		else 
			union = PointGeometryUnion.union((Puntal) unionPoints, unionLA);
		
		if (union == null)
			return geomFact.createGeometryCollection(null);
		
		return union;
	}
	
  /**
   * Computes the union of two geometries, 
   * either of both of which may be null.
   * 
   * @param g0 a Geometry
   * @param g1 a Geometry
   * @return the union of the input(s)
   * @return null if both inputs are null
   */
  private Geometry unionWithNull(Geometry g0, Geometry g1)
  {
  	if (g0 == null && g1 == null)
  		return null;

  	if (g1 == null)
  		return g0;
  	if (g0 == null)
  		return g1;
  	
  	return g0.union(g1);
  }

  /**
   * Computes a unary union with no extra optimization,
   * and no short-circuiting.
   * Due to the way the overlay operations 
   * are implemented, this is still efficient in the case of linear 
   * and puntal geometries.
   * 
   * @param g0 a geometry
   * @return the union of the input geometry
   */
	private Geometry unionNoOpt(Geometry g0)
	{
    Geometry empty = geomFact.createPoint((Coordinate) null);
		return OverlayOp.overlayOp(g0, empty, OverlayOp.UNION);
	}
	
}
