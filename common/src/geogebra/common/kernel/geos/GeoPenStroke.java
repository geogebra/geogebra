package geogebra.common.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.plugin.GeoClass;

public class GeoPenStroke extends GeoPolyLine {

	public GeoPenStroke(Construction cons, GeoPointND[] points) {
		super(cons, points);
	}

	public GeoPenStroke(Construction cons1) {
		super(cons1);
	}

	@Override
	public String getTypeString() {
		return "PenStroke";
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
	
	//@Override
	public int xxgetTooltipMode() {
		return TOOLTIP_OFF;
	}


}
