package org.geogebra.common.main.error;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.commands.MyException;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
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
		} else if (handler.getCurrentCommand() != null) {

			handleCommandError(handler.getCurrentCommand(), handler, loc);
		} else {
			handler.showError(loc.getError("InvalidInput"));
		}

	}

	private static void handleCommandError(String currentCommand,
			ErrorHandler handler, Localization loc) {
		handler.showCommandError(currentCommand,
				loc.getError("InvalidInput") + "\n\n" + loc.getPlain("Syntax")
						+ ":\n" + loc.getCommandSyntax(currentCommand));

	}

	public static void handleError(MyError e, String cmd, Localization loc,
			ErrorHandler handler) {
		if (e.getcommandName() != null) {
			handleCommandError(e.getcommandName(), handler, loc);
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

			public String getCurrentCommand() {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}


}
