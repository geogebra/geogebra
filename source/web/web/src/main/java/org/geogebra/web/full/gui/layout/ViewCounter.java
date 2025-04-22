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
