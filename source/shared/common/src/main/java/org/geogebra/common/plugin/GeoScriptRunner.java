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

import org.geogebra.common.main.App;
import org.geogebra.common.plugin.script.Script;

/**
 * Runs GGB Scripts
 */
public class GeoScriptRunner implements EventListener {

	private App app;
	private boolean eventsDuringClick;

	/**
	 * @param app
	 *            application
	 */
	public GeoScriptRunner(App app) {
		this.app = app;
	}

	@Override
	public void sendEvent(Event evt) {
		if (app.isScriptingDisabled() || evt.target == null) {
			return;
		}

		eventsDuringClick = eventsDuringClick || needsStoringUndo(evt.type);
		Script script = evt.target.getScript(evt.type);
		if (script == null) {
			return;
		}

		if (app.getEventDispatcher().isDisabled(script.getType())) {
			return;
		}

		if (evt.type != EventType.CLICK) {
			if (app.isBlockUpdateScripts() && !evt.isAlwaysDispatched()) {
				return;
			}
		}
		try {
			if (evt.type == EventType.CLICK) {
				if (evt.isAlwaysDispatched()) {
					script.run(evt);
				} else {
					handleClick(script, evt);
				}
			} else {
				app.setBlockUpdateScripts(true);
				boolean ok = script.run(evt);
				app.setBlockUpdateScripts(!ok);
			}
		} catch (ScriptError e) {
			app.showError(e.getScriptError());
		}
	}

	/* Some scripts (especially JS) may have no impact on construction and should not
	trigger storing undo. Here we check that something significant happened in script.*/
	private boolean needsStoringUndo(EventType type) {
		return type == EventType.UPDATE || type == EventType.ADD
				|| type == EventType.REMOVE || type == EventType.UPDATE_STYLE;
	}

	private void handleClick(Script script, Event evt) throws ScriptError {
		eventsDuringClick = false;
		script.run(evt);
		if (eventsDuringClick) {
			app.storeUndoInfo();
		}
	}
}
