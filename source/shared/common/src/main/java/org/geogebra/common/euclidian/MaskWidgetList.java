package org.geogebra.common.euclidian;

/**
 * List of mask widgets.
 */
public interface MaskWidgetList {

	/**
	 * Remove all mask widgets from the foreground, putting them back
	 * on the canvas
	 */
	void clearMasks();

	/**
	 * Query existing masks, and move them to the foreground to
	 * cover the widgets
	 */
	void masksToForeground();
}
