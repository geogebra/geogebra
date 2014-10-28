package geogebra.common.gui.dialog.handler;

import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.main.App;
import geogebra.common.util.AsyncOperation;

/**
 * Handler of a number, with possibility of changing the sign
 * 
 * @author mathieu
 * 
 */
public class NumberChangeSignInputHandler extends NumberInputHandler {
	public NumberChangeSignInputHandler(AlgebraProcessor algebraProcessor, AsyncOperation callback, App app, boolean oldVal) {
		super(algebraProcessor, callback, app, oldVal);
	}
	
  /**
   * If (changeSign==true), change sign of the number handled
   * 
   * @param inputString
   * @param changeSign
   * @return number handled
   */
  public boolean processInput(String inputString, boolean changeSign) {
    if (changeSign) {
      StringBuilder sb = new StringBuilder();
      sb.append("-(");
      sb.append(inputString);
      sb.append(")");
      return processInput(sb.toString());
    }
    
		return processInput(inputString);
  }
}
