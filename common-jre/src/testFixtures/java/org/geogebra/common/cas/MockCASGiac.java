package org.geogebra.common.cas;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;

/**
 * If the {@link Kernel}'s evaluation mode is symbolic ({@code SymbolicMode.SYMBOLIC_AV}) (e.g.
 * in the CAS app), all inputs have to go through Giac. We cannot really instantiate Giac in
 * {@code common-jre}, so having a mock of Giac allows us to use the symbolic evaluation in
 * {@code common-jre}, keep related tests together and only use desktop tests for features that
 * are heavily depending on CAS.
 */
public final class MockCASGiac extends CASgiac {

	private final List<String> responses = new ArrayList<>();

	/**
	 * Creates a Giac CAS mock that registers itself as the CAS factory for the app.
	 */
	public MockCASGiac(AppCommon app) {
		super((CASparser) app.getKernel().getGeoGebraCAS().getCASparser());
		app.setCASFactory(new CASFactory() {
			@Override
			public CASGenericInterface newGiac(CASparser parser, Kernel kernel) {
				return MockCASGiac.this;
			}
		});
	}

	public MockCASGiac(CASparser casParser) {
		super(casParser);
	}

	@Override
	public String evaluateCAS(String exp) {
		return responses.remove(0);
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
	protected String evaluate(String exp, long timeoutMilliseconds) throws Throwable {
		return "";
	}

	/**
	 * Add {@code response} to the internal queue of responses. These will be returned and
	 * dequeued by {@link #evaluateCAS(String)} in the order they were memorized.
	 * @param response mocked Giac response
	 */
	public void memorize(String response) {
		responses.add(response);
	}
}
