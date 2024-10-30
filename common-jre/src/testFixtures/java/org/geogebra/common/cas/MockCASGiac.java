package org.geogebra.common.cas;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.factories.CASFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;

public class MockCASGiac extends CASgiac {

	private final List<String> answers = new ArrayList<>();

	/**
	 * Creates new Giac CAS
	 * @param casParser parser
	 */
	public MockCASGiac(CASparser casParser) {
		super(casParser);
	}

	@Override
	public String evaluateCAS(String exp) {
		return answers.remove(0);
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
	 * Add response to a queue. These will be dequeued by {@link #evaluateCAS(String)}
	 * in the order they were memorized.
	 * @param response mocked Giac response
	 */
	public void memorize(String response) {
		this.answers.add(response);
	}

	/**
	 * Creates a mock and registers the necessary factory in the test app
	 * @param app application
	 * @return new mock of Giac CAS
	 */
	public static MockCASGiac init(AppCommon app) {
		MockCASGiac
				giac = new MockCASGiac((CASparser) app.getKernel().getGeoGebraCAS().getCASparser());
		app.setCASFactory(new CASFactory() {
			@Override
			public CASGenericInterface newGiac(CASparser parser, Kernel kernel) {
				return giac;
			}
		});
		return giac;
	}
}
