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

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;

final class ViewportPanZoomDelta {

	private double baseXZero;
	private double baseYZero;
	private double baseXScale;
	private double baseYScale;
	private final EuclidianViewBounds bounds;
	private boolean active;

	/**
	 * Tracks a viewport baseline (zero point and scales) and applies the pan/zoom
	 * delta from that baseline to the current {@link EuclidianViewBounds}.
	 * <p>
	 * Intended for reusing an already-built shape during interactive pan/zoom.
	 */
	ViewportPanZoomDelta(EuclidianViewBounds bounds) {
		this.bounds = bounds;
	}

	void snapshot() {
		baseXZero = bounds.getXZero();
		baseYZero = bounds.getYZero();
		baseXScale = bounds.getXScale();
		baseYScale = bounds.getYScale();
		active = false;
	}

	/**
	 * Returns {@code gArea} transformed from the stored baseline viewport to the
	 * current viewport reported by {@link EuclidianViewBounds}.
	 * <p>
	 * The mapping accounts for pan (zero-point shift) and zoom (scale change).
	 * This method also updates the baseline to the current viewport so repeated
	 * calls apply incremental deltas during a gesture.
	 * @param shape a shape built for the last baseline (non-{@code null})
	 * @return a new {@link GShape} positioned for the current viewport
	 * @apiNote Use during pan/zoom to preview the fill without a full recompute.
	 * After the gesture completes and you recompute, call {@link #snapshot()} again.
	 * @implNote The internal affine transform composes incrementally; calls made in
	 * sequence during a gesture build on prior state.
	 */
	GShape applyTo(GShape shape) {
		active = true;
		GAffineTransform transform = AwtFactory.getPrototype().newAffineTransform();
		transform.translate(bounds.getXZero(), bounds.getYZero());
		transform.scale(bounds.getXScale() / baseXScale, bounds.getYScale() / baseYScale);
		transform.translate(-baseXZero, -baseYZero);
		snapshot();
		active = false;
		return transform.createTransformedShape(shape);
	}

	/**
	 * Returns {@code gArea} transformed from the stored baseline viewport to the
	 * current viewport as a new area.
	 * @param gArea an area built for the last baseline (non-{@code null})
	 * @return a new {@link GArea} positioned for the current viewport
	 */
	GArea applyTo(GArea gArea) {
		return AwtFactory.getPrototype().newArea(applyTo((GShape) gArea));
	}

	boolean isActive() {
		return active;
	}
}
