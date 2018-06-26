package org.geogebra.web.full.gui.layout;

import org.geogebra.web.full.gui.layout.ZoomSplitLayoutPanel.Splitter;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.Event;

public class SplitterImpl {

	protected Element baseDivElement;

	public Element createElement(Splitter splitter) {
		return (baseDivElement = Document.get().createDivElement());
	}

	public boolean shouldHandleEvent(Event event, boolean mouseDown) {
		return true;
	}

	/**
	 * @param splitterSize
	 *            splitter width
	 */
	public void setToHorizontal(int splitterSize) {
		baseDivElement.getStyle().setPropertyPx("width", splitterSize);
		baseDivElement.addClassName("gwt-SplitLayoutPanel-HDragger");
	}

	/**
	 * @param splitterSize
	 *            splitter height
	 */
	public void setToVertical(int splitterSize) {
		baseDivElement.getStyle().setPropertyPx("height", splitterSize);
		baseDivElement.addClassName("gwt-SplitLayoutPanel-VDragger");
	}
	
	public Element getSplitterElement() {
		return baseDivElement;
	}
	
	/**
	 * @param layer
	 *            parent layer
	 */
	public void splitterInsertedIntoLayer(Layer layer) {
		// overridden in touch
	}
}
