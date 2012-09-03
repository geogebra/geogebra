package geogebra3D.euclidianFor3D;

import geogebra.common.euclidian.DrawableND;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.euclidian.EuclidianControllerD;
import geogebra.euclidian.EuclidianViewD;

/**
 * Simple extension of EuclidianView to implement handling of 3D objects
 * 
 * @author matthieu
 * 
 */
public class EuclidianViewFor3D extends EuclidianViewD {

	/**
	 * @param ec controller
	 * @param showAxes show the axes
	 * @param showGrid shos the grid
	 * @param evno dock panel id
	 */
	public EuclidianViewFor3D(EuclidianControllerD ec, boolean[] showAxes,
			boolean showGrid, int evno) {
		super(ec, showAxes, showGrid, evno, null);

		// Application.debug("ici");
	}

	@Override
	public DrawableND newDrawable(GeoElement geo) {

		// first try super method
		DrawableND d = super.newDrawable(geo);
		if (d != null) {
			return d;
		}

		// try 3D geos
		switch (geo.getGeoClassType()) {
		case ANGLE3D:
			d = new DrawAngleFor3D(this, (GeoAngle) geo);
			break;
		}

		return d;
	}

}
