package org.geogebra.common.kernel.commands.redefinition;

import java.util.Arrays;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.GeoClass;

public class RuleCollection {

	private RedefinitionRule[] rules;

	public RuleCollection(RedefinitionRule... rules) {
		this.rules = rules;
	}

	public boolean allowed(GeoElement from, GeoElement to) {
		return allowed(from.getGeoClassType(), to.getGeoClassType());
	}

	protected boolean allowed(GeoClass fromType, GeoClass toType) {
		return Arrays.stream(rules).anyMatch(rule -> rule.allowed(fromType, toType));
	}
}
