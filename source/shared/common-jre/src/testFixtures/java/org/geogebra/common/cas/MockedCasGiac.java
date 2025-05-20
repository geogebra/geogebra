package org.geogebra.common.cas;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValidExpression;

/**
 * If the {@link Kernel}'s evaluation mode is symbolic ({@code SymbolicMode.SYMBOLIC_AV}) (e.g.
 * in the CAS app), all inputs have to go through Giac. We cannot really instantiate Giac in
 * {@code common-jre}, so having a mock of Giac allows us to use the symbolic evaluation in
 * {@code common-jre}, keep related tests together and only use desktop tests for features that
 * are heavily depending on CAS.
 */
public final class MockedCasGiac {
	private final Map<String, String> mockedOutputs = new HashMap<>();

	/**
	 * Associate the given input with the mocked output. When the {@link CASgiac#evaluateCAS}
	 * is called for the given input, the corresponding mocked output is returned.
	 * @param input the input expression to memorize
	 * @param mockedOutput the output to be returned
	 * when {@link CASgiac#evaluateCAS} is called for the input
	 */
	public void memorize(String input, String mockedOutput) {
		mockedOutputs.put(input, mockedOutput);
	}

	/**
	 * Apply the mocked cas giac to app by creating a new mocked {@code CASgiac}
	 * that registers itself as the CAS factory for the provided app.
	 * @param app the app to apply the mocked cas giac
	 */
	public void applyTo(AppCommon app) {
		CASgiac casGiac = new CASgiac((CASparser) app.getKernel().getGeoGebraCAS().getCASparser()) {
			@Override
			public String evaluateCAS(String input) {
				if (mockedOutputs.containsKey(input)) {
					return mockedOutputs.get(input);
				}
				throw new Error("No mocked output is provided for input: \"" + input + "\". "
						+ "Please provide one using MockedCasGiac::memorize or @MockedCasValues.");
			}

			@Override
			protected String translateAndEvaluateCAS(ValidExpression exp, StringTemplate tpl) {
				return evaluateCAS(casParser.translateToCAS(exp, StringTemplate.defaultTemplate, this));
			}

			@Override
			public void clearResult() {
				// not needed
			}

			@Override
			public boolean externalCAS() {
				return false;
			}

			@Override
			protected String evaluate(String exp, long timeoutMilliseconds) {
				return "";
			}
		};
		app.setCASFactory(new CASFactory() {
			@Override
			public CASGenericInterface newGiac(CASparser parser, Kernel kernel) {
				return casGiac;
			}
		});
	}
}
