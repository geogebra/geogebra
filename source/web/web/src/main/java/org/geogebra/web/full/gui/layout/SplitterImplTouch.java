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

package org.geogebra.web.full.gui.layout;

import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.EventTarget;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Overflow;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.layout.client.Layout.Layer;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;

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
		switch (DOM.eventGetType(event)) {
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
		baseDivElement.removeClassName("splitPaneDragger");
		mainDivElement.addClassName("gwt-SplitLayoutPanel-HDragger splitPaneDragger");
		Style mainDivStyle = mainDivElement.getStyle();
		mainDivStyle.setPropertyPx("width", splitterSize);
		mainDivStyle.setProperty("height", "100%");
		mainDivStyle.setProperty("position", "absolute");
		mainDivStyle.setLeft(15, Unit.PX);

		Style glassDivStyle = glassDivElement.getStyle();
		glassDivStyle.setProperty("width", (splitterSize + GLASS_SIZE) + "px");
		glassDivStyle.setProperty("height", "100%");
		glassDivStyle.setProperty("position", "absolute");
		glassDivStyle.setProperty("left", (-(GLASS_SIZE / 2)) + "px");
		glassDivStyle.setZIndex(10);
	}

	@Override
	public void setToVertical(int splitterSize) {
		baseDivElement.removeClassName("splitPaneDragger");
		mainDivElement.addClassName("gwt-SplitLayoutPanel-VDragger splitPaneDragger");
		Style mainDivStyle = mainDivElement.getStyle();
		mainDivStyle.setPropertyPx("height", splitterSize);
		mainDivStyle.setProperty("width", "100%");
		mainDivStyle.setProperty("position", "absolute");
		mainDivStyle.setTop(15, Unit.PX);

		Style glassDivStyle = glassDivElement.getStyle();
		glassDivStyle.setProperty("height", (splitterSize + GLASS_SIZE) + "px");
		glassDivStyle.setProperty("width", "100%");
		glassDivStyle.setProperty("position", "absolute");
		glassDivStyle.setProperty("top", (-(GLASS_SIZE / 2)) + "px");
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
