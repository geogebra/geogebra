package org.geogebra.common.main.error;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;

public class ErrorHelper {
	public static void handleException(Exception e, App app,
			ErrorHandler handler) {
		e.printStackTrace();
		Localization loc = app.getLocalization();
		app.initTranslatedCommands();
		if (e instanceof CircularDefinitionException) {
			handler.showError(loc.getError("CircularDefinition"));
		} else if (e.getCause() instanceof MyError) {
			handleError((MyError) e.getCause(), null, loc, handler);
		} else if (loc.getCommandSyntax(
				loc.getReverseCommand(handler.getCurrentCommand())) != null) {

			handleCommandError(handler.getCurrentCommand(), handler, loc);
		} else {
			handler.showError(loc.getError("InvalidInput"));
		}

	}

	private static void handleCommandError(String currentCommand,
			ErrorHandler handler, Localization loc) {
		String cmd = loc.getReverseCommand(currentCommand);
		handler.showCommandError(cmd,
				loc.getError("InvalidInput") + "\n\n" + loc.getPlain("Syntax")
						+ ":\n" + loc.getCommandSyntax(cmd));

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
