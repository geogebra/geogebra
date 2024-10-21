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
