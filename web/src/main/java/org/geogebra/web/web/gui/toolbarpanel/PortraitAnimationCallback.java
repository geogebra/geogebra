package org.geogebra.web.web.gui.toolbarpanel;

import org.geogebra.common.euclidian.EuclidianView;

public class PortraitAnimationCallback extends HeaderAnimationCallback {


	/**
	 * 
	 * @param header
	 * @param expandFrom
	 * @param expandTo
	 */
	public PortraitAnimationCallback(Header header) {
		super(header, 0, 0);

	}

	@Override
	protected void onStart() {
		if (header.isOpen()) {
			header.removeStyleName("header-close-portrait");
			header.addStyleName("header-open-portrait");
			header.toolbarPanel.onOpen();
		}
		// header.hideCenter();
		}

	@Override
	protected void onEnd() {
		if (!header.isOpen()) {
			header.removeStyleName("header-open-portrait");
			header.addStyleName("header-close-portrait");
		}
		EuclidianView ev = header.app.getActiveEuclidianView();
		int d = header.isOpen() ? -1 : 1;

		ev.translateCoordSystemForAnimation(
				d * header.toolbarPanel.getOpenHeightInPortrait() / 2);
	}

	@Override
	public void tick(double progress) {
		// nothing to do.
	}

}
