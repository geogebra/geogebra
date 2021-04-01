package org.geogebra.web.html5.factories;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.util.NumberFormatAdapter;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.web.html5.util.MyNumberFormat;
import org.geogebra.web.html5.util.NumberFormatW;
import org.geogebra.web.html5.util.ScientificFormat;

public class FormatFactoryW extends FormatFactory {
	private static final class FastFormatAdapter
			implements ScientificFormatAdapter {
		private int d;

		protected FastFormatAdapter(int d) {
			this.d = d;
		}

		@Override
		public String format(double x) {
			return MyNumberFormat.toPrecision(x, this.d).replace("e", "E");
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

	@Override
	public NumberFormatAdapter getNumberFormat(int digits) {
		switch (digits) {
		case 0:
			return new NumberFormatW("0.", digits);
		case 1:
			return new NumberFormatW("0.#", digits);
		case 2:
			return new NumberFormatW("0.##", digits);
		case 3:
			return new NumberFormatW("0.###", digits);
		case 4:
			return new NumberFormatW("0.####", digits);
		case 5:
			return new NumberFormatW("0.#####", digits);
		case 6:
			return new NumberFormatW("0.######", digits);
		case 7:
			return new NumberFormatW("0.#######", digits);
		case 8:
			return new NumberFormatW("0.########", digits);
		case 9:
			return new NumberFormatW("0.#########", digits);
		case 10:
			return new NumberFormatW("0.##########", digits);
		case 11:
			return new NumberFormatW("0.###########", digits);
		case 12:
			return new NumberFormatW("0.############", digits);
		case 13:
			return new NumberFormatW("0.#############", digits);
		case 14:
			return new NumberFormatW("0.##############", digits);
		default:
			return new NumberFormatW("0.###############", digits);
		}
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
