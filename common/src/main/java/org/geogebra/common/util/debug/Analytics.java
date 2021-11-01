package org.geogebra.common.util.debug;

import java.util.Map;

import javax.annotation.Nullable;

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
	 * @param params parameters
	 */
	public static void logEvent(String name, @Nullable Map<String, Object> params) {
		if (INSTANCE != null) {
			INSTANCE.recordEvent(name, params);
		} else {
			Log.debug("Analytics is not set, event with name '" + name + "' cannot be recored");
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

		protected Param() {
		}
	}
}
