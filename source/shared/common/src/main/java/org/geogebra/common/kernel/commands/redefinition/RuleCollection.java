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

import java.util.Arrays;

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

public class RuleCollection {

	private RedefinitionRule[] rules;

	public RuleCollection(RedefinitionRule... rules) {
		this.rules = rules;
	}

	/**
	 * @param from original element
	 * @param to new element
	 * @return whether redefinition from original to new is allowed
	 */
	public boolean allowed(GeoElementND from, GeoElementND to) {
		return allowed(from.getGeoClassType(), to.getGeoClassType());
	}

	protected boolean allowed(GeoClass fromType, GeoClass toType) {
		return Arrays.stream(rules).anyMatch(rule -> rule.allowed(fromType, toType));
	}
}
