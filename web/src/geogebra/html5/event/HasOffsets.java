package geogebra.html5.event;

import java.util.LinkedList;

public interface HasOffsets {
	public int getXoffset();
	public int getYoffset();
	/*public float getWidthScale();
	public float getHeightScale();
	public float getScaleX();
	public float getScaleY();*/
	public boolean isOffsetsUpToDate();
	public void updateOffsets();	
	public LinkedList<PointerEvent> getMouseEventPool();
	public LinkedList<PointerEvent> getTouchEventPool();
	public int mouseEventX(int clientX);
	public int mouseEventY(int clientY);
	public int getEvID();
}
