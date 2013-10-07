package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;

public class AndroidLAF extends DefaultLAF {

	public AndroidLAF(final TouchApp app) {
		super(app);
	}

	@Override
	public DefaultResources getIcons() {
		return AndroidResources.INSTANCE;
	}

	@Override
	public int getPaddingLeftOfDialog() {
		return 0;
	}

	@Override
	public boolean receivesDoubledEvents() {
		return true;
	}

	@Override
	public boolean useClickHandlerForOpenClose() {
		return true;
	}
}
