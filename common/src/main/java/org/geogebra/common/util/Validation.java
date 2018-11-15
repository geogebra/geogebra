package org.geogebra.common.util;

import org.geogebra.common.util.debug.Log;

public class Validation {

	/**
	 * @param tf
	 *            textfield
	 * @param def
	 *            default
	 * @return whether the field contains a double
	 */
	public static double validateDouble(TextObject tf, double def) {
		return new DoubleValidator().validateDouble(tf, def);
	}

	/**
	 * @param tf
	 *            textfield
	 * @param def
	 *            default
	 * @return whether the field contains a positive double
	 */
	public static double validateDoublePositive(TextObject tf, double def) {
		DoubleValidator dv = new DoubleValidator() {
			@Override
			protected boolean checkInterval(double val) {
				return val > 0;
			}
		};
		return dv.validateDouble(tf, def);
	}

	private static class DoubleValidator {

		protected DoubleValidator() {
		}

		public double validateDouble(TextObject tf, double def) {
			double val = Double.NaN;
			try {
				val = Double.parseDouble(tf.getText());
			} catch (NumberFormatException e) {
				Log.debug("invalid number:" + tf.getText());
			}
			if (!Double.isNaN(val) && !Double.isInfinite(val)
					&& checkInterval(val)) {
				return val;
			}
			tf.setText(def + "");
			return def;
		}

		/**
		 * @param val
		 *            value to be checked
		 */
		protected boolean checkInterval(double val) {
			return true;
		}
	}

}
