package com.himamis.retex.editor.web;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

public class MyTextArea extends TextArea {

	/**
	 * @param element
	 *            wrapped element
	 */
	public MyTextArea(Element element) {
		super(element);
	}

	/**
	 * Factory method
	 * 
	 * @param element
	 *            textarea element
	 * @return textarea widget
	 */
	public static MyTextArea wrap(Element element) {
		// Assert that the element is attached.
		assert Document.get().getBody().isOrHasChild(element);

		MyTextArea textArea = new MyTextArea(element);

		// Mark it attached and remember it for cleanup.
		textArea.onAttach();
		RootPanel.detachOnWindowClose(textArea);

		return textArea;
	}

	/**
	 * @param handler
	 *            composition event handler
	 */
	public void addCompositionUpdateHandler(EditorCompositionHandler handler) {
		addDomHandler(handler, CompositionUpdateEvent.getType());
	}

	public void addCompositionEndHandler(EditorCompositionHandler handler) {
		addDomHandler(handler, CompositionEndEvent.getType());
	}
}
