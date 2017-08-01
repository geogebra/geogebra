package org.geogebra.web.web.gui.toolbarpanel;

public class PortraitAnimationCallback extends HeaderAnimationCallback {

	/**
	 * 
	 * @param header
	 * @param expandFrom
	 * @param expandTo
	 */
	public PortraitAnimationCallback(Header header, int expandFrom,
			int expandTo) {
		super(header, expandFrom, expandTo);

	}

	@Override
	protected void onStart() {
		if (header.isOpen()) {
			header.removeStyleName("header-close-portrait");
			header.addStyleName("header-open-portrait");
			header.toolbarPanel.onOpen();
		}
	}

	@Override
	protected void onEnd() {
		if (!header.isOpen()) {
			header.removeStyleName("header-open-portrait");
			header.addStyleName("header-close-portrait");
		}
	}

	@Override
	public void tick(double progress) {
		// nothing to do.
	}

}
