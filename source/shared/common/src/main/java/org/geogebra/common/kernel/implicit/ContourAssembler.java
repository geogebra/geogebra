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

package org.geogebra.common.kernel.implicit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.geogebra.common.kernel.MyPoint;

/**
 * Accumulates marching algorithm segments into one or more complete contours.
 *
 * <p>Internally delegates linking logic to a {@link ContourLinker}, then
 * flattens the linked point-lists into a single output sequence.</p>
 */
public class ContourAssembler {
	private List<MyPoint> output;
	private final ContourLinker linker;

	/**
	 * Creates a contour builder using the given linker strategy.
	 *
	 * <p>Starts with an empty output list; all subsequent points
	 * will be appended via {@link #flush()}.</p>
	 *
	 * @param linker the {@link ContourLinker} that defines how to connect marching segments
	 */
	public ContourAssembler(ContourLinker linker) {
		this.linker = linker;
		this.output = new ArrayList<>();
	}

	/**
	 * Add segment(s) based on the marching rectangle rect.
	 * @param rect {@link MarchingRect}
	 * @param provider {@link MarchingConfigProvider}
	 * @return the configuration flag.
	 */
	public int add(MarchingRect rect, MarchingConfigProvider provider) {
		return add(provider.create(rect), provider);
	}

	/**
	 * Add segment(s) based on the marching rectangle rect.
	 * @param config {@link MarchingConfig}
	 * @param provider {@link MarchingConfigProvider}
	 * @return the configuration flag.
	 */
	public int add(MarchingConfig config, MarchingConfigProvider provider) {
		if (!config.isValid()) {
			return config.flag();
		}
		MyPoint[] pts = provider.getPoints();
		boolean xChange = provider.canChangePointOrder();
		if (pts.length > 2) {
			linker.link(pts[0], pts[1], xChange);
			linker.link(pts[2], pts[3], xChange);
		} else {
			linker.link(pts[0], pts[1], xChange);
		}

		return config.flag();
	}

	/**
	 * Add all processed points to the curve.
	 */
	public void flush() {
		for (PointList points1 : linker) {
			output.add(points1.start);
			output.addAll(points1.pts);
			output.add(points1.end);
		}

		linker.clear();
	}

	/**
	 * @param locusPoints points
	 */
	public void updatePoints(List<MyPoint> locusPoints) {
		this.output = locusPoints;
	}

	/**
	 *
	 * @return the number of contours produced
	 */
	public int contourCount() {
		return linker.size();
	}

	/**
	 * Gets contour at given index.
	 *
	 * @param index of the contour
	 * @return the contour at given index
	 */
	public PointList contourAt(int index) {
		return linker.get(index);
	}

	/**
	 * Clears the current flattened output sequence produced by {@link #flush()}.
	 * <p>
	 * This does not affect the in-progress linking state held by the {@link ContourLinker};
	 * use {@link ContourLinker#clear()} via {@link #flush()} to discard pending contours.
	 * </p>
	 */
	public void reset() {
		output.clear();
	}

	/**
	 * Returns the flattened list of points accumulated by {@link #flush()} (or last
	 * set via {@link #updatePoints(List)}).
	 *
	 * @return a live reference to the current output list; callers may read or modify it
	 */
	public List<MyPoint> getOutput() {
		return output;
	}

	/**
	 * Enqueues a single segment {@code p0 -> p1} for linking using the current strategy.
	 * <p>
	 * Useful for stitching perimeter runs or externally supplied segments that do not
	 * originate from a {@link MarchingRect}.
	 * </p>
	 *
	 * @param p0 segment start (world coordinates)
	 * @param p1 segment end (world coordinates)
	 */
	public void addPointPair(MyPoint p0, MyPoint p1) {
		linker.link(p0, p1, false);
	}

	/**
	 * Iterates over the currently accumulated (but not yet flushed) contours.
	 *
	 * @param consumer callback invoked for each {@link PointList}
	 * @apiNote This walks the linker's internal state. Call {@link #flush()} to
	 *          flatten contours into {@link #getOutput()}.
	 */
	public void forEachContour(Consumer<PointList> consumer) {
		linker.forEach(consumer);
	}

	/**
	 * Returns the live list of contours maintained by the underlying {@link ContourLinker}.
	 *
	 * @return a mutable list view of in-progress contours
	 * @apiNote Intended for inspection/testing. Mutations affect the assembler's state.
	 */
	public List<PointList> getContours() {
		return linker;
	}
}
