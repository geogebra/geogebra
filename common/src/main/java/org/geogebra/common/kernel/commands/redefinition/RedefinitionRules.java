package org.geogebra.common.kernel.commands.redefinition;

import org.geogebra.common.plugin.GeoClass;

/**
 * Create redefinition rules with this class.
 */
public class RedefinitionRules {

	private static class SameClassRule implements RedefinitionRule {

		@Override
		public boolean allowed(GeoClass fromType, GeoClass toType) {
			return fromType.equals(toType);
		}
	}

	private static class OneWayRule implements RedefinitionRule {

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

	private static class AnyRule implements RedefinitionRule {

		private RedefinitionRule[] rules;

		private AnyRule(RedefinitionRule[] rules) {
			this.rules = rules;
		}

		@Override
		public boolean allowed(GeoClass fromType, GeoClass toType) {
			for (RedefinitionRule rule: rules) {
				if (rule.allowed(fromType, toType)) {
					return true;
				}
			}
			return false;
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

	/**
	 * Rule returns true if any of the redefinition rules return true.
	 *
	 * @param rules rule list
	 * @return redefinition rule
	 */
	public static RedefinitionRule anyRule(RedefinitionRule... rules) {
		return new AnyRule(rules);
	}
}
