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
import org.gwtproject.layout.client.Layout.Layer;
import org.gwtproject.user.client.Event;

public class SplitterImpl {

	protected Element baseDivElement;

	/**
	 * Set up elements and return the base.
	 * @return base element
	 */
	public Element createElement() {
		return baseDivElement = Document.get().createDivElement();
	}

	/**
	 * @param event
	 *            pointer event
	 * @param mouseDown
	 *            whether pointer is currently down
	 * @return whether event should be handled
	 */
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
