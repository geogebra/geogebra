package geogebra.html5.event;

import geogebra.web.euclidian.event.MouseEventW;

import java.util.LinkedList;

public interface HasOffsets {
	public int getXoffset();
	public int getYoffset();
	public float getWidthScale();
	public float getHeightScale();
	public float getScaleX();
	public float getScaleY();
	public boolean isOffsetsUpToDate();
	public void updateOffsets();	
	public LinkedList<MouseEventW> getMouseEventPool();
	public LinkedList<PointerEvent> getTouchEventPool();
}
