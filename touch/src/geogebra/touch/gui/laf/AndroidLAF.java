package geogebra.touch.gui.laf;


public class AndroidLAF extends DefaultLAF
{
	@Override
	public DefaultResources getIcons()
	{
		// TODO Auto-generated method stub
		return AndroidResources.INSTANCE;
	}
	@Override
	public int getAppBarHeight()
	{
		return 50;
	}
	
	@Override
	public boolean isMouseDownIgnored()
	{
	  return true;
	}
	
	@Override
	public int getPaddingLeftOfDialog() {
		return 15;
	}
}
