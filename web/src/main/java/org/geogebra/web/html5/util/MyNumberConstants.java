package org.geogebra.web.html5.util;

import com.himamis.retex.editor.share.util.Unicode;

public class MyNumberConstants {
	public String notANumber() {
		return "NaN";
	}

	public String currencyPattern() {
		return "\u00A4#,##0.00";
	}

	public String decimalPattern() {
		return "#,##0.###";
	}

	public String decimalSeparator() {
		return ".";
	}

	public String defCurrencyCode() {
		return "USD";
	}

	public String exponentialSymbol() {
		return "E";
	}

	public String globalCurrencyPattern() {
		return "\u00A4\u00A4\u00A4\u00A4#,##0.00 \u00A4\u00A4";
	}

	public String groupingSeparator() {
		return ",";
	}

	public String infinity() {
		return Unicode.INFINITY + "";
	}

	public String minusSign() {
		return "-";
	}

	public String monetaryGroupingSeparator() {
		return ",";
	}

	public String percent() {
		return "%";
	}

	public String percentPattern() {
		return "#,##0%";
	}

	public String perMill() {
		return "\u2030";
	}

	public String plusSign() {
		return "+";
	}

	public String scientificPattern() {
		return "#E0";
	}

	public String simpleCurrencyPattern() {
		return "\u00A4\u00A4\u00A4\u00A4#,##0.00";
	}

}
