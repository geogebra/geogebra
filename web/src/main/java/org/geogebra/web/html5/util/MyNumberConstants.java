package org.geogebra.web.html5.util;

import com.google.gwt.i18n.client.constants.NumberConstants;
import com.himamis.retex.editor.share.util.Unicode;

public class MyNumberConstants implements NumberConstants {
	@Override
	public String notANumber() {
		return "NaN";
	}

	@Override
	public String currencyPattern() {
		return "\u00A4#,##0.00";
	}

	@Override
	public String decimalPattern() {
		return "#,##0.###";
	}

	@Override
	public String decimalSeparator() {
		return ".";
	}

	@Override
	public String defCurrencyCode() {
		return "USD";
	}

	@Override
	public String exponentialSymbol() {
		return "E";
	}

	@Override
	public String globalCurrencyPattern() {
		return "\u00A4\u00A4\u00A4\u00A4#,##0.00 \u00A4\u00A4";
	}

	@Override
	public String groupingSeparator() {
		return ",";
	}

	@Override
	public String infinity() {
		return Unicode.INFINITY + "";
	}

	@Override
	public String minusSign() {
		return "-";
	}

	@Override
	public String monetaryGroupingSeparator() {
		return ",";
	}

	@Override
	public String monetarySeparator() {
		return ".";
	}

	@Override
	public String percent() {
		return "%";
	}

	@Override
	public String percentPattern() {
		return "#,##0%";
	}

	@Override
	public String perMill() {
		return "\u2030";
	}

	@Override
	public String plusSign() {
		return "+";
	}

	@Override
	public String scientificPattern() {
		return "#E0";
	}

	@Override
	public String simpleCurrencyPattern() {
		return "\u00A4\u00A4\u00A4\u00A4#,##0.00";
	}

	@Override
	public String zeroDigit() {
		return "0";
	}

}
