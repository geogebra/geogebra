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
