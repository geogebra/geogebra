package org.geogebra.common.cas;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.cas.giac.CASgiac;

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

	public void memmorize(String s) {
		this.answers.add(s);
	}
}
