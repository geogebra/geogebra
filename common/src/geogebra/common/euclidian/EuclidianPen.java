package geogebra.common.euclidian;

import geogebra.common.awt.Color;
import geogebra.common.awt.Point;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.main.AbstractApplication;

import java.util.List;
/**
 * Pen for free drawing in EV
 */
public abstract class EuclidianPen {

	/**
	 * @param geo image or PolyLine for drawing
	 * If geo is not null, marks true to mark this as writing to existing image
	 */
	public abstract void setPenGeo(GeoElement geo);

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
	 * Changes pen color
	 * @param color new color
	 */
	public abstract void setPenColor(Color color);
	/**
	 * Changes pen size
	 * @param size new size in pixels
	 */
	public abstract void setPenSize(int size);

	/**
	 * @return pen size in pixels
	 */
	public abstract int getPenSize();
	
	/**
	 * @return pen color as geogebra.common.awt.Color
	 */
	public abstract Color getPenColorCommon();

	protected boolean absoluteScreenPosition;
	
	public void setAbsoluteScreenPosition(boolean b) {
		absoluteScreenPosition = b;
		
	}
}
