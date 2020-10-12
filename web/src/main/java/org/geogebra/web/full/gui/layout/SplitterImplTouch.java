package org.geogebra.web.full.gui.layout;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.Event;

/**
 * Override splitter behavior on touch devices: add glass dragging zone
 *
 */
public class SplitterImplTouch extends SplitterImpl {

	private Element mainDivElement;
	private Element glassDivElement;

	private static final int GLASS_SIZE = 30;

	@Override
	public Element createElement() {
		super.createElement();
		createElements();
		return baseDivElement;
	}

	private void createElements() {
		mainDivElement = Document.get().createDivElement();
		glassDivElement = Document.get().createDivElement();

		glassDivElement.appendChild(mainDivElement);
		baseDivElement.appendChild(glassDivElement);
	}

	@Override
	public boolean shouldHandleEvent(Event event, boolean mouseDown) {
		if (mouseDown) {
			return true;
		}
		switch (event.getTypeInt()) {
		case Event.ONMOUSEDOWN:
		case Event.ONMOUSEMOVE:
		case Event.ONMOUSEUP:
			EventTarget tg = event.getEventTarget();
			if (Element.is(tg) && (Element.as(tg) == glassDivElement)) {
				return false;
			}
			break;
		}
		return true;
	}

	@Override
	public void setToHorizontal(int splitterSize) {
		mainDivElement.addClassName("gwt-SplitLayoutPanel-HDragger");
		Style mainDivStyle = mainDivElement.getStyle();
		mainDivStyle.setPropertyPx("width", splitterSize);
		mainDivStyle.setProperty("height", "100%");
		mainDivStyle.setProperty("position", "absolute");
		mainDivStyle.setLeft(15, Unit.PX);

		Style glassDivStyle = glassDivElement.getStyle();
		glassDivStyle.setProperty("width", (splitterSize + GLASS_SIZE) + "px");
		glassDivStyle.setProperty("height", "100%");
		glassDivStyle.setProperty("position", "absolute");
		glassDivStyle.setProperty("left", (-((GLASS_SIZE) / 2)) + "px");
		glassDivStyle.setZIndex(10);
	}

	@Override
	public void setToVertical(int splitterSize) {
		mainDivElement.addClassName("gwt-SplitLayoutPanel-VDragger");
		Style mainDivStyle = mainDivElement.getStyle();
		mainDivStyle.setPropertyPx("height", splitterSize);
		mainDivStyle.setProperty("width", "100%");
		mainDivStyle.setProperty("position", "absolute");
		mainDivStyle.setTop(15, Unit.PX);

		Style glassDivStyle = glassDivElement.getStyle();
		glassDivStyle.setProperty("height", (splitterSize + GLASS_SIZE) + "px");
		glassDivStyle.setProperty("width", "100%");
		glassDivStyle.setProperty("position", "absolute");
		glassDivStyle.setProperty("top", (-((GLASS_SIZE) / 2)) + "px");
		glassDivStyle.setZIndex(10);
	}

	@Override
	public void splitterInsertedIntoLayer(Layer layer) {
		layer.getContainerElement().getStyle().setOverflow(Overflow.VISIBLE);
	}
	
	@Override
	public Element getSplitterElement() {
	    return mainDivElement;
	}
}
