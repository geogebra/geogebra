package com.himamis.retex.editor.web;

import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.RootPanel;
import org.gwtproject.user.client.ui.TextArea;
import org.gwtproject.user.client.ui.TextBoxBase;

public class MyTextArea extends TextBoxBase {

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
