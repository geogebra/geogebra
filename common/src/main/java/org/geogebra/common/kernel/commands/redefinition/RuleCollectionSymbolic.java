package org.geogebra.common.kernel.commands.redefinition;

import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

public class RuleCollectionSymbolic extends RuleCollection {

	public RuleCollectionSymbolic(RedefinitionRule... rules) {
		super(rules);
	}

	@Override
	public boolean allowed(GeoElementND from, GeoElementND to) {
		if (from instanceof GeoList && to instanceof GeoList) {
			GeoList toList = (GeoList) to;
			return toList.size() == 0
					|| allowed(getElementType((GeoList) from), toList.getElementType());
		}
		return allowed(from.getGeoClassType(), to.getGeoClassType());
	}

	private GeoClass getElementType(GeoList from) {
		return from.size() == 0 ? GeoClass.NUMERIC : from.getElementType();
	}
}
