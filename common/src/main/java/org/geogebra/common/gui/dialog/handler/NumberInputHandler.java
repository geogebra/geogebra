package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

public class NumberInputHandler implements InputHandler {
	private AlgebraProcessor algebraProcessor;
	private GeoNumberValue num = null;
	private AsyncOperation<GeoNumberValue> callback;
	private boolean oldVal;
	private App app;
	private EvalInfo evalInfo;

	/**
	 * @param algebraProcessor
	 *            algebra processor
	 */
	public NumberInputHandler(AlgebraProcessor algebraProcessor) {
		super();
		this.algebraProcessor = algebraProcessor;
		this.app = algebraProcessor.getKernel().getApplication();
		this.evalInfo = createEvalInfo(algebraProcessor.getConstruction());
	}

	/**
	 * @param algebraProcessor
	 *            algebra processor
	 * @param cb
	 *            callback
	 * @param appl
	 *            app
	 * @param oldValue
	 *            old value
	 */
	public NumberInputHandler(AlgebraProcessor algebraProcessor,
			AsyncOperation<GeoNumberValue> cb, App appl, boolean oldValue) {
		this(algebraProcessor);
		callback = cb;
		oldVal = oldValue;
		app = appl;
	}

	private EvalInfo createEvalInfo(Construction cons) {
		return new EvalInfo(!cons.isSuppressLabelsActive(), true)
				.withSliders(true)
				.addDegree(app.getKernel().getAngleUnitUsesDegrees())
				.withSymbolicMode(SymbolicMode.NONE);
	}

	@Override
	public void processInput(String inputString, final ErrorHandler handler,
			final AsyncOperation<Boolean> callback0) {
		try {
			handler.resetError();
			algebraProcessor.processAlgebraCommandNoExceptionHandling(
					inputString, false, handler, evalInfo,
					new AsyncOperation<GeoElementND[]>() {

						@Override
						public void callback(GeoElementND[] result) {
							boolean success = result != null
									&& result[0] instanceof GeoNumberValue;
							Construction cons = algebraProcessor.getKernel()
									.getConstruction();
							if (success) {
								setNum((GeoNumberValue) result[0]);
								if (callback != null) {
									cons.setSuppressLabelCreation(oldVal);
									callback.callback(num);
								}
							} else {
								handler.showError(
										Errors.NumberExpected.getError(app.getLocalization()));
							}
							if (callback0 != null) {
								callback0.callback(success);
							}
						}
					});
		} catch (Throwable e) {
			if (callback0 != null) {
				callback0.callback(false);
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setNum(GeoNumberValue num) {
		this.num = num;
	}

	public GeoNumberValue getNum() {
		return num;
	}

}
