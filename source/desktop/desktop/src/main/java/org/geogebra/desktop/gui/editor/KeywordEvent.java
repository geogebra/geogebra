/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.
This code has been written initially for Scilab (http://www.scilab.org/).

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.editor;

import java.util.EventObject;

/**
 * Used to handle an event generated on a keyword
 * 
 * @author Calixte DENIZET
 */
public class KeywordEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private int start;
	private int length;
	private int type;
	private EventObject event;

	/**
	 * Constructor
	 * 
	 * @param source
	 *            the Object where the event occurred
	 * @param event
	 *            the MouseEvent which generated this event
	 * @param type
	 *            the type of the keyword
	 * @param start
	 *            the position of the keyword in the doc
	 * @param length
	 *            the length of the keyword
	 */
	public KeywordEvent(Object source, EventObject event, int type, int start,
			int length) {
		super(source);
		this.start = start;
		this.length = length;
		this.type = type;
		this.event = event;
	}

	/**
	 * @return the position of the keyword in the doc
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return the length of the keyword
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the type of the keyword
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the event which generated this event
	 */
	public EventObject getEvent() {
		return event;
	}
}
