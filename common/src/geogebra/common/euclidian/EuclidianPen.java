package geogebra.common.euclidian;

import java.awt.event.MouseEvent;

import geogebra.common.kernel.geos.GeoImage;

public abstract class EuclidianPen {

	public abstract GeoImage getPenGeo();

	public abstract void setPenWritingToExistingImage(boolean b);

	public abstract void setPenGeo(GeoImage geo);

	public abstract void setFreehand(boolean b);

	public abstract void resetPenOffsets();

}
