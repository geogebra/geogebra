package geogebra.web.euclidian.event;

import geogebra.html5.event.HasOffsets;
import geogebra.html5.event.PointerEvent;

import java.util.LinkedList;

public class ZeroOffset implements HasOffsets {

	public int getXoffset() {
	    return 0;
    }

	public int getYoffset() {
	    return 0;
    }

	public boolean isOffsetsUpToDate() {
	    return true;
    }

	public void updateOffsets() {
	    // nothing to update
    }
	public static final ZeroOffset instance = new ZeroOffset();
	private LinkedList<PointerEvent> mousePool = new LinkedList<PointerEvent>();
	public LinkedList<PointerEvent> getMouseEventPool() {
	    return mousePool;
    }
	private LinkedList<PointerEvent> touchPool = new LinkedList<PointerEvent>();
	public LinkedList<PointerEvent> getTouchEventPool() {
	    return touchPool;
    }

	public float getWidthScale() {
	    return 1;
    }

	public float getHeightScale() {
		return 1;
    }

	public float getScaleX() {
	    return 1;
    }

	public float getScaleY() {
	    return 1;
    }

	public int mouseEventX(int clientX) {
	    return clientX;
    }

	public int mouseEventY(int clientY) {
	    return clientY;
    }

	public int getEvID() {
	    return 0;
    }
}
