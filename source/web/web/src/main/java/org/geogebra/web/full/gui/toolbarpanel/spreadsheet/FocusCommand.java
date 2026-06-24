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

package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.gwtproject.core.client.Scheduler;

import elemental2.dom.Element;

public class FocusCommand implements Scheduler.ScheduledCommand {
	private final Element element;
	private final Element.FocusOptionsType focusOptionsType;
	private boolean canceled;

	/**
	 * Constructor
	 * @param element The element which should be focused when this FocusCommand is executed.
	 */
	public FocusCommand(Element element) {
		this.element = element;
		this.focusOptionsType = Element.FocusOptionsType.create();
		focusOptionsType.setPreventScroll(true);
	}

	@Override
	public void execute() {
		if (!canceled) {
			element.focus(focusOptionsType);
		}
	}

	/**
	 * Cancel this command.
	 */
	public void cancel() {
		canceled = true;
	}
}
