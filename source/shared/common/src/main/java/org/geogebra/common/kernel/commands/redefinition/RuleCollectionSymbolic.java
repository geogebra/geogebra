/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
