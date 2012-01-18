package geogebra.web.euclidian.event;

import java.util.HashMap;

import com.google.gwt.dom.client.NativeEvent;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.web.main.Application;

public class TouchEvent extends AbstractEvent {
	
	private static final Integer TOUCHSTART = 1;
	private static final Integer TOUCHMOVE = 2;
	private static final Integer TOUCHEND = 3;
	public static HashMap<Integer, TouchEvent> pool = new HashMap<Integer, TouchEvent>();
	private com.google.gwt.event.dom.client.TouchEvent event;
	private Integer id;
	
	private TouchEvent(com.google.gwt.event.dom.client.TouchEvent event) {
		this.event = event;
		this.id  = getTypeId(event.getAssociatedType().getName());
		Application.console("id: "+this.id);
		TouchEvent.pool.put(this.id, this);
	}
	
	private Integer getTypeId(String type) {
	   if (type.equals("touchstart")) {
		   return TOUCHSTART;
	   } else if (type.equals("touchmove")) {
		   return TOUCHMOVE;
	   } else if (type.equals("touchend")) {
		   return TOUCHEND;
	   }
	 return 0;
    }

	@Override
	public Point getPoint() {
		TouchEvent current = TouchEvent.pool.get(this.id);
		return null;
	}

	@Override
	public boolean isAltDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isShiftDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void release(int l) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isRightClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isControlDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getClickCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isMetaDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getWheelRotation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isMiddleClick() {
		// TODO Auto-generated method stub
		return false;
	}

	public static AbstractEvent wrapEvent(NativeEvent nativeEvent) {
	    // TODO Auto-generated method stub
	    return null;
    }

}
