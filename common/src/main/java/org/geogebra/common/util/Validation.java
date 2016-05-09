package org.geogebra.common.util;

import org.geogebra.common.util.debug.Log;

public class Validation {

	public static double validateDouble(TextObject tf, double def) {
		return new DoubleValidator().validateDouble(tf, def);
	}

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

		public DoubleValidator() {
		}

		public double validateDouble(TextObject tf, double def) {
			double val = Double.NaN;
			try {
				val = Double.parseDouble(tf.getText());
			} catch (NumberFormatException e) {
				Log.debug("invalid number:" + tf.getText());
			}
			if (!Double.isNaN(val) && !Double.isInfinite(val)
					&& checkInterval(val))
				return val;
			tf.setText(def + "");
			return def;
		}

		protected boolean checkInterval(double val) {
			return true;
		}
	}

}
