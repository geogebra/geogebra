package org.geogebra.common.move.views;

/**
 * @author gabor
 * 
 *         renders the view concerning application is offline
 *
 */
public class OfflineView extends BaseView<BooleanRenderable> {

	/**
	 * renders the given View
	 * 
	 * @param b
	 *            true for online, false for offline
	 */
	public void render(boolean b) {
		for (BooleanRenderable renderable : viewComponents) {
			renderable.render(b);
		}
	}

}
