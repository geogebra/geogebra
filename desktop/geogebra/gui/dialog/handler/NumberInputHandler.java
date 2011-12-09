package geogebra.gui.dialog.handler;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.gui.InputHandler;
import geogebra.kernel.commands.AlgebraProcessor;

public class NumberInputHandler implements InputHandler {
  private AlgebraProcessor algebraProcessor;
  private NumberValue num = null;
  
  public NumberInputHandler(AlgebraProcessor algebraProcessor) {
  	super();
  	this.algebraProcessor = algebraProcessor;
  }

  public boolean processInput(String inputString) {
    GeoElement[] result = (GeoElement[]) algebraProcessor.processAlgebraCommand(inputString, false);
    boolean success = result != null && result[0].isNumberValue();
    if (success) {
      setNum((NumberValue) result[0]);
    }
    return success;
  }

  public void setNum(NumberValue num) {
    this.num = num;
  }

  public NumberValue getNum() {
    return num;
  }
}
