package org.geogebra.common.util;

import org.geogebra.common.main.App;

public class Validation {
	private static Validation validation = new Validation();

	public static double validateDouble(TextObject tf, double def) {
		return validation.new DoubleValidator().validateDouble(tf, def);
	}

	public static double validateDoublePositive(TextObject tf, double def) {
		DoubleValidator dv = validation.new DoubleValidator() {
			@Override
			protected boolean checkInterval(double val) {
				return val > 0;
			}
		};
		return dv.validateDouble(tf, def);
	}

	private class DoubleValidator {

		private double validateDouble(TextObject tf, double def) {
			double val = Double.NaN;
			try {
				val = Double.parseDouble(tf.getText());
			} catch (NumberFormatException e) {
				App.debug("invalid number:" + tf.getText());
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
