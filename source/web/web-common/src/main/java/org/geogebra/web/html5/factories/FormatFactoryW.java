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

		private FastFormatAdapter(int d) {
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
		return getNumberFormat("", digits);
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
