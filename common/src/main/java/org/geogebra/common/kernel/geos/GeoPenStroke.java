package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.GeoClass;

/**
 * Class for polylines created using pen
 * @author Michael
 */
public class GeoPenStroke extends GeoPolyLine {

	/**
	 * @param cons constraction
	 * @param points vertices
	 */
	public GeoPenStroke(Construction cons, GeoPointND[] points) {
		super(cons, points);
	}

	/**
	 * @param cons1 construction
	 */
	public GeoPenStroke(Construction cons1) {
		super(cons1);
		setVisibleInView3D(false);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.PENSTROKE;
	}
	
	@Override
	public String toString(StringTemplate tpl) {
		return label;
	}
	
	@Override
	public GeoElement copyInternal(Construction cons1) {
		GeoPolyLine ret = new GeoPenStroke(cons1);
		ret.points = GeoElement.copyPoints(cons1, points);
		ret.set(this);

		return ret;
	}
	
	@Override
	public boolean isPinnable() {
		return true;
	}
	
	@Override 
	public boolean isLabelVisible() { 
		return false; 
	}

	@Override
	public boolean isLabelShowable() {
		return false;
	}

	@Override
	public boolean needToShowBothRowsInAV() {
		return false;
	}

}
