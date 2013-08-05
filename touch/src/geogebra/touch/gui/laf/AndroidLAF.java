package geogebra.touch.gui.laf;

public class AndroidLAF extends DefaultLAF {
    @Override
    public int getAppBarHeight() {
	return 50;
    }

    @Override
    public DefaultResources getIcons() {
	// TODO Auto-generated method stub
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
    public boolean supportsShare() {
	return true;
    }
}
