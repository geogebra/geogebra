package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style.Overflow;

/**
 * Callback for tool panel opening/closing in portrait mode
 */
public class PortraitAnimationCallback extends HeaderAnimationCallback {

	private AppW app;

	/**
	 * @param header
	 *            header panel
	 * @param app
	 *            application
	 */
	public PortraitAnimationCallback(Header header, AppW app) {
		super(header, 0, 0);
		this.app = app;
	}

	@Override
	protected void onStart() {
		app.getFrameElement().getStyle().setOverflow(Overflow.HIDDEN);
		if (header.isOpen()) {
			header.removeStyleName("header-close-portrait");
			header.addStyleName("header-open-portrait");
			header.toolbarPanel.onOpen();
		}
		// header.hideCenter();
	}

	@Override
	protected void onEnd() {
		app.getFrameElement().getStyle().setOverflow(Overflow.VISIBLE);
		if (!header.isOpen()) {
			header.removeStyleName("header-open-portrait");
			header.addStyleName("header-close-portrait");
		}

		EuclidianView ev = header.app.getActiveEuclidianView();
		if (ev.getViewID() == App.VIEW_EUCLIDIAN3D) {
			return;
		}
		int d = header.isOpen() ? -1 : 1;

		ev.translateCoordSystemForAnimation(
				d * header.toolbarPanel.getOpenHeightInPortrait() / 2);
	}

	@Override
	public void tick(double progress) {
		// nothing to do.
	}

}
