package org.geogebra.common.main.undo;

import org.geogebra.common.plugin.EventType;

public interface ActionExecutor {

	/**
	 * Replay an action if applicable
	 *
	 * @param action
	 *            action type
	 * @param state
	 *            state to restore
	 * @param args
	 *            action parameters
	 * @return whether action was handled by this executor
	 */
	boolean executeAction(EventType action, AppState state, String... args);

	boolean undoAction(EventType action, String... args);
}
