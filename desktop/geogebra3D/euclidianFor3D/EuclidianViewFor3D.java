package geogebra3D.euclidianFor3D;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.euclidian.Drawable;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.geos.GeoAngle;

/**
 * Simple extension of EuclidianView to implement handling of 3D objects
 * @author matthieu
 *
 */
public class EuclidianViewFor3D extends EuclidianView {

	public EuclidianViewFor3D(EuclidianController ec, boolean[] showAxes,
			boolean showGrid, int evno) {
		super(ec, showAxes, showGrid, evno);
		
		//Application.debug("ici");
	} 
	
	@Override
	protected Drawable newDrawable(GeoElement geo) {
		
		//first try super method
		Drawable d = super.newDrawable(geo);
		if (d!=null)
			return d;
		
		//try 3D geos
		switch (geo.getGeoClassType()) {
		case ANGLE3D:
			d = new DrawAngleFor3D(this, (GeoAngle) geo);
			break;
		}

		return d;		
	}

} 
