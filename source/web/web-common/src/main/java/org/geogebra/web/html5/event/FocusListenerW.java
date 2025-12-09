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

package org.geogebra.web.html5.event;

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.event.FocusListenerDelegate;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.web.html5.gui.view.autocompletion.ScrollableSuggestBox;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.FocusEvent;
import org.gwtproject.event.dom.client.FocusHandler;

/**
 * @author judit
 * 
 */
public class FocusListenerW implements FocusHandler, BlurHandler {

	private FocusListenerDelegate delegate;
	private ScrollableSuggestBox textField;

	/**
	 * @param listener
	 *            delegate
	 */
	public FocusListenerW(@Nonnull FocusListenerDelegate listener,
			ScrollableSuggestBox textField) {
		this.delegate = listener;
		this.textField = textField;
	}

	/** dummy method */
	public void init() {
		// avoid warnings
	}

	@Override
	public void onFocus(FocusEvent event) {
		delegate.focusGained();
		ScreenReader.debug(textField.getElement().getAttribute("aria-label"));
	}

	@Override
	public void onBlur(BlurEvent event) {
		delegate.focusLost();
	}
}
