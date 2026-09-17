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

package org.geogebra.ggbjdk.java.awt.geom;

import org.geogebra.common.awt.GGeneralPath;

/**
 * The {@code GeneralPath} class represents a geometric path
 * constructed from straight lines, and quadratic and cubic
 * (B&eacute;zier) curves.  It can contain multiple subpaths.
 * <p>
 * {@code GeneralPath} is a legacy final class which exactly
 * implements the behavior of its superclass {@link Path2D.Double}.
 * <p>
 * Use {@code Path2D.Float} (or this legacy {@code GeneralPath}
 * subclass) when dealing with data that can be represented
 * and used with floating point precision.  Use {@code Path2D.Double}
 * for data that requires the accuracy or range of double precision.
 *
 * @author Jim Graham
 * @since 1.2
 */
public final class GeneralPath extends Path2D.Double implements GGeneralPath {
	/**
	 * Constructs a new empty single precision {@code GeneralPath} object
	 * with a default winding rule of {@link #WIND_NON_ZERO}.
	 *
	 * @since 1.2
	 */
	public GeneralPath() {
		super(WIND_NON_ZERO, INIT_SIZE);
	}

	/**
	 * Constructs a new <code>GeneralPath</code> object with the specified
	 * winding rule to control operations that require the interior of the
	 * path to be defined.
	 *
	 * @param rule the winding rule
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 * @since 1.2
	 */
	public GeneralPath(int rule) {
		super(rule, INIT_SIZE);
	}

	/**
	 * Constructs a new <code>GeneralPath</code> object with the specified
	 * winding rule and the specified initial capacity to store path
	 * coordinates.
	 * This number is an initial guess as to how many path segments
	 * will be added to the path, but the storage is expanded as
	 * needed to store whatever path segments are added.
	 *
	 * @param rule the winding rule
	 * @param initialCapacity the estimate for the number of path segments
	 *                        in the path
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 * @since 1.2
	 */
	public GeneralPath(int rule, int initialCapacity) {
		super(rule, initialCapacity);
	}

	/**
	 * Constructs a new <code>GeneralPath</code> object from an arbitrary
	 * {@link Shape} object.
	 * All of the initial geometry and the winding rule for this path are
	 * taken from the specified <code>Shape</code> object.
	 *
	 * @param s the specified <code>Shape</code> object
	 * @since 1.2
	 */
	public GeneralPath(Shape s) {
		super(s, null);
	}

	GeneralPath(
			int windingRule, byte[] pointTypes, int numTypes, double[] pointCoords, int numCoords) {
		// used to construct from native

		this.windingRule = windingRule;
		this.pointTypes = pointTypes;
		this.numTypes = numTypes;
		this.doubleCoords = pointCoords;
		this.numCoords = numCoords;
	}
}
