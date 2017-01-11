package org.geogebra.common.main.error;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

public class ErrorHelper {
	public static void handleException(Exception e, App app,
			ErrorHandler handler) {
		if (e instanceof ParseException) {
			Log.error(e.getMessage());
		} else {
			e.printStackTrace();
		}
		Localization loc = app.getLocalization();
		app.initTranslatedCommands();
		if (e instanceof CircularDefinitionException) {
			handler.showError(loc.getError("CircularDefinition"));
		} else if (e.getCause() instanceof MyError) {
			handleError((MyError) e.getCause(), null, loc, handler);
		} else if (loc.getReverseCommand(handler.getCurrentCommand()) != null) {
			handleCommandError(loc, handler.getCurrentCommand(), handler);
		} else {
			Log.debug("NO COMMAND FOR ERROR" + handler.getCurrentCommand());
			handler.showError(loc.getError("InvalidInput"));
		}

	}

	public static void handleCommandError(Localization loc, String localCommand,
			ErrorHandler handler) {
		String cmd = loc.getReverseCommand(localCommand);
		handler.showCommandError(cmd,
				loc.getError("InvalidInput") + ":\n" + localCommand + "\n\n"
						+ loc.getPlain("Syntax") + ":\n"
						+ loc.getCommandSyntax(cmd));

	}

	public static void handleError(MyError e, String cmd, Localization loc,
			ErrorHandler handler) {
		if (e.getcommandName() != null) {
			String internal = loc
					.getReverseCommand(handler.getCurrentCommand());
			handler.showCommandError(internal, e.getLocalizedMessage());
		} else {
			handler.showError(e.getLocalizedMessage());
		}
	}

	public static void handleInvalidInput(String str, Localization loc,
			ErrorHandler handler) {
		if (loc != null) {
			handler.showError(loc.getError("InvalidInput") + ":\n" + str);
		} else {
			handler.showError("InvalidInput:\n" + str);
		}
	}

	public static ErrorHandler silent() {
		return new ErrorHandler() {

			@Override
			public void showError(String msg) {
				Log.printStacktrace(msg);

			}

			@Override
			public void resetError() {
				// do nothing
			}

			@Override
			public void showCommandError(String command, String message) {
				Log.warn(command + ":" + message);

			}

			@Override
			public String getCurrentCommand() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean onUndefinedVariables(String string,
					AsyncOperation<String[]> callback) {
				return false;
			}

		};
	}

}
