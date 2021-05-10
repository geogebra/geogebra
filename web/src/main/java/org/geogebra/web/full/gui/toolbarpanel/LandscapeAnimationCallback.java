package org.geogebra.web.full.gui.toolbarpanel;

/**
 * Callback for tool panel opening/closing in landscape mode
 */
public class LandscapeAnimationCallback extends NavRailAnimationCallback {

	/**
	 * @param header
	 *            header
	 */
	public LandscapeAnimationCallback(NavigationRail header) {
		super(header);
	}

	@Override
	protected void onStart() {
		navRail.setAnimating(true);
		navRail.hideUndoRedoPanel();
	}

	@Override
	protected void onEnd() {
		navRail.setAnimating(false);
		navRail.onLandscapeAnimationEnd();
	}
}
