package org.geogebra.common.move.views;

import java.util.Iterator;

/**
 * @author gabor
 * 
 *         renders the view concerning application is offline
 *
 */
public class OfflineView extends BaseView<BooleanRenderable> {

	/**
	 * Contstructs an offline view pool
	 */
	public OfflineView() {
		super();
	}

	/**
	 * renders the given View
	 * 
	 * @param b
	 *            true for online, false for offline
	 */
	public void render(boolean b) {
		Iterator<BooleanRenderable> views = this.viewComponents.iterator();
		while (views.hasNext()) {
			views.next().render(b);
		}
	}

}
