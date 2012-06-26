package geogebra.web.euclidian.event;

public interface HasOffsets {
	public int getXoffset();
	public int getYoffset();
	public boolean isOffsetsUpToDate();
	public void updateOffsets();	
}
