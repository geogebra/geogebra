package com.himamis.retex.editor.web;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

public class MyTextArea extends TextArea {

	public MyTextArea(Element element) {
		super(element);
	}

	public static MyTextArea wrap(Element element) {
		// Assert that the element is attached.
		assert Document.get().getBody().isOrHasChild(element);

		MyTextArea textArea = new MyTextArea(element);

		// Mark it attached and remember it for cleanup.
		textArea.onAttach();
		RootPanel.detachOnWindowClose(textArea);

		return textArea;
	}

	public HandlerRegistration addCompositionUpdateHandler(
			CompositionHandler handler) {
		return addDomHandler(handler, CompositionEvent.getType());

	}
}
