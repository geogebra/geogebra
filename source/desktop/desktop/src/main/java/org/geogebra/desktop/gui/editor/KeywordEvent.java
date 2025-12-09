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
// This code has been written initially for Scilab (http://www.scilab.org/).

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
