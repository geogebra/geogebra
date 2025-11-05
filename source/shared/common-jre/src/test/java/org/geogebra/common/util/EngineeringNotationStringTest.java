package org.geogebra.common.util;

import static org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType.GEOGEBRA;
import static org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType.LATEX;
import static org.geogebra.common.util.EngineeringNotationString.format;
import static org.geogebra.editor.share.util.Unicode.CENTER_DOT;
import static org.geogebra.editor.share.util.Unicode.SUPERSCRIPT_0;
import static org.geogebra.editor.share.util.Unicode.SUPERSCRIPT_1;
import static org.geogebra.editor.share.util.Unicode.SUPERSCRIPT_2;
import static org.geogebra.editor.share.util.Unicode.SUPERSCRIPT_3;
import static org.geogebra.editor.share.util.Unicode.SUPERSCRIPT_6;
import static org.geogebra.editor.share.util.Unicode.SUPERSCRIPT_9;
import static org.geogebra.editor.share.util.Unicode.SUPERSCRIPT_MINUS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class EngineeringNotationStringTest extends BaseAppTestSetup {
	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@SuppressWarnings({"checkstyle:RegexpSinglelineCheck", "checkstyle:LineLengthCheck"})
	@ParameterizedTest
	@CsvSource({
			// Value, 		Expected GeoGebra format, 																				Expected LaTeX format, 		Precision
			"12, 			12 " + CENTER_DOT + " 10" + SUPERSCRIPT_0 + ", 															12 \\cdot 10^{0}, 			5",
			"123456, 		123.456 " + CENTER_DOT + " 10" + SUPERSCRIPT_3 + ", 													123.456 \\cdot 10^{3}, 		5",
			"-18, 			-18 " + CENTER_DOT + " 10" + SUPERSCRIPT_0 + ", 														-18 \\cdot 10^{0}, 			5",
			"-7654321, 		-7.654321 " + CENTER_DOT + " 10" + SUPERSCRIPT_6 + ", 													-7.654321 \\cdot 10^{6}, 	6",
			"0, 			0 " + CENTER_DOT + " 10" + SUPERSCRIPT_0 + ", 															0 \\cdot 10^{0}, 			5",
			"-0, 			0 " + CENTER_DOT + " 10" + SUPERSCRIPT_0 + ", 															0 \\cdot 10^{0}, 			5",
			"120, 			120 " + CENTER_DOT + " 10" + SUPERSCRIPT_0 + ", 														120 \\cdot 10^{0}, 			5",
			"0.5, 			500 " + CENTER_DOT + " 10" + SUPERSCRIPT_MINUS + SUPERSCRIPT_3 + ", 									500 \\cdot 10^{-3}, 		5",
			"0.01, 			10 " + CENTER_DOT + " 10" + SUPERSCRIPT_MINUS + SUPERSCRIPT_3 + ",										10 \\cdot 10^{-3}, 			5",
			"0.003, 		3 " + CENTER_DOT + " 10" + SUPERSCRIPT_MINUS + SUPERSCRIPT_3 + ", 										3 \\cdot 10^{-3}, 			5",
			"-17.32, 		-17.32 " + CENTER_DOT + " 10" + SUPERSCRIPT_0 + ", 														-17.32 \\cdot 10^{0}, 		5",
			"126.3122, 		126.3122 " + CENTER_DOT + " 10" + SUPERSCRIPT_0 + ", 													126.3122 \\cdot 10^{0}, 	5",
			"5.0001, 		5.0001 " + CENTER_DOT + " 10" + SUPERSCRIPT_0 + ", 														5.0001 \\cdot 10^{0}, 		5",
			"123456789.3, 	123.4567893 " + CENTER_DOT + " 10" + SUPERSCRIPT_6 + ", 												123.4567893 \\cdot 10^{6}, 	7",
			"12345.12345, 	12.34512345 " + CENTER_DOT + " 10" + SUPERSCRIPT_3 + ", 												12.34512345 \\cdot 10^{3}, 	8",
			"1234567890.3, 	1.2345678903 " + CENTER_DOT + " 10" + SUPERSCRIPT_9 + ", 												1.2345678903 \\cdot 10^{9}, 10",
			"0.00007, 		70 " + CENTER_DOT + " 10" + SUPERSCRIPT_MINUS + SUPERSCRIPT_6 + ", 										70 \\cdot 10^{-6}, 			5",
			"0.0001007, 	100.7 " + CENTER_DOT + " 10" + SUPERSCRIPT_MINUS + SUPERSCRIPT_6 + ", 									100.7 \\cdot 10^{-6}, 		5",
			"1E+100, 		10 " + CENTER_DOT + " 10" + SUPERSCRIPT_9 + SUPERSCRIPT_9 + ", 											10 \\cdot 10^{99}, 			5",
			"-1E+100, 		-10 " + CENTER_DOT + " 10" + SUPERSCRIPT_9 + SUPERSCRIPT_9 + ", 										-10 \\cdot 10^{99}, 		5",
			"1E-100, 		100 " + CENTER_DOT + " 10" + SUPERSCRIPT_MINUS + SUPERSCRIPT_1 + SUPERSCRIPT_0 + SUPERSCRIPT_2 + ", 	100 \\cdot 10^{-102}, 		5",
			"-1E-100, 		-100 " + CENTER_DOT + " 10" + SUPERSCRIPT_MINUS + SUPERSCRIPT_1 + SUPERSCRIPT_0 + SUPERSCRIPT_2 + ", 	-100 \\cdot 10^{-102}, 		5",
	})
	public void testEngineeringNotations(double value, String expectedGeoGebraFormat, String expectedLatexFormat, int precision) {
		assertAll(
				() -> assertEquals(expectedGeoGebraFormat,
						format(value, GEOGEBRA, getBaseNumberFormatter(precision))),
				() -> assertEquals(expectedLatexFormat,
						format(value, LATEX, getBaseNumberFormatter(precision))));
	}

	private Function<Double, String> getBaseNumberFormatter(int decimalPlaces) {
		return value -> {
			BigDecimal roundedValue = new BigDecimal(value);
			return Double.toString(
					roundedValue.setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue())
					.replaceAll("0+$", "")
					.replaceAll("\\.$", "");
		};
	}
}
