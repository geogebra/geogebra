package geogebra.mobile.utils;

import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;

/**
 * Adapter class with empty methods for {@link TouchHandler}
 * @author Matthias Meisinger
 *
 */
public class TouchAdapter implements TouchHandler
{

	@Override
	public void onTouchStart(TouchStartEvent event)
	{		
	}

	@Override
	public void onTouchMove(TouchMoveEvent event)
	{

	}

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{

	}

	@Override
	public void onTouchCanceled(TouchCancelEvent event)
	{

	}

}
