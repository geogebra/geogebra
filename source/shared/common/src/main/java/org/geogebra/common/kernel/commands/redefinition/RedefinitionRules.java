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

import org.geogebra.common.plugin.GeoClass;

/**
 * Create redefinition rules with this class.
 */
public class RedefinitionRules {

	private static final class SameClassRule implements RedefinitionRule {

		@Override
		public boolean allowed(GeoClass fromType, GeoClass toType) {
			return fromType.equals(toType) && fromType != GeoClass.DEFAULT;
		}
	}

	private static final class OneWayRule implements RedefinitionRule {

		private GeoClass fromType;
		private GeoClass toType;

		private OneWayRule(GeoClass fromType, GeoClass toType) {
			this.fromType = fromType;
			this.toType = toType;
		}

		@Override
		public boolean allowed(GeoClass fromType, GeoClass toType) {
			return fromType.equals(this.fromType) && toType.equals(this.toType);
		}
	}

	/**
	 * Rule returns true if the classes are the same.
	 *
	 * @return redefinition rule
	 */
	public static RedefinitionRule sameClassRule() {
		return new SameClassRule();
	}

	/**
	 * Rule returns true for this conversion instance.
	 *
	 * @param fromClass redefinition from class
	 * @param toClass to class
	 * @return redefinition rule.
	 */
	public static RedefinitionRule oneWayRule(GeoClass fromClass, GeoClass toClass) {
		return new OneWayRule(fromClass, toClass);
	}
}
