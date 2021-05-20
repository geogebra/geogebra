package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.DockSplitPaneW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style.Overflow;

/**
 * Callback for tool panel opening/closing in portrait mode
 */
public class PortraitAnimationCallback extends NavRailAnimationCallback {

	private final DockSplitPaneW dockParent;
	private final AppW app;

	/**
	 * @param header
	 *            header panel
	 * @param app application
	 * @param dockParent parent split pane
	 */
	public PortraitAnimationCallback(NavigationRail header, AppW app,
			DockSplitPaneW dockParent) {
		super(header);
		this.app = app;
		this.dockParent = dockParent;
	}

	@Override
	protected void onStart() {
		app.getFrameElement().getStyle().setOverflow(Overflow.HIDDEN);
		if (navRail.isOpen()) {
			navRail.toolbarPanel.onOpen();
		}
		// header.hideCenter();
	}

	@Override
	protected void onEnd() {
		app.getFrameElement().getStyle().setOverflow(Overflow.VISIBLE);
		EuclidianView ev = navRail.app.getActiveEuclidianView();
		if (ev.getViewID() == App.VIEW_EUCLIDIAN3D) {
			return;
		}
		int d = navRail.isOpen() ? -1 : 1;

		ev.translateCoordSystemForAnimation(
				d * navRail.toolbarPanel.getOpenHeightInPortrait() / 2);
		dockParent.forceLayout();
	}

}
