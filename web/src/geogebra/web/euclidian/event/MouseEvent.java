package geogebra.web.euclidian.event;



import java.util.HashMap;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.HasNativeEvent;
import com.google.gwt.user.client.Event;

import geogebra.common.awt.Point;
import geogebra.common.euclidian.event.AbstractEvent;

public class MouseEvent extends AbstractEvent {
	
	public static HashMap<Integer,MouseEvent> pool = new HashMap<Integer, MouseEvent>();
	private NativeEvent event;
	private Integer id;
	
	public static final int	MOUSE_CLICKED = 500;
	public static final int	MOUSE_DRAGGED =	506;
	public static final int	MOUSE_ENTERED =	504;
	public static final int	MOUSE_EXITED =	505;
	public static final int	MOUSE_FIRST	 = 500;
	public static final int	MOUSE_LAST	 = 507;
	public static final int	MOUSE_MOVED	 = 503;
	public static final int	MOUSE_PRESSED	= 501;
	public static final int	MOUSE_RELEASED	= 502;
	public static final int	MOUSE_WHEEL	 = 507;
	
	private MouseEvent(NativeEvent event) {
		this.event = event;
		this.id = getTypeId(event.getType());
		MouseEvent.pool.put(this.id, this);
	}
	
	private Integer getTypeId(String type) {
		if (type.equals("click")) {
			return MOUSE_CLICKED;
		} else if (type.equals("mousedown")) {
			return MOUSE_PRESSED;
		} else if (type.equals("mouseup")) {
			return MOUSE_RELEASED;
		} else if (type.equals("mousemove")) {
			return MOUSE_MOVED;
		} else if (type.equals("mousewheel")) {
			return MOUSE_WHEEL;
		} else if (type.equals("mouseover")) {
			return MOUSE_ENTERED;
		} else if (type.equals("mouseout")) {
			return MOUSE_EXITED;
		} else return 0;		
    }

	public static AbstractEvent wrapEvent(NativeEvent nativeEvent) {
		return new MouseEvent(nativeEvent);
	}

	@Override
	public Point getPoint() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return new Point(current.event.getClientX(),current.event.getClientY());
	}

	@Override
	public boolean isAltDown() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.getAltKey();
	}

	@Override
	public boolean isShiftDown() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.getShiftKey();
	}

	public static NativeEvent getEvent(AbstractEvent e) {
		return MouseEvent.pool.get(e.getID()).event;
	}

	@Override
	public void release(int l) {
		MouseEvent.pool.remove(l);
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public int getX() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.getClientX();
	}

	@Override
	public int getY() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.getClientY();
	}

	@Override
	public boolean isRightClick() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return (current.event.getButton() == NativeEvent.BUTTON_RIGHT);
	}

	@Override
	public boolean isControlDown() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.getCtrlKey();
	}

	@Override
	public int getClickCount() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		if (current.event.getType() == "doubleclick") {
			return 2;
		} else {
			return 1;
		}
	}

	@Override
	public boolean isMetaDown() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.getMetaKey();
	}

	@Override
	public double getWheelRotation() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return current.event.getRotation();
	}

	@Override
	public boolean isMiddleClick() {
		MouseEvent current = MouseEvent.pool.get(this.id);
		return (current.event.getButton() == NativeEvent.BUTTON_MIDDLE);
	}

}
