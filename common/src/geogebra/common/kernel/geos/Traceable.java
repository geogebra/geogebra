package geogebra.common.kernel.geos;

import geogebra.common.kernel.kernelND.GeoElementND;

/**
 * @author Markus Hohenwarter
 */
public interface Traceable extends GeoElementND{
	/**
	 * @return true if tracing
	 */
	public boolean getTrace();
	/**
	 * Turn tracing on/off
	 * @param flag true to switch tracing on
	 */
	public void setTrace(boolean flag);
	/**
	 * Update and repaint this element
	 */
	public void updateRepaint();

}
