package org.geogebra.common.util.debug;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.geogebra.common.GeoGebraConstants;

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

		protected Event() {
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
		public static final String SEARCH_TERM = "search_term";
		public static final String TOOL_NAME = "tool_name";

		/**
		 * Convert sub app code to analyitcs sub app parameter
		 * @param subAppName sub app name
		 * @return sub app parameter
		 */
		public static String convertToSubAppParam(String subAppName) {
			switch (subAppName) {
			case GeoGebraConstants.GEOMETRY_APPCODE:
				return SUB_APP_GEOMETRY;
			case GeoGebraConstants.CAS_APPCODE:
				return SUB_APP_CAS;
			case GeoGebraConstants.G3D_APPCODE:
				return SUB_APP_3D;
			case GeoGebraConstants.PROBABILITY_APPCODE:
				return SUB_APP_PROBABILITY;
			case GeoGebraConstants.GRAPHING_APPCODE:
			default:
				return SUB_APP_GRAPHING;
			}
		}

		protected Param() {
		}
	}
}
