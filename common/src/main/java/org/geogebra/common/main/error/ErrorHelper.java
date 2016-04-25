package org.geogebra.common.main.error;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.commands.MyException;
import org.geogebra.common.main.BracketsError;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyParseError;
import org.geogebra.common.util.debug.Log;

public class ErrorHelper {
	public static void handleException(Exception e, Localization loc,
			ErrorHandler handler) {
		e.printStackTrace();
		if (e instanceof CircularDefinitionException) {
			handler.showError(loc.getError("CircularDefinition"));
		} else if (e instanceof MyException) {
			handler.showError(loc.getError("InvalidInput") + ":\n"
					+ ((MyException) e).getInput());
		} else {
			handler.showError(loc.getError("InvalidInput"));
		}

	}

	public static void handleError(MyError e, String cmd, Localization loc,
			ErrorHandler handler) {
		if(e instanceof BracketsError) {
			handleException(
					new MyException(e, MyException.IMBALANCED_BRACKETS, cmd),
					loc, handler);
		} else if  (e instanceof MyParseError) {
			// this is thrown from eg a=1; a(2,2)
			handleException(new MyException(e, MyException.INVALID_INPUT, cmd),
					loc, handler);
		} else {
			handler.showError(e.getLocalizedMessage());
		}
	}

	public static ErrorHandler silent() {
		return new ErrorHandler() {

			public void showError(String msg) {
				Log.printStacktrace(msg);
				// TODO Auto-generated method stub

			}

			public void setActive(boolean b) {
				// TODO Auto-generated method stub

			}

			public void showCommandError(String command, String message) {
				// TODO Auto-generated method stub

			}

		};
	}


}
