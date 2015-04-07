package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AsyncOperation;

public class NumberInputHandler implements InputHandler {
  private AlgebraProcessor algebraProcessor;
  private GeoNumberValue num = null;
  private AsyncOperation callback;
  private boolean oldVal;
  private App app;
  
  public NumberInputHandler(AlgebraProcessor algebraProcessor) {
  	super();
  	this.algebraProcessor = algebraProcessor;
  }

  public NumberInputHandler(AlgebraProcessor algebraProcessor, AsyncOperation cb,
		  App appl, boolean oldValue) {
	this(algebraProcessor);
	callback = cb;
	oldVal = oldValue;
	app = appl;
	
  }
  
  public boolean processInput(String inputString) {
    GeoElement[] result = algebraProcessor.processAlgebraCommand(inputString, false);
    boolean success = result != null && result[0] instanceof GeoNumberValue;
    if (success) {
      setNum((GeoNumberValue) result[0]);
      if (callback != null){
    	  app.getKernel().getConstruction().setSuppressLabelCreation(oldVal);
    	  callback.callback(num);
      }
    }
    else{
    	algebraProcessor.showError("NumberExpected");
    }
    return success;
  }

  public void setNum(GeoNumberValue num) {
    this.num = num;
  }

  public GeoNumberValue getNum() {
    return num;
  }
}
