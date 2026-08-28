/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.util.debug;

import java.util.HashMap;
import java.util.Map;

/**
 * Logs accessibility events with the parameters expected by analytics.
 */
public final class AccessibilityAnalytics {

	private AccessibilityAnalytics() {
	}

	/**
	 * Logs that the login button was clicked.
	 * @param trigger button location
	 */
	public static void logLoginClicked(String trigger) {
		logEvent(Event.LOGIN_CLICKED, trigger, null, null, Value.DIRECT, null);
	}

	/**
	 * Logs that the login dialog was shown.
	 * @param trigger button location
	 * @param flow flow that opened the dialog
	 */
	public static void logLoginShown(String trigger, String flow) {
		logEvent(Event.LOGIN_SHOWN, trigger, null, null, flow, null);
	}

	/**
	 * Logs that login finished successfully.
	 * @param trigger button location
	 * @param flow flow that opened the dialog
	 */
	public static void logLoginCompleted(String trigger, String flow) {
		logEvent(Event.LOGIN_COMPLETED, trigger, null, null, flow, null);
	}

	/**
	 * Logs that the save dialog was shown.
	 * @param trigger button location
	 * @param flow flow that opened the dialog
	 */
	public static void logSaveShown(String trigger, String flow) {
		logEvent(Event.SAVE_SHOWN, trigger, null, null, flow, null);
	}

	/**
	 * Logs that saving completed.
	 * @param trigger button location
	 * @param flow flow that opened the save
	 */
	public static void logSaveCompleted(String trigger, String flow) {
		logEvent(Event.SAVE_COMPLETED, trigger, null, null, flow, null);
	}

	/**
	 * Logs that the share button was clicked.
	 * @param fromHeader whether the header button triggered the flow
	 * @param loggedIn whether the user was logged in
	 * @param saved whether the current material was saved
	 */
	public static void logShareClicked(boolean fromHeader, boolean loggedIn, boolean saved) {
		logEvent(Event.SHARE_CLICKED, fromHeader ? Value.HEADER : Value.BURGER_MENU,
				loggedIn ? Value.LOGGED_IN : Value.LOGGED_OUT,
				saved ? Value.SAVED : Value.UNSAVED, Value.SHARE, null);
	}

	/**
	 * Logs that the share dialog was shown.
	 * @param trigger button location
	 */
	public static void logShareDialogShown(String trigger) {
		logEvent(Event.SHARE_DIALOG_SHOWN, trigger, null, null, Value.SHARE, null);
	}

	/**
	 * Logs that a share action completed.
	 * @param trigger button location
	 * @param action selected share action
	 */
	public static void logShareCompleted(String trigger, String action) {
		logEvent(Event.SHARE_COMPLETED, trigger, null, null, Value.SHARE, action);
	}

	/**
	 * Logs that the assign button was clicked.
	 * @param loggedIn whether the user was logged in
	 * @param saved whether the current material was saved
	 */
	public static void logAssignClicked(boolean loggedIn, boolean saved) {
		logEvent(Event.ASSIGN_CLICKED, Value.HEADER,
				loggedIn ? Value.LOGGED_IN : Value.LOGGED_OUT,
				saved ? Value.SAVED : Value.UNSAVED, Value.ASSIGN, null);
	}

	/**
	 * Logs that the assign dialog was shown.
	 */
	public static void logAssignDialogShown() {
		logEvent(Event.ASSIGN_DIALOG_SHOWN, Value.HEADER, null, null, Value.ASSIGN, null);
	}

	/**
	 * Logs that an assign action completed.
	 * @param action selected assign action
	 */
	public static void logAssignCompleted(String action) {
		logEvent(Event.ASSIGN_COMPLETED, Value.HEADER, null, null, Value.ASSIGN, action);
	}

	/**
	 * Logs that the profile menu was opened.
	 */
	public static void logProfileClicked() {
		logEvent(Event.PROFILE_CLICKED, Value.HEADER, null, null, Value.DIRECT, null);
	}

	/**
	 * Logs that a profile menu action was selected.
	 * @param action selected profile action
	 */
	public static void logProfileAction(String action) {
		logEvent(Event.PROFILE_ACTION, Value.HEADER, null, null, Value.DIRECT, action);
	}

	private static void logEvent(String event, String trigger, String userState,
			String materialState, String flow, String action) {
		Map<String, Object> params = new HashMap<>();
		addParam(params, Param.TRIGGER, trigger);
		addParam(params, Param.USER_STATE, userState);
		addParam(params, Param.MATERIAL_STATE, materialState);
		addParam(params, Param.FLOW, flow);
		addParam(params, Param.ACTION, action);
		Analytics.logEvent(event, params);
	}

	private static void addParam(Map<String, Object> params, String key, String value) {
		if (value != null) {
			params.put(key, value);
		}
	}

	/**
	 * Accessibility analytics event names.
	 */
	public static final class Event {
		public static final String LOGIN_CLICKED = "login_clicked";
		public static final String LOGIN_SHOWN = "login_shown";
		public static final String LOGIN_COMPLETED = "login_completed";

		public static final String SAVE_SHOWN = "save_shown";
		public static final String SAVE_COMPLETED = "save_completed";

		public static final String SHARE_CLICKED = "share_clicked";
		public static final String SHARE_DIALOG_SHOWN = "share_dialog_shown";
		public static final String SHARE_COMPLETED = "share_completed";

		public static final String ASSIGN_CLICKED = "assign_clicked";
		public static final String ASSIGN_DIALOG_SHOWN = "assign_dialog_shown";
		public static final String ASSIGN_COMPLETED = "assign_completed";

		public static final String PROFILE_CLICKED = "profile_clicked";
		public static final String PROFILE_ACTION = "profile_action";

		private Event() {
		}
	}

	/**
	 * Accessibility analytics parameter names.
	 */
	public static final class Param {
		public static final String TRIGGER = "trigger";
		public static final String USER_STATE = "user_state";
		public static final String MATERIAL_STATE = "material_state";
		public static final String FLOW = "flow";
		public static final String ACTION = "action";

		private Param() {
		}
	}

	/**
	 * Accessibility analytics parameter values.
	 */
	public static final class Value {
		public static final String HEADER = "header";
		public static final String BURGER_MENU = "burger_menu";

		public static final String LOGGED_IN = "logged_in";
		public static final String LOGGED_OUT = "logged_out";

		public static final String SAVED = "saved";
		public static final String UNSAVED = "unsaved";

		public static final String SHARE = "share";
		public static final String ASSIGN = "assign";
		public static final String DIRECT = "direct";
		public static final String UNSET = "unset";

		public static final String COPY = "copy";
		public static final String PRINT = "print";
		public static final String EXPORT_IMAGE = "export_image";
		public static final String EMBED = "embed";
		public static final String GEOGEBRA_CLASSROOM = "geogebra_classroom";
		public static final String GOOGLE_CLASSROOM = "google_classroom";
		public static final String PROFILE = "profile";
		public static final String ACCOUNT_SETTINGS = "account_settings";
		public static final String SIGN_OUT = "sign_out";

		private Value() {
		}
	}
}
