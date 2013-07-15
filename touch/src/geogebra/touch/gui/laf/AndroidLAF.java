package geogebra.touch.gui.laf;

public class AndroidLAF extends DefaultLAF
{
	@Override
	public DefaultIcons getIcons()
	{
		// TODO Auto-generated method stub
		return AndroidIcons.INSTANCE;
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
}
