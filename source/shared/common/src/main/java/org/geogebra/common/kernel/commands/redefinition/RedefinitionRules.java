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
