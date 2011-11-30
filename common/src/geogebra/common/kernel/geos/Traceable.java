package geogebra.common.kernel.geos;

/**
 * @author Markus Hohenwarter
 */
public interface Traceable {
	
	public boolean getTrace();
	public void setTrace(boolean flag);
	public void updateRepaint();

}
