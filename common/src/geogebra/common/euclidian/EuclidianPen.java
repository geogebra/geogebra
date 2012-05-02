package geogebra.common.euclidian;

import geogebra.common.awt.Color;
import geogebra.common.awt.Point;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.geos.GeoImage;

import java.util.List;
/**
 * Pen for free drawing in EV
 */
public abstract class EuclidianPen {

	/**
	 * @return image used fordrawing
	 */
	public abstract GeoImage getPenGeo();

	/**
	 * @param geo image for drawing
	 * If geo is not null, marks true to mark this as writing to existing image
	 */
	public abstract void setPenGeo(GeoImage geo);

	/**
	 * @param b true to switch to freehand function drawing mode
	 */
	public abstract void setFreehand(boolean b);

	/**
	 * Resets offset
	 */
	public abstract void resetPenOffsets();

	/**
	 * @param e mouse pressed event
	 * @param hits hit objects
	 */
	public abstract void handleMousePressedForPenMode(AbstractEvent e, Hits hits);

	/**
	 * @param event mouse released event
	 */
	public abstract void handleMouseReleasedForPenMode(AbstractEvent event);
	
	/**
	 * Draws bezier curve connecting given points to image
	 * @param ge image
	 * @param list points
	 */
	public abstract void doDrawPoints(GeoImage ge,List<Point> list); 

	public abstract void setPenColor(Color color);
	
	public abstract void setPenSize(int size);

	public abstract int getPenSize();
	
	public abstract Color getPenColorCommon();
}
