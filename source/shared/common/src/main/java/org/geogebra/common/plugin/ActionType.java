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

package org.geogebra.common.plugin;

/**
 * Types of actions that can be undone/redone
 * by an {@link org.geogebra.common.main.undo.ActionExecutor}.
 * Values overlap with {@link EventType}, which is for reporting only.
 */
public enum ActionType {
	// actions affecting page content
	REMOVE, ADD, UPDATE, SPLIT_STROKE, MERGE_STROKE, UPDATE_ORDERING,
	EMBEDDED_PRUNE_STATE_LIST, REDO, SET_CONTENT, UNDO,
	// actions affecting whole pages
	CLEAR_PAGE, REMOVE_PAGE, ADD_PAGE, PASTE_PAGE,
	MOVE_PAGE, RENAME_PAGE;

	/**
	 * @return whether this action changes individual elements rather than whole pages
	 */
	public boolean isGeoElementAction() {
		return compareTo(CLEAR_PAGE) < 0;
	}
}
