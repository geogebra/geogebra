package org.geogebra.common.util.debug;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.geogebra.common.SuiteSubApp;

/** Subclass this and set the instance to use it for logging analytics events. */
public abstract class Analytics {
	private static Analytics INSTANCE = null;

	/** Set the Analytics instance to log events */
	public static void setInstance(Analytics analytics) {
		INSTANCE = analytics;
	}

	/**
	 * Logs an analytics event, if the instance is already set.
	 * @param name event name
	 */
	public static void logEvent(String name) {
		logEvent(name, null);
	}

	/**
	 * Logs an analytics event, if the instance is already set.
	 * @param name event name
	 * @param param parameter name
	 * @param value parameter value
	 */
	public static void logEvent(String name, String param, Object value) {
		Map<String, Object> params = new HashMap<>();
		params.put(param, value);
		logEvent(name, params);
	}

	/**
	 * Logs an analytics event, if the instance is already set.
	 * @param name event name
	 * @param params parameters
	 */
	public static void logEvent(String name, @Nullable Map<String, Object> params) {
		if (INSTANCE != null) {
			INSTANCE.recordEvent(name, params);
		} else {
			Log.trace("Analytics is not set, event with name '" + name + "' cannot be recored");
		}
	}

	/**
	 * Log the analytics event, and optionally send it to a remote device.
	 * @param name event name
	 * @param params event parameters
	 */
	protected abstract void recordEvent(String name, @Nullable Map<String, Object> params);

	/**
	 * Analytics events.
	 */
	public static class Event {
		public static final String COMMAND_ERROR = "command_error";
		public static final String COMMAND_VALIDATED = "command_validated";
		public static final String COMMAND_HELP_ICON = "command_help_icon";
		public static final String EXAM_MODE_INITIATED = "exam_mode_initiated";
		public static final String APP_SWITCHED = "switch_app";
		public static final String LOGIN = "login";
		public static final String SEARCH = "search";
		public static final String TOOL_SELECTED = "tool_selected";
		public static final String KEYBOARD = "keyboard";
		public static final String INSERT_IMAGE = "insert_image";

		private Event() {
		}
	}

	/**
	 * Parameters to the analytics events.
	 */
	public static class Param {
		public static final String COMMAND = "command";
		public static final String AV_INPUT = "av_input";
		public static final String ERROR_TYPE = "error_type";
		public static final String STATUS = "status";
		public static final String OBJECT_CREATION = "object_creation";
		public static final String REDEFINED = "redefined";
		public static final String NEW = "new";
		public static final String OK = "ok";
		public static final String ERROR = "error";
		public static final String SUB_APP = "sub_app";
		public static final String SUB_APP_GRAPHING = "graphing";
		public static final String SUB_APP_GEOMETRY = "geometry";
		public static final String SUB_APP_CAS = "CAS";
		public static final String SUB_APP_3D = "3D";
		public static final String SUB_APP_PROBABILITY = "probability";
		public static final String SUB_APP_SCIENTIFIC_CALCULATOR = "sciCalc";
		public static final String SEARCH_TERM = "search_term";
		public static final String TOOL_NAME = "tool_name";
		public static final String KEY = "key";
		public static final String TAB = "tab";
		public static final String INPUT_SOURCE = "input_source";

		/**
		 * Convert sub app code to analyitcs sub app parameter
		 * @param subAppName sub app name
		 * @return sub app parameter
		 */
		public static String convertToSubAppParam(SuiteSubApp subAppName) {
			switch (subAppName) {
			case GEOMETRY:
				return SUB_APP_GEOMETRY;
			case CAS:
				return SUB_APP_CAS;
			case G3D:
				return SUB_APP_3D;
			case PROBABILITY:
				return SUB_APP_PROBABILITY;
			case SCIENTIFIC:
				return SUB_APP_SCIENTIFIC_CALCULATOR;
			case GRAPHING:
			default:
				return SUB_APP_GRAPHING;
			}
		}

		private Param() {
		}
	}

	public static class Keyboard {
		public static final String ABC = "ABC";
		public static final String FUNCTIONS = "f(x)";
		public static final String GREEK = "Greek";
		public static final String NUMBERS = "123";
		public static final String SYMBOLS = "symbols";
	}

	public enum InputSource {
		ALGEBRA("Algebra"),
		DATA_TABLE("Data Table"),
		DISTRIBUTION("Distribution"),
		SETTINGS("Settings"),
		DIALOG("Dialog");

		private final String value;

		InputSource(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public static class ImageInputSource {
		public static final String GALLERY = "gallery";
		public static final String CAMERA = "camera";
	}
}
