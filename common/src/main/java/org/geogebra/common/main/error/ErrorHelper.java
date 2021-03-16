package org.geogebra.common.main.error;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

/**
 * Helper methods for converting throwables into human readable messages
 */
public class ErrorHelper {
	/**
	 * Converts exception to string and sends it to error handler
	 * 
	 * @param e
	 *            exception
	 * @param app
	 *            application
	 * @param handler
	 *            handler
	 */
	public static void handleException(Exception e, App app,
			ErrorHandler handler) {
		if (e instanceof ParseException) {
			Log.error(e.getMessage());
		} else {
			Log.debug(e);
		}
		
		if (handler == null) {
			return;
		}
		
		if (handler instanceof ErrorLogger) {
			((ErrorLogger) handler).log(e);
		}
		Localization loc = app.getLocalization();
		
		if (loc == null) {
			handler.showError("Sorry, something went wrong:" + e.getMessage());
			return;
		}
		
		app.initTranslatedCommands();
		if (e instanceof CircularDefinitionException) {
			handler.showError(Errors.CircularDefinition.getError(loc));
		} else if (e.getCause() instanceof MyError) {
			handleError((MyError) e.getCause(), null, loc, handler);
		} else if (loc.getReverseCommand(handler.getCurrentCommand()) != null) {
			handleCommandError(loc, handler.getCurrentCommand(), handler);
		} else {
			handler.showError(loc.getInvalidInputError());
		}

	}

	/**
	 * @param loc
	 *            localization
	 * @param localCommand
	 *            localized command name
	 * @param handler
	 *            error handler
	 */
	public static void handleCommandError(Localization loc, String localCommand,
			ErrorHandler handler) {
		String cmd = loc.getReverseCommand(localCommand);
		handler.showCommandError(cmd, loc.getInvalidInputError()
						+ ":\n"
						+ localCommand + "\n\n"
						+ loc.getMenu("Syntax") + ":\n"
						+ loc.getCommandSyntax(cmd));

	}

	/**
	 * Forwards error to error handler
	 * 
	 * @param e
	 *            error
	 * @param cmd
	 *            input
	 * @param loc
	 *            localization
	 * @param handler
	 *            handler
	 */
	public static void handleError(MyError e, String cmd, Localization loc,
			ErrorHandler handler) {
		if (handler instanceof ErrorLogger) {
			((ErrorLogger) handler).log(e);
		} else {
			e.printStackTrace();
		}

		if (e.getcommandName() != null) {
			String internal = loc
					.getReverseCommand(e.getcommandName());
			handler.showCommandError(internal, e.getLocalizedMessage());
		} else {
			handler.showError(e.getLocalizedMessage());
		}
	}

	/**
	 * @param str
	 *            input
	 * @param loc
	 *            localization
	 * @param handler
	 *            error handler
	 */
	public static void handleInvalidInput(String str, Localization loc,
			ErrorHandler handler) {
		if (loc != null) {
			handler.showError(loc.getInvalidInputError() + ":\n" + str);
		} else {
			handler.showError("Please check your input:\n" + str);
		}
	}

	/**
	 * @return instance of ErrorHandler that ignores all errors
	 */
	public static ErrorHandler silent() {
		return new ErrorHandler() {

			@Override
			public void showError(String msg) {
				Log.trace(msg);

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
