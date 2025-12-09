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

package org.geogebra.desktop.euclidian.event;

import java.awt.event.FocusEvent;

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.event.FocusListenerDelegate;

/**
 * @author judit
 *
 */
public class FocusListenerD implements java.awt.event.FocusListener {
	private FocusListenerDelegate delegate;

	public FocusListenerD(@Nonnull FocusListenerDelegate listener) {
		delegate = listener;
	}

	@Override
	public void focusGained(FocusEvent e) {
		delegate.focusGained();
	}

	@Override
	public void focusLost(FocusEvent e) {
		delegate.focusLost();
	}

}
