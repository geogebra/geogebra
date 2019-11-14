/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * MyError.java
 *
 * Created on 04. Oktober 2001, 09:29
 */

package org.geogebra.common.main;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 * 
 * @author Markus
 */
public class MyError extends Error {

	private static final StringTemplate errorTemplate = StringTemplate.defaultTemplate;
	private static final long serialVersionUID = 1L;
	/** application */
	protected Localization loc;
	private String[] strs;
	private String commandName = null;
	private Errors message;

	/**
	 * Creates new MyError
	 * 
	 * @param loc0
	 *            localization
	 * @param errorName
	 *            error name (should be a key in error.properties)
	 */
	public MyError(Localization loc0, String errorName) {
		// set localized message
		super(errorName);
		this.loc = loc0;
	}

	/**
	 * @param loc0
	 *            localization
	 * @param errorName
	 *            error name (should be a key in error.properties)
	 * @param commandName
	 *            associated command name
	 * @param cause
	 *            cause
	 * @return command error
	 */
	public static MyError forCommand(Localization loc0, String errorName,
			String commandName,
			Throwable cause) {
		// set localized message
		MyError ret = new MyError(errorName, cause);
		ret.loc = loc0;
		ret.commandName = commandName;
		return ret;
	}

	/**
	 * @param loc0
	 *            localization
	 * @param message
	 *            primary message
	 * @param strs
	 *            parts of the error (space separated)
	 */
	public MyError(Localization loc0, String message, String... strs) {
		super(message);
		this.loc = loc0;
		// set localized message
		this.strs = strs;
	}

	/**
	 * @param loc0
	 *            localization
	 * @param message0
	 *            primary message
	 * @param strs0
	 *            parts of the error (space separated)
	 */
	public MyError(Localization loc0, Errors message0, String... strs0) {
		super(message0.key);
		this.loc = loc0;
		this.message = message0;
		this.strs = strs0;
	}

	/**
	 * @param loc0
	 *            localization
	 * @param message0
	 *            message
	 * @param lt
	 *            left expression
	 * @param opname
	 *            operation
	 * @param rt
	 *            right expression
	 */
	public MyError(Localization loc0, Errors message0, ExpressionValue lt, String opname,
			ExpressionValue rt) {
		super(message0.key);
		this.loc = loc0;
		this.message = message0;

		strs = new String[3];
		strs[0] = toErrorString(lt);
		strs[1] = String.valueOf(opname); // handles null
		strs[2] = toErrorString(rt);
	}

	/**
	 * @param ev expression
	 * @return expression as string or "null"
	 */
	public static String toErrorString(ExpressionValue ev) {
		if (ev == null) {
			return "null";
		}
		return ev.toString(errorTemplate);
	}

	/**
	 * @param errorName
	 *            translateable error name
	 * @param cause
	 *            cause
	 */
	public MyError(String errorName, Throwable cause) {
		super(errorName, cause);
	}

	/**
	 * @return associated command name
	 */
	public String getcommandName() {
		return commandName;
	}

	@Override
	public String getLocalizedMessage() {
		StringBuilder sb = new StringBuilder();
		// space needed in case error is displayed on one line
		sb.append(getError());

		// only needed for old "string" errors, not new enum errors
		if (message == null && strs != null) {
			sb.append(" \n");
			for (String part : strs) {
				sb.append(part);
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	private String getError() {
		
		// using new Errors enum
		if (message != null) {
			String ret = message.getError(loc, strs);
			return ret;
		}
		
		// using old string method
		return getError(getMessage());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getClass().toString());
		sb.append(": ");
		sb.append(getError());
		if (strs != null) {
			for (int i = 0; i < strs.length; i++) {
				sb.append(" : ");
				sb.append(getError(strs[i]));
			}
		}
		return sb.toString();
	}

	/**
	 * @param s
	 *            key
	 * @return localized error
	 */
	private String getError(String s) {
		String ret = loc == null ? s : loc.getError(s);
		return ret;
	}

	/**
	 * 
	 * Errors and default translations
	 * 
	 * (defaults needed eg in webSimple)
	 *
	 */
	public enum Errors {

		FrameLoadError("FrameLoadError", "This web page does not support embedding."),

		CASGeneralErrorMessage("CAS.GeneralErrorMessage",
				"Sorry, something went wrong. Please check your input"),

		CASInvalidReferenceError("CAS.InvalidReferenceError", "One or more references are invalid"),

		CASSelectionStructureError("CAS.SelectionStructureError",
				"Please check the structure of your selection"),

		CASTimeoutError("CAS.TimeoutError", "Calculation took too long and was aborted"),

		CASVariableIsDynamicReference("CAS.VariableIsDynamicReference",
				"Attempt to assign dynamic reference"),

		UndefinedVariable("UndefinedVariable", "Undefined variable"),

		InvalidInput("InvalidInput", "Please check your input"),

		IllegalMultiplication("IllegalMultiplication", "Illegal multiplication"),

		IllegalAddition("IllegalAddition", "Illegal addition"),

		IllegalDivision("IllegalDivision", "Illegal division"),

		IllegalSubtraction("IllegalSubtraction", "Illegal subtraction"),

		IllegalExponent("IllegalExponent", "Illegal exponent"),

		IllegalArgument("IllegalArgument", "Illegal argument"),

		IllegalArgumentNumber("IllegalArgumentNumber", "Illegal number of arguments"),

		IllegalBoolean("IllegalBoolean", "Illegal Boolean operation"),

		IllegalComparison("IllegalComparison", "Illegal comparison"),

		IllegalListOperation("IllegalListOperation", "Illegal list operation"),

		IllegalAssignment("IllegalAssignment", "Illegal assignment"),

		UnbalancedBrackets("UnbalancedBrackets", "Unbalanced brackets"),

		ReplaceFailed("ReplaceFailed", "Redefinition failed"),
		
		CircularDefinition("CircularDefinition", "Circular Definition"),

		LoadFileFailed("LoadFileFailed", "Opening file failed"),

		NotAuthorized("NotAuthorized", "Not authorized"),

		SaveFileFailed("SaveFileFailed", "Saving file failed"),

		LoggingError("LoggingError", "Problem starting logging"),

		ToolCreationFailed("Tool.CreationFailed", "Tool could not be created"),

		ToolDeleteUsed("Tool.DeleteUsed",
				"Following tools were used to create selected objects and cannot be deleted:"),

		DeleteFailed("DeleteFailed", "Delete failed"),

		AssignmentToFixed("AssignmentToFixed", "Fixed objects may not be changed"),

		RenameFailed("RenameFailed", "Rename failed"),

		PasteImageFailed("PasteImageFailed", "Sorry - couldn't paste bitmap from the clipboard"),

		NumberExpected("NumberExpected", "Number expected"),

		FunctionExpected("FunctionExpected", "Function expected"),

		InvalidFunction("InvalidFunction",
				"Invalid function:\nPlease enter an explicit function in x"),

		// IllegalArgumentAinCustomToolB("IllegalArgumentAinCustomToolB", "Illegal
		// Argument %0 in Custom Tool %1"),

		InvalidFunctionA("InvalidFunctionA",
				"Invalid function:\nPlease enter an explicit function in %0") {

			@Override
			public String getError(Localization loc, String... strs) {
				String ret = null;
				if (loc != null) {
					ret = loc.getPlain(key, strs.length > 0 ? strs[0] : "x");
				}

				if (ret == null || ret.startsWith(key)) {
					if (strs.length > 0) {
						ret = defaultTranslation.replace("%0", strs[0]);
					} else {
						ret = InvalidFunction.defaultTranslation();
					}
				}

				return ret;
			}
		},

		CellAisNotDefined("CellAisNotDefined", "Cell %0 is not defined") {
			@Override
			public String getError(Localization loc, String... strs) {
				String ret = null;
				if (loc != null) {
					ret = loc.getPlain(key, strs.length > 0 ? strs[0] : "x");
				}

				if (ret == null || ret.startsWith(key)) {
					if (strs.length > 0) {
						ret = defaultTranslation.replace("%0", strs[0]);
					} else {
						ret = InvalidInput.defaultTranslation();
					}
				}

				return ret;
			}

		};

		String key;
		String defaultTranslation;

		Errors(String key0, String default0) {
			key = key0;
			defaultTranslation = default0;
		}

		/**
		 * @return default translation
		 */
		protected String defaultTranslation() {
			return defaultTranslation;
		}

		/**
		 * Builds error message from current key and given arguments
		 * 
		 * @param loc
		 *            localization
		 * @param strs
		 *            arguments
		 * @return error message
		 */
		public String getError(Localization loc, String... strs) {
			StringBuilder sb = new StringBuilder();
			if (loc != null) {
				sb.append(loc.getError(key));
			}

			if (sb.length() == 0 || sb.toString().equals(key)) {
				sb.setLength(0);
				sb.append(defaultTranslation);

			}

			if (strs != null && strs.length > 0) {
				// space in case \n removed for one-line display
				sb.append(" \n");
				for (String part : strs) {
					sb.append(removeNull(part));
					sb.append(" ");
				}
			}
			
			return sb.toString();
		}

		/**
		 * remove null: as label eg 3/(x^2=1) gives Illegal division 3 / null:
		 * (-x - 1) (-x + 1) = 0
		 * 
		 * if label really is null, doesn't matter if removed
		 * 
		 * @param s
		 *            input
		 * @return input with "null:" / "undefined:" removed
		 */
		private static String removeNull(String s) {
			if (s != null && s.startsWith("null:")) {
				return s.substring("null:".length());
			}
			// for web
			if (s != null && s.startsWith("undefined:")) {
				return s.substring("undefined:".length());
			}
			return s;
		}

		/**
		 * 
		 * @return ggbtrans translation key eg "LoadFileFailed"
		 */
		public String getKey() {
			return key;
		}

	}

}
