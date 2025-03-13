package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.jupiter.api.BeforeEach;

public class BaseAppTest {
	protected final ErrorAccumulator errorAccumulator = new ErrorAccumulator();
	protected AppCommon app;
	protected AlgebraProcessor algebraProcessor;
	private Kernel kernel;

	@BeforeEach
	public void initApp() {
		app = AppCommonFactory.create();
		algebraProcessor = app.getKernel().getAlgebraProcessor();
		kernel = app.getKernel();
		kernel.setPrintDecimals(15);
	}

	public Kernel getKernel() {
		return kernel;
	}

	protected <T extends GeoElementND> T add(String command) {
		return getFirstElement(evaluate(command));
	}

	private <T extends GeoElementND> T getFirstElement(GeoElementND[] geoElements) {
		if (geoElements != null) {
			return geoElements.length == 0 ? null : (T) geoElements[0];
		} else {
			return null;
		}
	}

	protected GeoElementND[] evaluate(String expression) {
		EvalInfo evalInfo = EvalInfoFactory.getEvalInfoForAV(app, false);
		return algebraProcessor.processAlgebraCommandNoExceptionHandling(
				expression, false, errorAccumulator, evalInfo, null);
	}

}
