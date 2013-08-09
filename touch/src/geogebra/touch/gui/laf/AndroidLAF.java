package geogebra.touch.gui.laf;

import geogebra.touch.TouchApp;

public class AndroidLAF extends DefaultLAF {

	public AndroidLAF(TouchApp app) {
		super(app);
	}

	@Override
	public int getAppBarHeight() {
		return 50;
	}

	@Override
	public DefaultResources getIcons() {
		return AndroidResources.INSTANCE;
	}

	@Override
	public int getPaddingLeftOfDialog() {
		return 15;
	}

	@Override
	public boolean isMouseDownIgnored() {
		return true;
	}

	@Override
	public boolean isShareSupported() {
		return true;
	}
}
