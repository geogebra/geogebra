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
