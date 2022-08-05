package org.geogebra.web.html5.factories;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.web.html5.util.NumberFormatW;
import org.geogebra.web.html5.util.ScientificFormat;

import elemental2.core.JsNumber;
import jsinterop.base.Js;

public class FormatFactoryW extends FormatFactory {
	private static final class FastFormatAdapter
			implements ScientificFormatAdapter {
		private int d;

		protected FastFormatAdapter(int d) {
			this.d = d;
		}

		@Override
		public String format(double x) {
			return FormatFactoryW.toPrecision(x, d);
		}

		@Override
		public int getSigDigits() {
			return d;
		}

		@Override
		public void setSigDigits(int sigDigits) {
			d = sigDigits;
		}

		@Override
		public void setMaxWidth(int mWidth) {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * @param x number
	 * @param d digits
	 * @return formatted number
	 */
	public static String toPrecision(double x, int d) {
		JsNumber num = Js.uncheckedCast(x);
		return num.toPrecision(d).replace("e", "E");
	}

	@Override
	public NumberFormatAdapter getNumberFormat(int digits) {
		return new NumberFormatW("", digits);
	}

	@Override
	public NumberFormatAdapter getNumberFormat(String s, int d) {
		return new NumberFormatW(s, d);
	}

	@Override
	public ScientificFormatAdapter getScientificFormat(int a, int b, boolean c) {
		return new ScientificFormat(a, b, c);
	}

	@Override
	public ScientificFormatAdapter getFastScientificFormat(int digits) {
		return new FastFormatAdapter(digits);
	}
}
