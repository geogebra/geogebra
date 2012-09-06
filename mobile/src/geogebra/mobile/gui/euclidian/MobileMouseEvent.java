package geogebra.mobile.gui.euclidian;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.event.AbstractEvent;

/**
 * Base implementation of AbstractEvent.
 * 
 * @author Thomas Krismayer
 *
 */
public class MobileMouseEvent extends AbstractEvent
{

	private GPoint point = new GPoint(0,0); 

	public MobileMouseEvent(int x, int y)
	{
		this.point = new GPoint(x, y); 
	}

	@Override
	public GPoint getPoint()
	{
		return this.point;
	}

	@Override
	public boolean isAltDown()
	{
		return false;
	}

	@Override
	public boolean isShiftDown()
	{
		return false;
	}

	@Override
	public void release()
	{
	}

	@Override
	public int getID()
	{
		return 0;
	}

	@Override
	public int getX()
	{
		return this.point.x;
	}

	@Override
	public int getY()
	{
		return this.point.y;
	}

	@Override
	public boolean isRightClick()
	{
		return false;
	}

	@Override
	public boolean isControlDown()
	{
		return false;
	}

	@Override
	public int getClickCount()
	{
		return 0;
	}

	@Override
	public boolean isMetaDown()
	{
		return false;
	}

	@Override
	public double getWheelRotation()
	{
		return 0;
	}

	@Override
	public boolean isMiddleClick()
	{
		return false;
	}

	@Override
	public boolean isPopupTrigger()
	{
		return false;
	}

}
