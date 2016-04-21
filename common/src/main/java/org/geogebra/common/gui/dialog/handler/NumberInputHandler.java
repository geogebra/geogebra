package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AsyncOperation;

public class NumberInputHandler implements InputHandler {
  private AlgebraProcessor algebraProcessor;
  private GeoNumberValue num = null;
	private AsyncOperation<GeoNumberValue> callback;
  private boolean oldVal;
  private App app;
  
  public NumberInputHandler(AlgebraProcessor algebraProcessor) {
  	super();
  	this.algebraProcessor = algebraProcessor;
  }

	public NumberInputHandler(AlgebraProcessor algebraProcessor,
			AsyncOperation<GeoNumberValue> cb, App appl, boolean oldValue) {
		this(algebraProcessor);
		callback = cb;
		oldVal = oldValue;
		app = appl;
  }
  
	public void processInput(String inputString,
			final AsyncOperation<Boolean> callback0) {
		try {
			algebraProcessor.processAlgebraCommandNoExceptionHandling(
					inputString, false, false, false, true,
					new AsyncOperation<GeoElement[]>() {

						@Override
						public void callback(GeoElement[] result) {
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
								algebraProcessor.showError("NumberExpected");
							}
							if (callback0 != null) {
								boolean currentVal = cons
										.isSuppressLabelsActive();
								cons.setSuppressLabelCreation(false);
								callback0.callback(success);
								cons.setSuppressLabelCreation(currentVal);
							}
							return;

						}
					});
		} catch (Throwable e) {
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
