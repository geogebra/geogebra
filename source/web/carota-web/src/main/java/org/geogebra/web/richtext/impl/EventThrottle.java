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

package org.geogebra.web.richtext.impl;

import org.geogebra.web.richtext.EditorChangeListener;
import org.gwtproject.timer.client.Timer;

import elemental2.core.Global;

public class EventThrottle {

	private final HasContent editor;
	private Timer updateTimer;

	public EventThrottle(HasContent editor) {
		this.editor = editor;
	}

	/**
	 * Pass content events with a slight debounce delay and selection events immediately
	 * @param listener content and selection listener
	 */
	public void setListener(EditorChangeListener listener) {
		updateTimer = new Timer() {
			@Override
			public void run() {
				listener.onContentChanged(Global.JSON.stringify(editor.save()));
			}
		};

		editor.contentChanged(() -> {
			listener.onInput();
			updateTimer.cancel();
			updateTimer.schedule(500);
		});

		editor.selectionChanged(listener::onSelectionChanged);

		editor.onEscape(listener::onEscape);
	}

}
