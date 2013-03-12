package geogebra.common.gui.dialog.handler;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;

public class NumberInputHandler implements InputHandler {
  private AlgebraProcessor algebraProcessor;
  private GeoNumberValue num = null;
  
  public NumberInputHandler(AlgebraProcessor algebraProcessor) {
  	super();
  	this.algebraProcessor = algebraProcessor;
  }

  public boolean processInput(String inputString) {
    GeoElement[] result = algebraProcessor.processAlgebraCommand(inputString, false);
    boolean success = result != null && result[0].isNumberValue();
    if (success) {
      setNum((GeoNumberValue) result[0]);
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
