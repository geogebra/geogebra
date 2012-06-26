package geogebra.web.euclidian.event;

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
}
