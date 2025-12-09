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

package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.function.GeoFunctionConverter;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.geogebra.common.kernel.interval.samplers.FunctionSampler;

/**
 * Function plotter based on interval arithmetic
 *
 * @author laszlo
 */
public class IntervalPlotter {
	private final EuclidianViewBounds evBounds;
	private final IntervalPathPlotter gp;
	private boolean enabled;
	private IntervalFunctionModel model = null;

	private IntervalPlotController controller;
	private final GeoFunctionConverter converter;
	private IntervalPath path;

	/**
	 * Creates a disabled plotter
	 */
	public IntervalPlotter(GeoFunctionConverter converter, EuclidianViewBounds bounds,
			IntervalPathPlotter pathPlotter) {
		this.converter = converter;
		this.evBounds = bounds;
		this.gp = pathPlotter;
		this.enabled = false;
	}

	/**
	 * Enables plotter without controller
	 */
	public void enableFor(GeoFunction function) {
		build(function);
		enable();
	}

	private void enable() {
		enabled = true;
		model.update();
	}

	/**
	 * Enables plotter
	 * @param function plotted function
	 * @param view view
	 * @param forList whether function is a part of a list
	 */
	public void enableFor(GeoFunction function, EuclidianView view, boolean forList) {
		build(function);
		this.controller.attachEuclidianView(view);
		if (!forList) {
			view.getEuclidianController().addZoomerAnimationListener(controller, function);
		}
		enable();
	}

	private void build(GeoFunction function) {
		IntervalTupleList tuples = new IntervalTupleList();
		IntervalFunctionData data = new IntervalFunctionData(function, converter, evBounds, tuples);
		FunctionSampler sampler = new FunctionSampler(data, evBounds);
		QueryFunctionData query = new QueryFunctionDataImpl(tuples);
		path = new IntervalPath(gp, evBounds, query);
		model = new IntervalFunctionModelImpl(data, sampler, evBounds, path);
		this.controller = new IntervalPlotController(model, function);
	}

	/**
	 * Update path to draw.
	 */
	public void update() {
		model.update();
	}

	/**
	 * Draws result to Graphics
	 *
	 * @param g2 {@link GGraphics2D}
	 */
	public void draw(GGraphics2D g2) {
		gp.draw(g2);
	}

	/**
	 *
	 * @return if plotter is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Disable interval plotter.
	 */
	public void disable() {
		enabled = false;
		if (model != null) {
			model.clear();
		}

		if (controller != null) {
			controller.detach();
		}
	}

	/**
	 * @return point of label
	 */
	public GPoint getLabelPoint() {
		return path.getLabelPoint();
	}

	/**
	 * Call it when plotter needs a full update
	 */
	public void needsUpdateAll() {
		model.needsResampling();
	}
}
