package org.geogebra;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.cas.CASparser;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.settings.config.AppConfigCas;
import org.geogebra.desktop.cas.giac.CASgiacD;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Developer tool for extracting CAS input and output pairs for mocking in tests.
 * This class should have a single test that ideally matches the test that needs mocked CAS values.
 * If this test contains the same number and order of input evaluation using the
 * {@link CasValueExtractor#evaluate(String)}, then it will return all the mocked CAS values needed
 * for the corresponding test in order without repetition.
 * Running the test will print out the input-output pairs in a convenient format to use, set by
 * {@link CasValueExtractor#printFormat}.
 */
@Disabled
public class CasValueExtractor {
	private static final Format printFormat = Format.MOCKED_CAS_VALUES_ANNOTATION;

	@ParameterizedTest
	@ValueSource(strings = {
			// Restricted inequalities
			"x > 0",
			"y <= 1",
			"x < y",
			"x - y > 2",
			"x^2 + 2y^2 < 1",
			"f: x > 0",
			"f(x) = x > 2",
			// Restricted integrals
			"Integral(g, -5, 5)",
			"Integral(g, x, -5, 5)",
			"NIntegral(g, -5, 5)",
			// Restricted vectors
			"a = (1, 2)",
			"b = (1, 2) + 0",
			// Restricted implicit curves
			"x^2 = 1",
			"2^x = 2",
			"sin(x) = 0",
			"y - x^2 = 0",
			"x^2 = y",
			"x^2 + y^2 = 4",
			"x^2 / 9 + y^2 / 4 = 1",
			"x^2 - y^2 = 4",
			"x^3 + y^2 = 2",
			"y^3 = x",
			// Restricted lines
			"x = 0",
			"x + y = 0",
			"2x - 3y = 4",
	})
	public void extractMockValues(String input) {
		evaluate("g(x) = x");
		evaluate(input);
	}

	@BeforeEach
	public void setup() {
		app.getKernel().setPrintDecimals(13);
	}

	@AfterAll
	public static void printValues() {
		printValues(CasGiacMock.getValues(), printFormat);
	}

	/**
	 * The format to be used when printing the input-output pairs at the end of the test.
	 */
	private enum Format {
		/**
		 * Input-output pairs in a {@code @MockedCasValues} annotation.
		 */
		MOCKED_CAS_VALUES_ANNOTATION,
		/**
		 * {@link org.geogebra.common.cas.MockedCasGiac#memorize} calls.
		 */
		MEMORIZE_METHOD_CALL,
		/**
		 * Raw input and output separated by {@code ->}.
		 */
		RAW,
	}

	private static void printValues(List<Map.Entry<String, String>> values, Format format) {
		switch (format) {
		case MOCKED_CAS_VALUES_ANNOTATION:
			String longestInput = values.stream()
					.map(Map.Entry::getKey)
					.max(Comparator.comparingInt(String::length))
					.orElse("");
			// Input value including starting " and closing space
			int maxInputLength = longestInput.length() + 2;
			// Input length rounded up to next multiple of 4 (default tab length)
			int outputStart = (maxInputLength + 3) / 4 * 4;

			System.out.println("@MockedCasValues({");
			values.forEach(value -> System.out.println(
					// Input with default 2 tabs of indentation
					"\t\t\"" + value.getKey() + " "
					// A number of tabs to align all the "->" delimiters
					+ "\t".repeat((outputStart - value.getKey().length() + 1) / 4) + "-> "
					// Output
					+ value.getValue() + "\","
			));
			System.out.println("})");

			break;
		case MEMORIZE_METHOD_CALL:
			for (Map.Entry<String, String> value : values) {
				System.out.println("mockedCasGiac.memorize(\"" + value.getKey() + "\", "
						+ "\"" + value.getValue() + "\");");
			}
			break;
		case RAW:
			for (Map.Entry<String, String> value : values) {
				System.out.println(value.getKey() + " -> " + value.getValue());
			}
		}
	}

	private final AppCommon app = AppCommonFactory.create(
			new AppConfigCas(GeoGebraConstants.SUITE_APPCODE));
	private final CasGiacMock casGiacMock = new CasGiacMock(app);
	private final AlgebraProcessor algebraProcessor = app.getKernel().getAlgebraProcessor();
	private final ErrorAccumulator errorAccumulator = new ErrorAccumulator();

	private static final class CasGiacMock extends CASgiacD {
		private static final List<String> inputOrder = new ArrayList<>();
		private static final Map<String, String> inputOutputPairs = new HashMap<>();

		public CasGiacMock(AppCommon app) {
			super((CASparser) app.getKernel().getGeoGebraCAS().getCASparser());
			app.setCASFactory(new CASFactory() {
				@Override
				public CASGenericInterface newGiac(CASparser parser, Kernel kernel) {
					return CasGiacMock.this;
				}
			});
		}

		@Override
		protected String translateAndEvaluateCAS(ValidExpression casInput, StringTemplate tpl) {
			String input = casParser.translateToCAS(casInput, StringTemplate.defaultTemplate, this);
			if (!inputOrder.contains(input)) {
				inputOrder.add(input);
			}
			String output = super.translateAndEvaluateCAS(casInput, tpl);
			inputOutputPairs.put(input, output);
			return output;
		}

		public static List<Map.Entry<String, String>> getValues() {
			return inputOrder.stream()
					.map(input -> entry(input, inputOutputPairs.get(input)))
					.collect(Collectors.toList());
		}
	}

	private GeoElementND[] evaluate(String expression) {
		EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
		return algebraProcessor.processAlgebraCommandNoExceptionHandling(
				expression, false, errorAccumulator, evalInfo, null);
	}
}
