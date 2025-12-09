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

package org.geogebra.common.kernel;

/**
 * point pair (i, j, dist) stores the point pair D_i, Q_j and their distance
 */
public class PointPair {
	/** index of point in P */
	public int indexP;
	/** true if point in P is alive */
	public boolean isPalive;
	/** index of point in Q */
	public int indexQ;
	/** true if point in Q is on path */
	public boolean isQonPath;
	/** distance */
	public double dist;
	/** pointer to next PointPair */
	public PointPair next;

	/**
	 * @param i
	 *            index of point in P
	 * @param isPalive
	 *            true if point in P is alive
	 * @param j
	 *            index of point in Q
	 * @param isQjOnPath
	 *            true if Qj is on path
	 * @param distance
	 *            distance
	 */
	public PointPair(int i, boolean isPalive, int j, boolean isQjOnPath,
			double distance) {
		indexP = i;
		this.isPalive = isPalive;
		indexQ = j;
		isQonPath = isQjOnPath;
		dist = distance;
	}

	/*
	 * public String toString() { StringBuilder sb = new StringBuilder();
	 * sb.append("("); sb.append(indexP); sb.append(", "); sb.append(isPalive);
	 * sb.append(", "); sb.append(indexQ); sb.append(", ");
	 * sb.append(isQonPath); sb.append(", "); sb.append(dist); sb.append(")\n");
	 * return sb.toString(); }
	 */
}
