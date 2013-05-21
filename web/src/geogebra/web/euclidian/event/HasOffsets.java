package geogebra.web.euclidian.event;

import java.util.LinkedList;

public interface HasOffsets {
	public int getXoffset();
	public int getYoffset();
	public boolean isOffsetsUpToDate();
	public void updateOffsets();	
	public LinkedList<MouseEventW> getMouseEventPool();
	public LinkedList<TouchEvent> getTouchEventPool();
}
