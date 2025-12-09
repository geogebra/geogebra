/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.euclidian3D;

import java.util.HashMap;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.ConstructionDefaults3D;
import org.geogebra.common.kernel.ConstructionDefaults;

/**
 * static methods used in desktop / web for 3D style bar
 * 
 * @author mathieu
 *
 */
public class EuclidianStyleBarStatic3D {

	/**
	 * fill map for 3D
	 * 
	 * @param defaultGeoMap
	 *            map already filled for 2D
	 */
	public static void addToDefaultMap(
			HashMap<Integer, Integer> defaultGeoMap) {

		// lines
		defaultGeoMap.put(EuclidianConstants.MODE_ORTHOGONAL_THREE_D,
				ConstructionDefaults.DEFAULT_LINE);

		// conics
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_AXIS_POINT,
				ConstructionDefaults.DEFAULT_CONIC);
		defaultGeoMap.put(EuclidianConstants.MODE_CIRCLE_POINT_RADIUS_DIRECTION,
				ConstructionDefaults.DEFAULT_CONIC);

		// intersection curve
		defaultGeoMap.put(EuclidianConstants.MODE_INTERSECTION_CURVE,
				ConstructionDefaults3D.DEFAULT_INTERSECTION_CURVE);

		// planes
		defaultGeoMap.put(EuclidianConstants.MODE_PLANE_THREE_POINTS,
				ConstructionDefaults3D.DEFAULT_PLANE3D);
		defaultGeoMap.put(EuclidianConstants.MODE_PLANE,
				ConstructionDefaults3D.DEFAULT_PLANE3D);
		defaultGeoMap.put(EuclidianConstants.MODE_ORTHOGONAL_PLANE,
				ConstructionDefaults3D.DEFAULT_PLANE3D);
		defaultGeoMap.put(EuclidianConstants.MODE_PARALLEL_PLANE,
				ConstructionDefaults3D.DEFAULT_PLANE3D);

		// spheres
		defaultGeoMap.put(EuclidianConstants.MODE_SPHERE_POINT_RADIUS,
				ConstructionDefaults3D.DEFAULT_QUADRIC);
		defaultGeoMap.put(EuclidianConstants.MODE_SPHERE_TWO_POINTS,
				ConstructionDefaults3D.DEFAULT_QUADRIC);

		// cylinders, cones
		defaultGeoMap.put(EuclidianConstants.MODE_CONE_TWO_POINTS_RADIUS,
				ConstructionDefaults.DEFAULT_PYRAMID_AND_CONE);
		defaultGeoMap.put(EuclidianConstants.MODE_CYLINDER_TWO_POINTS_RADIUS,
				ConstructionDefaults.DEFAULT_PRISM_AND_CYLINDER);
		defaultGeoMap.put(EuclidianConstants.MODE_EXTRUSION,
				ConstructionDefaults.DEFAULT_PRISM_AND_CYLINDER);
		defaultGeoMap.put(EuclidianConstants.MODE_CONIFY,
				ConstructionDefaults.DEFAULT_PYRAMID_AND_CONE);

		// polyhedrons
		defaultGeoMap.put(EuclidianConstants.MODE_PYRAMID,
				ConstructionDefaults.DEFAULT_PYRAMID_AND_CONE);
		defaultGeoMap.put(EuclidianConstants.MODE_PRISM,
				ConstructionDefaults.DEFAULT_PRISM_AND_CYLINDER);
		defaultGeoMap.put(EuclidianConstants.MODE_TETRAHEDRON,
				ConstructionDefaults.DEFAULT_PYRAMID_AND_CONE);
		defaultGeoMap.put(EuclidianConstants.MODE_CUBE,
				ConstructionDefaults.DEFAULT_POLYHEDRON);

		// net
		defaultGeoMap.put(EuclidianConstants.MODE_NET,
				ConstructionDefaults3D.DEFAULT_NET);

	}
}
