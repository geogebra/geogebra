package org.geogebra.common.kernel.commands.redefinition;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.plugin.GeoClass;

public class RuleCollectionSymbolic extends RuleCollection {

	public RuleCollectionSymbolic(RedefinitionRule... rules) {
		super(rules);
	}

	@Override
	public boolean allowed(GeoElement from, GeoElement to) {
		if (from instanceof GeoList && to instanceof GeoList) {
			return allowed(getElementType((GeoList) from),
					((GeoList) to).getElementType());
		}
		return allowed(from.getGeoClassType(), to.getGeoClassType());
	}

	private GeoClass getElementType(GeoList from) {
		return from.size() == 0 ? GeoClass.NUMERIC : from.getElementType();
	}
}
