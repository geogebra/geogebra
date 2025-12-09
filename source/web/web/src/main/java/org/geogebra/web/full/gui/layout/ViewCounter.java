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

package org.geogebra.web.full.gui.layout;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.util.StringConsumer;

import elemental2.dom.HTMLCanvasElement;

public class ViewCounter {
	private final HTMLCanvasElement canvas;
	private final StringConsumer callback;
	private int count;

	/**
	 * @param canvas canvas
	 * @param callback callback after last view is painted
	 */
	public ViewCounter(HTMLCanvasElement canvas, StringConsumer callback) {
		this.canvas = canvas;
		this.callback = callback;
		this.count = 1;
	}

	/**
	 * Decrease the number of views + split panes waiting for paint
	 */
	public void decrement() {
		count--;
		if (count == 0) {
			callback.consume(StringUtil.removePngMarker(canvas.toDataURL()));
		}
	}

	/**
	 * Increment the counter.
	 */
	public void increment() {
		count++;
	}
}
