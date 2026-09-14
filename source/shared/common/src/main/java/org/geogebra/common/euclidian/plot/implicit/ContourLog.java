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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.util.debug.Log;

/**
 * Lightweight logger for the contour clipping/plotting pipeline.
 * <p>
 * Emits debug lines for contour counts, edge hits, view bounds, and the clip rectangle
 * when logging is enabled.
 * </p>
 *
 * @apiNote No-op unless the internal {@code logging} flag is {@code true}. Intended for
 *          development-time diagnostics.
 */
public class ContourLog {

	public static final String PREFIX = "[Contour] ";
	private static int contourCount = -1;
	private static final boolean logging = false;

	/**
	 * Logs the number of detected contours when it changes.
	 *
	 * @param contourCount current contour count
	 */
	public static void numOfContours(int contourCount) {
		if (ContourLog.contourCount != contourCount) {
			Log.debug(PREFIX + contourCount);
		}
		ContourLog.contourCount = contourCount;
	}

	/**
	 * Logs edge hits when verbose logging is enabled.
	 *
	 * @param hits list of edge hits from clipping
	 */
	public static void hits(List<EdgeHit> hits) {
		// no-op while focusing on post-stitch contour topology
	}

	/**
	 * Logs a compact fragment summary when explicit clipping diagnostics are enabled.
	 *
	 * @param fragments fragments produced by the clipper
	 */
	public static void fragments(List<ClippedFragment> fragments) {
		if (!logging) {
			return;
		}
		for (int i = 0; i < fragments.size(); i++) {
			ClippedFragment fragment = fragments.get(i);
			Log.debug("[Fragments] i=" + i
					+ " contour=" + fragment.sourceContourId()
					+ " closed=" + fragment.closed()
					+ " points=" + fragment.points().size()
					+ " start=" + fragment.start()
					+ " end=" + fragment.end());
		}
	}

	/**
	 * Logs a single edge hit (helper).
	 *
	 * @param hit hit to print
	 */
	public static void debugHit(EdgeHit hit) {
		debugHit(-1, hit);
	}

	/**
	 * Logs a single edge hit with an optional index prefix.
	 *
	 * @param index hit index, or a negative value to omit it
	 * @param hit hit to print
	 */
	public static void debugHit(int index, EdgeHit hit) {
		String prefix = index >= 0 ? "[ClipHits] i=" + index + " " : "[ClipHits] ";
		debug(prefix + formatHit(hit));
	}

	private static void debug(String message) {
		if (logging) {
			Log.debug(message);
		}
	}

	/**
	 * Logs the current view bounds for debugging.
	 *
	 * @param bounds Euclidian view bounds
	 */
	public static void bounds(EuclidianViewBounds bounds) {
		debug(PREFIX
				+ "("
				+ bounds.getXmin()
				+ ", "
				+ bounds.getXmax()
				+ ", "
				+ bounds.getYmin()
				+ ", "
				+ bounds.getYmax()
				+ ", "
				+ bounds.getWidth()
				+ ", "
				+ bounds.getHeight()
				+ ")");
	}

	/**
	 * Logs the clip rectangle and tolerances for debugging.
	 *
	 * @param clipRect rectangle used for clipping
	 * @param eps      numeric tolerances in world/param space
	 */
	public static void clipRect(ClipRect clipRect, ClipEpsilon eps) {
		debug(PREFIX
				+ " clipRect ("
				+ clipRect.getXmin()
				+ ", "
				+ clipRect.getXmax()
				+ ", "
				+ clipRect.getYmin()
				+ ", "
				+ clipRect.getYmax()
				+ ")\n");

	}

	/**
	 * Formats one hit into a compact single-line diagnostic string.
	 *
	 * @param hit hit to format
	 * @return compact hit description
	 */
	public static String formatHit(EdgeHit hit) {
		return "edge=" + hit.edge()
				+ " p=" + formatPoint(hit.point())
				+ " s=" + hit.sPerimeter()
				+ " seg=" + hit.segIndex()
				+ " tSeg=" + hit.tSegment()
				+ " contour=" + hit.getContourId();
	}

	private static String formatPoint(org.geogebra.common.kernel.MyPoint point) {
		return point == null ? "null" : formatPoint(point.x, point.y);
	}

	private static String formatPoint(double x, double y) {
		return "(" + x + ", " + y + ")";
	}
}
