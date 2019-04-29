package org.geogebra.common.factories;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.CASGenericInterface;

public class CASDummy extends CASgiac implements CASGenericInterface {

	public CASDummy(CASparser casParser) {
		super(casParser);
	}

	@Override
	public void clearResult() {
		//
	}

	@Override
	public boolean externalCAS() {
		return false;
	}

	@Override
	public String evaluateCAS(String exp) {
		return "?";
	}

	@Override
	protected String evaluate(String exp, long timeoutMilliseconds)
			throws Throwable {
		return "?";
	}

}
